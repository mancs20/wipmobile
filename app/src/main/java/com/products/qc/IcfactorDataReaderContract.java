package com.products.qc;

import android.provider.BaseColumns;

public final class IcfactorDataReaderContract {
    public IcfactorDataReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class IcfactorDataEntry implements BaseColumns {
        public static final String TABLE_NAME = "icfactor_data";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_SAMPLING = "sampling";
        public static final String COLUMN_NAME_FACTOR = "factor";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_BUTTON = "button";
        public static final String COLUMN_NAME_SL = "sl";
        public static final String COLUMN_NAME_M = "m";
        public static final String COLUMN_NAME_S = "s";
        public static final String COLUMN_NAME_TABLE = "qcfactortable";
        
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + IcfactorDataEntry.TABLE_NAME + " (" +
                		IcfactorDataEntry._ID + " INTEGER PRIMARY KEY," +
                		IcfactorDataEntry.COLUMN_NAME_SAMPLING + SQLSintax.INTEGER_TYPE + SQLSintax.COMMA_SEP +
                		IcfactorDataEntry.COLUMN_NAME_NAME + SQLSintax.VARCHAR_TYPE + SQLSintax.COMMA_SEP +
                		IcfactorDataEntry.COLUMN_NAME_FACTOR + SQLSintax.VARCHAR_TYPE + SQLSintax.COMMA_SEP +
                		IcfactorDataEntry.COLUMN_NAME_BUTTON + SQLSintax.INTEGER_TYPE + SQLSintax.COMMA_SEP +
                		IcfactorDataEntry.COLUMN_NAME_SL + SQLSintax.INTEGER_TYPE + SQLSintax.COMMA_SEP +
                		IcfactorDataEntry.COLUMN_NAME_M + SQLSintax.INTEGER_TYPE + SQLSintax.COMMA_SEP +
                		IcfactorDataEntry.COLUMN_NAME_S + SQLSintax.INTEGER_TYPE + SQLSintax.COMMA_SEP +
                		IcfactorDataEntry.COLUMN_NAME_TABLE + SQLSintax.INTEGER_TYPE +
                // Any other options for the CREATE command
                " )";

        public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + IcfactorDataEntry.TABLE_NAME;
    }
}
