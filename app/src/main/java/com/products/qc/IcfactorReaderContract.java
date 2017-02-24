package com.products.qc;

import android.provider.BaseColumns;

public final class IcfactorReaderContract {
    public IcfactorReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class IcfactorEntry implements BaseColumns {
        public static final String TABLE_NAME = "icfactor";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_PRODUCT = "product";
        public static final String COLUMN_NAME_FACTOR = "factor";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_BUTTON = "button";
        public static final String COLUMN_NAME_QCFACTORID = "qcfactorid";
        public static final String COLUMN_NAME_TABLE = "qcfactortable";
        public static final String COLUMN_NAME_ORDER = "ord";
        
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + IcfactorEntry.TABLE_NAME + " (" +
                		IcfactorEntry._ID + " INTEGER PRIMARY KEY," +
                		IcfactorEntry.COLUMN_NAME_PRODUCT + SQLSintax.INTEGER_TYPE + SQLSintax.COMMA_SEP +
                		IcfactorEntry.COLUMN_NAME_NAME + SQLSintax.VARCHAR_TYPE + SQLSintax.COMMA_SEP +
                		IcfactorEntry.COLUMN_NAME_FACTOR + SQLSintax.VARCHAR_TYPE + SQLSintax.COMMA_SEP +
                		IcfactorEntry.COLUMN_NAME_BUTTON + SQLSintax.INTEGER_TYPE + SQLSintax.COMMA_SEP +
                		IcfactorEntry.COLUMN_NAME_QCFACTORID + SQLSintax.INTEGER_TYPE + SQLSintax.COMMA_SEP +
                		IcfactorEntry.COLUMN_NAME_TABLE + SQLSintax.INTEGER_TYPE + SQLSintax.COMMA_SEP +
                		IcfactorEntry.COLUMN_NAME_ORDER + SQLSintax.INTEGER_TYPE +
                // Any other options for the CREATE command
                " )";

        public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + IcfactorEntry.TABLE_NAME;
    }
}
