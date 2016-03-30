package tv.starcards.starcardstv.application.data.userdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.application.data.state.IsLogged;
import tv.starcards.starcardstv.application.data.state.IsLoggedByPacket;
import tv.starcards.starcardstv.application.data.db.DBHelper;
import tv.starcards.starcardstv.application.exceptions.NoDataException;
import tv.starcards.starcardstv.application.data.tokens.LoginAccessToken;
import tv.starcards.starcardstv.application.data.tokens.PacketAccessToken;
import tv.starcards.starcardstv.application.data.tokens.PacketRefreshToken;
import tv.starcards.starcardstv.application.data.tokens.LoginRefreshToken;
import tv.starcards.starcardstv.application.util.Parser;

public class UserData {

    private static final String TAG = "UserData";
    private static UserData ourInstance = new UserData();

    private DBHelper dbHelper;
    private Parser parser;

    private String loginToken;
    private String refreshToken;
    private String userEmail;

    private UserData() {
    }

    public void initUserData(Context context) {
        dbHelper = new DBHelper(context);
        parser = new Parser();
    }

    public static UserData getInstance() {
        return ourInstance;
    }

    public void initLoggedState() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.LOGGED_DETAILS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int state = cursor.getColumnIndex(DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED);
            IsLogged.getInstance().saveLoggedState(cursor.getString(state));
        }
        cursor.close();
        Log.w(TAG, "User logged state : logged - " + String.valueOf(IsLogged.getInstance().isLogged()));
        database.close();
    }



    public void initLoginTokens() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TOKENS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int loginTokenIndex = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_LOGIN_ACCESS_TOKEN);
            LoginAccessToken.getInstance().setAccessToken(cursor.getString(loginTokenIndex));
            int refreshTokenIndex = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_LOGIN_REFRESH_TOKEN);
            LoginRefreshToken.getInstance().setRefreshToken(cursor.getString(refreshTokenIndex));
        }
        cursor.close();
        Log.w(TAG, "Access token is loaded as " + LoginAccessToken.getInstance().getAccessToken() + ", and refresh token is saved as  " + LoginRefreshToken.getInstance().getRefreshToken());
        database.close();
    }

    public void setLoginTokens(String response) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("UPDATE " + DBHelper.TOKENS_TABLE + " SET " +
                DBHelper.TOKEN_COLUMN_LOGIN_ACCESS_TOKEN + " = \"" + parser.parse(response, "login_token") + "\", " +
                DBHelper.TOKEN_COLUMN_LOGIN_REFRESH_TOKEN + " = \"" + parser.parse(response, "refresh_token") + "\", " +
                DBHelper.TOKEN_COLUMN_PACKET_ACCESS_TOKEN + " = \"" + PacketAccessToken.getInstance().getPacketAccessToken()+ "\", " +
                DBHelper.TOKEN_COLUMN_PACKET_REFRESH_TOKEN + " = \"" + PacketRefreshToken.getInstance().getPacketRefreshToken() + "\"");

        String forLog = "";
        Cursor cursor = database.query(DBHelper.TOKENS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int loginTokenIndex = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_LOGIN_ACCESS_TOKEN);
            int refreshTokenIndex = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_LOGIN_REFRESH_TOKEN);
            LoginAccessToken.getInstance().setAccessToken(cursor.getString(loginTokenIndex));
            LoginRefreshToken.getInstance().setRefreshToken(cursor.getString(refreshTokenIndex));
            forLog = "Login token saved as : " + cursor.getString(loginTokenIndex) + ", refresh token saved as : " + cursor.getString(refreshTokenIndex);
        }
        Log.w(TAG, "Save tokens to DB: " + forLog);
        cursor.close();
        database.close();
    }

    public void setUserData(String email, String password) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("UPDATE " + DBHelper.USER_INFO_TABLE + " SET " +
                        DBHelper.USER_INFO_ID + " = \"null\", " +
                        DBHelper.USER_INFO_EMAIL + " = \"" + email + "\", " +
                        DBHelper.USER_INFO_NAME + " = \"null\", " +
                        DBHelper.USER_INFO_BALANCE + " = \"null\", " +
                        DBHelper.USER_INFO_BONUS + " = \"null\" "
//                        DBHelper.USER_INFO_PASSWORD + " = \"" + password + "\" "
        );
        database.close();
    }

    public void setUserData(String response) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.USER_INFO_TABLE, null, null, null, null, null, null);
        String email = null;
        String password = null;
        if (cursor.moveToLast()) {
            int emailIndex = cursor.getColumnIndex(DBHelper.USER_INFO_EMAIL);
//            int passwordIndex = cursor.getColumnIndex(DBHelper.USER_INFO_PASSWORD);
            email = cursor.getString(emailIndex);
//            password = cursor.getString(passwordIndex);
        }

        database.execSQL("UPDATE " + DBHelper.USER_INFO_TABLE + " SET " +
                        DBHelper.USER_INFO_ID + " = \"" + parser.parse(response, "id") + "\", " +
                        DBHelper.USER_INFO_EMAIL + " = \"" + email + "\", " +
                        DBHelper.USER_INFO_NAME + " = \"" + parser.parse(response, "name") + "\", " +
                        DBHelper.USER_INFO_BALANCE + " = \"" + parser.parse(response, "balance") + "\", " +
                        DBHelper.USER_INFO_BONUS + " = \"" + parser.parse(response, "bonus") + "\" "
//                        DBHelper.USER_INFO_PASSWORD + " = \"" + password + "\" "
        );

        String forLog = "";
        cursor = database.query(DBHelper.USER_INFO_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int id = cursor.getColumnIndex(DBHelper.USER_INFO_ID);
            int emailIndex = cursor.getColumnIndex(DBHelper.USER_INFO_EMAIL);
            int name = cursor.getColumnIndex(DBHelper.USER_INFO_NAME);
            int balance = cursor.getColumnIndex(DBHelper.USER_INFO_BALANCE);
            int bonus = cursor.getColumnIndex(DBHelper.USER_INFO_BONUS);
//            int passwordIndex = cursor.getColumnIndex(DBHelper.USER_INFO_PASSWORD);
            UserEmail.getInstance().setUserEmail(cursor.getString(emailIndex));
            forLog = "User data: id = " + cursor.getString(id) + ", email = " + cursor.getString(emailIndex) + ", name = " + cursor.getString(name) + ", balance = " + cursor.getString(balance) + ", bonus = " + cursor.getString(bonus) + ", ? password = " /*+ cursor.getString(passwordIndex)*/;
            MainScreenActivity.email.setText(cursor.getString(emailIndex));
            MainScreenActivity.balance.setText(cursor.getString(balance));
            MainScreenActivity.bonus.setText(cursor.getString(bonus));
            MainScreenActivity.name.setText(cursor.getString(name));
        }
        Log.w(TAG,forLog);
        cursor.close();
        database.close();
    }

    public void initUserInfo() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.USER_INFO_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int emailIndex = cursor.getColumnIndex(DBHelper.USER_INFO_EMAIL);
            int name = cursor.getColumnIndex(DBHelper.USER_INFO_NAME);
            int balance = cursor.getColumnIndex(DBHelper.USER_INFO_BALANCE);
            int bonus = cursor.getColumnIndex(DBHelper.USER_INFO_BONUS);
            MainScreenActivity.email.setText(cursor.getString(emailIndex));
            MainScreenActivity.balance.setText(cursor.getString(balance));
            MainScreenActivity.bonus.setText(cursor.getString(bonus));
            MainScreenActivity.name.setText(cursor.getString(name));
        }
        cursor.close();
        database.close();
    }

    public void setLoggedByLogin() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("UPDATE " + DBHelper.LOGGED_DETAILS_TABLE + " SET " +
                DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED + " = \"true\", " +
                DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED_BY_PACKET + " = \"false\" ");

        Cursor cursor = database.query(DBHelper.LOGGED_DETAILS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int state = cursor.getColumnIndex(DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED);
            Log.w(TAG, "Uses is logged : " + cursor.getString(state));
            IsLogged.getInstance().saveLoggedState(cursor.getString(state));
        }
        cursor.close();
        database.close();
    }



    public void resetUserData() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("UPDATE " + DBHelper.TOKENS_TABLE + " SET " +
                DBHelper.TOKEN_COLUMN_LOGIN_ACCESS_TOKEN + " = \"null\", " +
                DBHelper.TOKEN_COLUMN_LOGIN_REFRESH_TOKEN + " = \"null\", " +
                DBHelper.TOKEN_COLUMN_PACKET_ACCESS_TOKEN + " = \"null\", " +
                DBHelper.TOKEN_COLUMN_PACKET_REFRESH_TOKEN + " = \"null\" ");

        database.execSQL("UPDATE " + DBHelper.LOGGED_DETAILS_TABLE + " SET " +
                DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED + " = \"false\", " +
                DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED_BY_PACKET + " = \"false\" ");

        database.execSQL("UPDATE " + DBHelper.USER_INFO_TABLE + " SET " +
                DBHelper.USER_INFO_ID + " = \"null\", " +
                DBHelper.USER_INFO_EMAIL + " = \"null\", " +
                DBHelper.USER_INFO_NAME + " = \"null\", " +
                DBHelper.USER_INFO_BALANCE + " = \"null\", " +
                DBHelper.USER_INFO_BONUS + " = \"null\" ");
//                DBHelper.USER_INFO_PASSWORD + " = \"null\" ");

        database.close();
    }
}
