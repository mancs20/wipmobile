package com.products.qc;

import android.provider.BaseColumns;
import com.products.qc.SQLSintax;

public final class ProductReaderContract {
    public ProductReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "product";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_VARIETY = "variety";
        public static final String COLUMN_NAME_VARIETYSN = "varietysn";
        public static final String COLUMN_NAME_SIZE = "size";
        public static final String COLUMN_NAME_SIZESN = "sizesn";
        public static final String COLUMN_NAME_STYLE = "style";
        public static final String COLUMN_NAME_STYLESN = "stylesn";
        public static final String COLUMN_NAME_LABEL = "label";
        public static final String COLUMN_NAME_LABELSN = "labelsn";
        public static final String COLUMN_NAME_MIN = "min";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_NAMESN = "namesn";

        public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + ProductEntry.TABLE_NAME + " (" +
    		ProductEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY," +
    		ProductEntry.COLUMN_NAME_VARIETY + SQLSintax.VARCHAR_TYPE + SQLSintax.COMMA_SEP +
    		ProductEntry.COLUMN_NAME_VARIETYSN + SQLSintax.VARCHAR_TYPE + SQLSintax.COMMA_SEP +
    		ProductEntry.COLUMN_NAME_SIZE + SQLSintax.VARCHAR_TYPE + SQLSintax.COMMA_SEP +
    		ProductEntry.COLUMN_NAME_SIZESN + SQLSintax.VARCHAR_TYPE + SQLSintax.COMMA_SEP +
    		ProductEntry.COLUMN_NAME_STYLE + SQLSintax.VARCHAR_TYPE + SQLSintax.COMMA_SEP +
    		ProductEntry.COLUMN_NAME_STYLESN + SQLSintax.VARCHAR_TYPE + SQLSintax.COMMA_SEP +
    		ProductEntry.COLUMN_NAME_LABEL + SQLSintax.VARCHAR_TYPE + SQLSintax.COMMA_SEP +
    		ProductEntry.COLUMN_NAME_LABELSN + SQLSintax.VARCHAR_TYPE + SQLSintax.COMMA_SEP +
    		ProductEntry.COLUMN_NAME_MIN + SQLSintax.INTEGER_TYPE + SQLSintax.COMMA_SEP +
    		ProductEntry.COLUMN_NAME_NAME + SQLSintax.VARCHAR_TYPE + SQLSintax.COMMA_SEP +
    		ProductEntry.COLUMN_NAME_NAMESN + SQLSintax.VARCHAR_TYPE +
            // Any other options for the CREATE command
            " )";

        public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;
    }
}