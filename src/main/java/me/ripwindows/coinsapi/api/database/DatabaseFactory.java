package me.ripwindows.coinsapi.api.database;


import me.ripwindows.coinsapi.api.database.impl.MongoDatabase;
import me.ripwindows.coinsapi.api.database.impl.SQLDatabase;
import me.ripwindows.coinsapi.exceptions.InvalidDatabaseTypeException;

public final class DatabaseFactory {
	
	private static DatabaseFactory factory;
	
	private DatabaseFactory() {
	}
	
	public static DatabaseFactory getFactory() {
		if (factory == null) {
			factory = new DatabaseFactory();
		}
		return factory;
	}
	
	public Database createDatabase(String type) throws InvalidDatabaseTypeException {
		
		Database.Type databaseType = Database.Type.valueOf(type.toUpperCase());
		return createDatabase(databaseType);
	}
	
	public Database createDatabase(Database.Type type) throws InvalidDatabaseTypeException {
		switch (type) {
			case SQL -> {
				return new SQLDatabase();
			}
			case MONGO -> {
				return new MongoDatabase();
			}
			default -> throw new InvalidDatabaseTypeException();
		}
    }
	
}
