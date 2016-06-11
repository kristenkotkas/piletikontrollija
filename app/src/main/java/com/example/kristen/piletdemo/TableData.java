package com.example.kristen.piletdemo;

import android.provider.BaseColumns;

public class TableData {

    public TableData() {
    }

    public static abstract class TableInfo implements BaseColumns {
        public static final String AUTH_KEY = "auth_key";
        public static final String VALID_CODE = "valid_code";
        public static final String DATABASE_NAME = "validations";
        public static final String TABLE_NAME = "valid_table";
        public static final String TABLE_AUTH = "table_auth";
    }

}
