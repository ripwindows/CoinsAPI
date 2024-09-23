package me.ripwindows.coinsapi.api.database.impl;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.ripwindows.coinsapi.CoinsAPI;
import me.ripwindows.coinsapi.api.User;
import me.ripwindows.coinsapi.api.database.Database;
import me.ripwindows.coinsapi.api.database.PlayerTable;
import me.ripwindows.coinsapi.utils.queries.Query;
import me.ripwindows.coinsapi.utils.tables.Table;
import me.ripwindows.coinsapi.utils.AsyncInterface;
import org.bukkit.configuration.Configuration;

import java.sql.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.logging.Level;

public final class SQLDatabase extends AsyncInterface implements Database {
    private final HashMap<UUID, User> stats = new HashMap<>();

    private final HikariConfig dataSourceConfig = new HikariConfig();
    private HikariDataSource dataSource;

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public Type type() {
        return Type.SQL;
    }

    @Override
    public void initialize() {
        Configuration config = CoinsAPI.getInstance().getSettingsConfig().getConfig();
        try {
            dataSourceConfig.setPoolName("stats");
            dataSourceConfig.setMaximumPoolSize(3);
            dataSourceConfig.setIdleTimeout(0);
            dataSourceConfig.setUsername(config.getString("sql.username"));
            dataSourceConfig.setPassword(config.getString("sql.password"));
            dataSourceConfig.setJdbcUrl("jdbc:mysql://" + config.getString("sql.host") + ":" + config.getInt("sql.port") + "/" + config.getString("sql.database") + config.getString("sql.database-properties"));
            dataSource = new HikariDataSource(dataSourceConfig);
            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement()) {
                Table table = new PlayerTable("player_table");
                statement.addBatch(table.toString());
                statement.executeBatch();

            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            CoinsAPI.getInstance().getLogger().log(Level.FINE, "Connected to database!");
        } catch (Exception exception) {
            exception.printStackTrace();
        }


    }

    @Override
    public CompletableFuture<User> loadPlayer(UUID uuid) {
        User user = new User();
        return future(() -> {
            try (
                    Connection connection = getConnection();
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(Query.select("player_table").where("UUID", uuid.toString()).build())) {
                int coins = 0;
                if (resultSet.next())
                    coins = resultSet.getInt("COINS");

                user.setUuid(uuid);
                user.setCoins(coins);

            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            stats.put(uuid, user);

            return user;
        });
    }

    @Override
    public CompletableFuture<Void> unloadAndSave(User user) {
        return future(() -> {
            String query = Query.select("player_table").where("UUID", user.getUuid().toString()).build();
            String updateQuery = Query.update("player_table")
                    .set("COINS", user.getCoins())
                    .where("UUID", user.getUuid().toString())
                    .build();
            String insertQuery = Query.insert("player_table")
                    .value("UUID", user.getUuid().toString())
                    .value("COINS", user.getCoins())
                    .build();
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery();
                 PreparedStatement prepareStatement = connection.prepareStatement(resultSet.next() ? updateQuery : insertQuery)) {
                prepareStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public Optional<User> getUser(UUID uuid) {
        if (stats.containsKey(uuid))
            return Optional.of(stats.get(uuid));

        try {
            return Optional.of(loadPlayer(uuid).get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void modifyUserStats(UUID uuid, Consumer<User> action) {
        Optional<User> user = getUser(uuid);
        user.ifPresent(action);
    }

    @Override
    public void modifyUserStats(User user, Consumer<User> action) {
        action.accept(user);
    }

    @Override
    public void disconnect() {
        dataSource.close();
    }
}
