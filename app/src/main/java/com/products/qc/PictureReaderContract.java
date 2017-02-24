package com.products.qc;

import android.provider.BaseColumns;

public final class PictureReaderContract {
    public PictureReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class PictureEntry implements BaseColumns {
        public static final String TABLE_NAME = "picture";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_SAMPLING = "sampling";
        public static final String COLUMN_NAME_NAME = "name";

        public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + PictureEntry.TABLE_NAME + " (" +
            		PictureEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY," +
            		PictureEntry.COLUMN_NAME_SAMPLING + SQLSintax.INTEGER_TYPE + SQLSintax.COMMA_SEP +
            		PictureEntry.COLUMN_NAME_NAME + SQLSintax.VARCHAR_TYPE +
            // Any other options for the CREATE command
            " )";

        public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PictureEntry.TABLE_NAME;
    }
}