package tv.starcards.starcardstv.application.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final int DATABASE_VERSION = 14;
    private static final String DATABASE_NAME = "user_info.db";

    //Table, which contains tokens
    public static final String TOKENS_TABLE                      = "tokens";
    public static final String TOKEN_COLUMN_LOGIN_ACCESS_TOKEN   = "login_token";
    public static final String TOKEN_COLUMN_LOGIN_REFRESH_TOKEN  = "refresh_token";
    public static final String TOKEN_COLUMN_PACKET_ACCESS_TOKEN  = "access_token";
    public static final String TOKEN_COLUMN_PACKET_REFRESH_TOKEN = "packet_refresh_token";

    // Table, which contains user's logged state
    public static final String LOGGED_DETAILS_TABLE                    = "logged_table";
    public static final String LOGGED_STATE_COLUMN_IS_LOGGED           = "is_logged";
    public static final String LOGGED_STATE_COLUMN_IS_LOGGED_BY_PACKET = "is_logged_by_packet";

    // Table, which contains user data
    public static final String USER_INFO_TABLE    = "info";
    public static final String USER_INFO_ID       = "_id";
    public static final String USER_INFO_EMAIL    = "email";
    public static final String USER_INFO_NAME     = "name";
    public static final String USER_INFO_BALANCE  = "balance";
    public static final String USER_INFO_BONUS    = "bonus";
//    public static final String USER_INFO_PASSWORD = "password";

    // Table, which contains packet data
    public static final String PACKET_TABLE    = "packets";
    public static final String PACKET_ID       = "packet_id";
    public static final String PACKET_NAME     = "packet_name";
    public static final String PACKET_PASSWORD = "packet_password";
    public static final String PACKET_DATE_END = "packet_date_end";
    public static final String PACKET_STATUS   = "packet_status";
    public static final String PACKET_USER_EMAIL = "packet_user_id";

    // Table, which contains tv channel's data
    public static final String CHANNELS_TABLE            = "tv_channels";
    public static final String CHANNEL_ID                = "_id";
    public static final String CHANNEL_NAME              = "name";
    public static final String CHANNEL_GENRE_ID          = "genre_id";
    public static final String CHANNEL_NUMBER            = "number";
    public static final String CHANNEL_URL               = "url";
    public static final String CHANNEL_ARCHIVE           = "archive";
    public static final String CHANNEL_ARCHIVE_RANGE     = "archive_range";
    public static final String CHANNEL_PVR               = "pvr";
    public static final String CHANNEL_CENSORED          = "censored";
    public static final String CHANNEL_FAVORITE          = "favorite";
    public static final String CHANNEL_LOGO              = "logo";
    public static final String CHANNEL_MONITORING_STATUS = "monitoring_status";
    public static final String CHANNEL_PACKET_ID         = "packet_id";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TOKENS_TABLE + "(" +
                TOKEN_COLUMN_LOGIN_ACCESS_TOKEN +   " TEXT, " +
                TOKEN_COLUMN_LOGIN_REFRESH_TOKEN +  " TEXT, " +
                TOKEN_COLUMN_PACKET_ACCESS_TOKEN +  " TEXT, " +
                TOKEN_COLUMN_PACKET_REFRESH_TOKEN + " TEXT" +
                ")"
        );

        db.execSQL(
                "CREATE TABLE " + LOGGED_DETAILS_TABLE + "(" +
                LOGGED_STATE_COLUMN_IS_LOGGED +           " TEXT, " +
                LOGGED_STATE_COLUMN_IS_LOGGED_BY_PACKET + " TEXT " +
                ")"
        );

        db.execSQL(
                "CREATE TABLE " + USER_INFO_TABLE + "(" +
                USER_INFO_ID       + " TEXT," +
                USER_INFO_EMAIL    + " TEXT PRIMARY KEY, " +
                USER_INFO_NAME     + " TEXT, " +
                USER_INFO_BALANCE  + " TEXT, " +
                USER_INFO_BONUS    + " TEXT " +
//                USER_INFO_PASSWORD + " TEXT " +
                ")"
        );

        db.execSQL(
                "CREATE TABLE " + PACKET_TABLE + "(" +
                PACKET_ID       + " TEXT PRIMARY KEY, " +
                PACKET_NAME     + " TEXT, " +
                PACKET_PASSWORD + " TEXT, " +
                PACKET_DATE_END + " TEXT, " +
                PACKET_STATUS   + " TEXT, " +
                PACKET_USER_EMAIL + " TEXT, " +
                "FOREIGN KEY (" + PACKET_USER_EMAIL + ") REFERENCES " + USER_INFO_TABLE + "(" + USER_INFO_EMAIL + ")" +
                ")"
        );

        db.execSQL(
                "CREATE TABLE " + CHANNELS_TABLE + "(" +
                CHANNEL_ID                + " TEXT PRIMARY KEY, " +
                CHANNEL_NAME              + " TEXT, " +
                CHANNEL_GENRE_ID          + " TEXT, " +
                CHANNEL_NUMBER            + " TEXT, " +
                CHANNEL_URL               + " TEXT, " +
                CHANNEL_ARCHIVE           + " TEXT, " +
                CHANNEL_ARCHIVE_RANGE     + " TEXT, " +
                CHANNEL_PVR               + " TEXT, " +
                CHANNEL_CENSORED          + " TEXT, " +
                CHANNEL_FAVORITE          + " TEXT, " +
                CHANNEL_LOGO              + " TEXT, " +
                CHANNEL_MONITORING_STATUS + " TEXT, " +
                CHANNEL_PACKET_ID         + " TEXT, " +
                "FOREIGN KEY (" + CHANNEL_PACKET_ID + ") REFERENCES " + PACKET_TABLE + "(" + PACKET_ID + ")" +
                ")"
        );

        // Fill the database with values by default
        db.execSQL("INSERT INTO " + TOKENS_TABLE + " VALUES (\"null\", \"null\", \"null\", \"null\" )");
        db.execSQL("INSERT INTO " + LOGGED_DETAILS_TABLE + " VALUES (\"false\", \"false\" )");
        db.execSQL("INSERT INTO " + USER_INFO_TABLE + " VALUES (\"null\", \"null\", \"null\", \"null\", \"null\")");
        db.execSQL("INSERT INTO " + PACKET_TABLE + " VALUES (\"null\", \"null\", \"null\", \"null\", \"null\", \"null\")");
        db.execSQL("INSERT INTO " + CHANNELS_TABLE + " VALUES (\"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\" )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TOKENS_TABLE + " ;");
        db.execSQL("DROP TABLE IF EXISTS " + USER_INFO_TABLE + " ;");
        db.execSQL("DROP TABLE IF EXISTS " + LOGGED_DETAILS_TABLE + " ;");
        db.execSQL("DROP TABLE IF EXISTS " + PACKET_TABLE + " ;");
        db.execSQL("DROP TABLE IF EXISTS " + CHANNELS_TABLE + " ;");
        onCreate(db);
    }
}
