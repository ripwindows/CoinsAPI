package me.ripwindows.coinsapi.api.database;


import me.ripwindows.coinsapi.utils.tables.Table;
import me.ripwindows.coinsapi.utils.tables.columns.Column;
import me.ripwindows.coinsapi.utils.tables.columns.ColumnSettings;
import me.ripwindows.coinsapi.utils.tables.columns.ColumnType;

public class PlayerTable extends Table {
    public PlayerTable(String name) {
        super(name);
        addColumns(
                Column.of("UUID", ColumnType.VARCHAR).size(255).settings(ColumnSettings.NOT_NULL),
                Column.of("COINS", ColumnType.INT)
        );
    }
}
