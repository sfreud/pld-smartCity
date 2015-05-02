package com.example.androidclient;

import android.provider.BaseColumns;

public final class DBContract {
    private DBContract() {}

    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ", ";
    public static final String INT_TYPE = " INTEGER";

    public static abstract class TransportRequestContract implements BaseColumns {
        public static final String TABLE_NAME = "transport_request";
        public static final String KEY = "id";
        public static final String EVENTSUMMARY = "event_summary";
        public static final String EVENTADDRESS = "event_address";
        public static final String EVENTLAT ="event_Lat";
        public static final String EVENTLNG = "event_Lng";
        public static final String STARTADDRESS = "start_adress";
        public static final String STARTLAT ="start_Lat";
        public static final String STARTLNG = "start_Lng";
        public static final String EVENTBEGINTIME = "event_begin_time";

        public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " ("
                + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EVENTSUMMARY + TEXT_TYPE + COMMA_SEP
                + EVENTADDRESS + TEXT_TYPE + COMMA_SEP
                + EVENTLAT + INT_TYPE + COMMA_SEP
                + EVENTLNG + INT_TYPE + COMMA_SEP
                + STARTADDRESS + TEXT_TYPE + COMMA_SEP
                + STARTLAT + INT_TYPE + COMMA_SEP
                + STARTLNG + INT_TYPE + COMMA_SEP
                + EVENTBEGINTIME + INT_TYPE
                + ")";

        public static final String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


}
