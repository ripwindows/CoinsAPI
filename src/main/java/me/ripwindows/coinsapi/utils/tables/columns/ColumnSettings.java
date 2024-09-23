package me.ripwindows.coinsapi.utils.tables.columns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ColumnSettings {

    AUTO_INCREMENT("AUTO_INCREMENT"),
    NOT_NULL("NOT NULL"),
    UNIQUE("UNIQUE "),
    ZEROFILL("ZEROFILL"),
    UNSIGNED("UNSIGNED");

    private final @Getter String value;


}