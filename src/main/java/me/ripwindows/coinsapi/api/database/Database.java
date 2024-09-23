package me.ripwindows.coinsapi.api.database;

import me.ripwindows.coinsapi.api.User;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface Database {
	
	Type type();
	
	void initialize();

	CompletableFuture<User> loadPlayer(UUID uuid);

	CompletableFuture<Void> unloadAndSave(User user);

	Optional<User> getUser(UUID uuid);

	void modifyUserStats(UUID uuid, Consumer<User> action);

	void modifyUserStats(User user, Consumer<User> action);

	void disconnect();
	enum Type {
		
		SQL,
		
		MONGO,
		
	}
}
