package com.example.shonandtomer.hilik;

import android.provider.BaseColumns;

/**
 * Created by sotmazgi on 2/20/2017.
 */

public class Constants {
    // Database Name
    public static final String DATABASE_NAME = "HilikDB";

    private Constants(){
        throw new AssertionError("Can't create constants class");
    }
    public static abstract class reports implements BaseColumns {
        // Table Names
        public static final String TABLE_REPORTS = "reportingList";

        // REPORTS Table - column names
        public static final String KEY_ID = "id";
        public static final String DAY = "day";
        public static final String MONTH = "month";
        public static final String YEAR = "year";
        public static final String ENTRY = "entry";
        public static final String EXIT = "exit";
    }
}
