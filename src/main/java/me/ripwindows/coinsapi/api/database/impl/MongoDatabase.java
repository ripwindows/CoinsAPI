package me.ripwindows.coinsapi.api.database.impl;

import com.mongodb.client.*;
import me.ripwindows.coinsapi.CoinsAPI;
import me.ripwindows.coinsapi.api.User;
import me.ripwindows.coinsapi.api.database.Database;
import me.ripwindows.coinsapi.utils.AsyncInterface;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bukkit.configuration.Configuration;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public final class MongoDatabase extends AsyncInterface implements Database {
    private final HashMap<UUID, User> stats = new HashMap<>();

    private MongoClient mongoClient;
    private com.mongodb.client.MongoDatabase database;
    private MongoCollection<Document> playerCollection;

    @Override
    public Type type() {
        return Type.MONGO;
    }

    @Override
    public void initialize() {
        Configuration config = CoinsAPI.getInstance().getSettingsConfig().getConfig();

        String url = config.getString("mongo.url");
        String db = config.getString("mongo.database");
        mongoClient = MongoClients.create(url);
        assert db != null;

        database = mongoClient.getDatabase(db);
        playerCollection = database.getCollection("player_table");
        CoinsAPI.getInstance().getLogger().info("Connected to MongoDB!");
    }

    @Override
    public CompletableFuture<User> loadPlayer(UUID uuid) {
        User user = new User();
        return future(() -> {
            Bson filter = Filters.eq("UUID", uuid.toString());
            Document result = playerCollection.find(filter).first();
            int coins = 0;
            if (result != null) {
                coins = result.getInteger("COINS", 0);
            }
            user.setUuid(uuid);
            user.setCoins(coins);

            stats.put(uuid, user);
            return user;
        });
    }

    @Override
    public CompletableFuture<Void> unloadAndSave(User user) {
        return future(() -> {
            Bson filter = Filters.eq("UUID", user.getUuid().toString());
            Document existingUser = playerCollection.find(filter).first();

            if (existingUser == null) {
                Document newUser = new Document("UUID", user.getUuid().toString())
                        .append("COINS", user.getCoins());
                playerCollection.insertOne(newUser);
                return;
            }
            Bson update = Updates.set("COINS", user.getCoins());
            playerCollection.updateOne(filter, update);

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
        mongoClient.close();
    }
}
