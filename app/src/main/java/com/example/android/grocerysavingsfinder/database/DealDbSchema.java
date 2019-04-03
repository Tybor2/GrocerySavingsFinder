package com.example.android.grocerysavingsfinder.database;

public class DealDbSchema {
    public static final class DealTable {
        public static final String NAME = "Deals";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String ITEM = "item";
            public static final String DEAL = "deal";
            public static final String EXPIRE = "expires";
            public static final String STORE = "store";
            public static final String NOTES = "notes";
        }
    }
}
