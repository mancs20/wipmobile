package com.products.qc;

import android.provider.BaseColumns;

public final class PalletReaderContract {
    public PalletReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class PalletEntry implements BaseColumns {
        public static final String TABLE_NAME = "pallet";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_PRODUCT = "product";
        public static final String COLUMN_NAME_CODE = "code";
        
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + PalletEntry.TABLE_NAME + " (" +
                PalletEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY," +
                PalletEntry.COLUMN_NAME_PRODUCT + SQLSintax.INTEGER_TYPE + SQLSintax.COMMA_SEP +
        		PalletEntry.COLUMN_NAME_CODE + SQLSintax.INTEGER_TYPE +
                // Any other options for the CREATE command
                " )";

        public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PalletEntry.TABLE_NAME;
    }
}