public final class DBPLD {	
	
    private DBPLD() {}

    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ", ";
    public static final String INT_TYPE = " INTEGER";
    public static final String FOREIGN_KEY = " FOREIGN KEY";
    public static final String BLOB_TYPE = " BLOB";
    
    
	    /* Inner class that defines the table contents */
    //BaseColumns -> constante héritée _ID servant de clé primaire (nécessaire pour utiliser la classe Cursor)
    public static abstract class users {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TITLE = "name";
        public static final String COLUMN_NAME_PASSWORD = "password";
 
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_PASSWORD + BLOB_TYPE + " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        
    }
    public static abstract class calendarEvents {
    	public static final String TABLE_NAME = "calendarEvents";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_UID = "userid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_EVENTDATE = "eventdate";
        
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_UID + INT_TYPE + " NOT NULL" + COMMA_SEP +
                COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_LOCATION + TEXT_TYPE + " DEFAULT \"\"" + COMMA_SEP +
                COLUMN_NAME_EVENTDATE + " TIMESTAMP" + COMMA_SEP +
                FOREIGN_KEY + "(" + COLUMN_NAME_UID + ") REFERENCES " 
                + users.TABLE_NAME +"(" + users.COLUMN_NAME_ID + ")"
                + ")";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
    public static abstract class itineraries {
    	public static final String TABLE_NAME = "itineraries";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_UID = "userid";
        public static final String COLUMN_NAME_DLOCATION = "departurelocation";
        public static final String COLUMN_NAME_ALOCATION = "arrivallocation";
        public static final String COLUMN_NAME_TMODE = "tranportmodes";
        public static final String COLUMN_NAME_DEPARTURE = "departure";
        public static final String COLUMN_NAME_ARRIVAL = "arrival";
        public static final String COLUMN_NAME_CALCULATED_DURATION = "calculatedduration";
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_UID + INT_TYPE + " NOT NULL" + COMMA_SEP + 
                COLUMN_NAME_TMODE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_DEPARTURE + " TIME" + COMMA_SEP +
                COLUMN_NAME_ARRIVAL + " TIME" + COMMA_SEP +
                COLUMN_NAME_CALCULATED_DURATION + " TIME" + COMMA_SEP +
                COLUMN_NAME_DLOCATION + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_DLOCATION + TEXT_TYPE + COMMA_SEP +
                FOREIGN_KEY + "(" + COLUMN_NAME_UID + ") REFERENCES " 
                + users.TABLE_NAME +"(" + users.COLUMN_NAME_ID + ")"
                		+ ")";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }    
}
	
	
	


