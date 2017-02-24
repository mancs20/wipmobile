package com.products.qc;

import android.provider.BaseColumns;

public final class SamplingReaderContract {
    public SamplingReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class SamplingEntry implements BaseColumns {
        public static final String TABLE_NAME = "sampling";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_PALLET = "pallet";
        public static final String COLUMN_NAME_SAMPLED = "sampled";
        public static final String COLUMN_NAME_TEMPERATURE = "temperature";
        public static final String COLUMN_NAME_GROWER = "grower";
        public static final String COLUMN_NAME_PLUS = "plus";
        public static final String COLUMN_NAME_BRIX = "brix";
        public static final String COLUMN_NAME_PRESSURE = "pressure";
        public static final String COLUMN_NAME_MEASUREMENTS = "measurements";
        public static final String COLUMN_NAME_TABLENUMBER = "tablenumber";
        
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + SamplingEntry.TABLE_NAME + " (" +
        		SamplingEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY," +
        		SamplingEntry.COLUMN_NAME_PALLET + SQLSintax.INTEGER_TYPE + SQLSintax.COMMA_SEP +
        		SamplingEntry.COLUMN_NAME_SAMPLED + SQLSintax.BOOLEAN_TYPE + SQLSintax.COMMA_SEP +
        		SamplingEntry.COLUMN_NAME_TEMPERATURE + SQLSintax.REAL_TYPE + SQLSintax.COMMA_SEP +
        		SamplingEntry.COLUMN_NAME_GROWER + SQLSintax.VARCHAR_TYPE + SQLSintax.COMMA_SEP +
        		SamplingEntry.COLUMN_NAME_PLUS + SQLSintax.INTEGER_TYPE + SQLSintax.COMMA_SEP +
        		SamplingEntry.COLUMN_NAME_BRIX + SQLSintax.REAL_TYPE + SQLSintax.COMMA_SEP +
        		SamplingEntry.COLUMN_NAME_PRESSURE + SQLSintax.REAL_TYPE + SQLSintax.COMMA_SEP +
        		SamplingEntry.COLUMN_NAME_TABLENUMBER + SQLSintax.INTEGER_TYPE + SQLSintax.COMMA_SEP +
        		SamplingEntry.COLUMN_NAME_MEASUREMENTS + SQLSintax.VARCHAR_TYPE +
                // Any other options for the CREATE command
                " )";

        public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SamplingEntry.TABLE_NAME;
    }
}