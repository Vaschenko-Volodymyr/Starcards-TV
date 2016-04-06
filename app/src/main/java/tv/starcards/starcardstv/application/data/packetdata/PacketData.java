package tv.starcards.starcardstv.application.data.packetdata;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.application.data.db.DBHelper;
import tv.starcards.starcardstv.application.data.state.IsLogged;
import tv.starcards.starcardstv.application.data.state.IsLoggedByPacket;
import tv.starcards.starcardstv.application.data.tokens.LoginAccessToken;
import tv.starcards.starcardstv.application.data.tokens.LoginRefreshToken;
import tv.starcards.starcardstv.application.data.tokens.PacketAccessToken;
import tv.starcards.starcardstv.application.data.tokens.PacketRefreshToken;
import tv.starcards.starcardstv.application.exceptions.NoDataException;
import tv.starcards.starcardstv.application.ui.adaptors.PacketAdaptor;
import tv.starcards.starcardstv.application.ui.fragments.CabinetFragment;
import tv.starcards.starcardstv.application.ui.models.PacketListModel;
import tv.starcards.starcardstv.application.util.DateConverter;
import tv.starcards.starcardstv.application.util.ListViewConverter;
import tv.starcards.starcardstv.application.util.Parser;

public class PacketData {

    private static final String TAG = PacketData.class.toString();
    private static PacketData ourInstance = new PacketData();

    private DBHelper dbHelper;
    private Parser parser;
    private ListViewConverter listViewConverter;

    private Resources resources;

    private String packetToken;
    private String packetRefreshToken;
    private String packetId;
    private String packetPassword;


    private PacketData() {
    }

    public void initPacketData(Context context, Resources resources) {
        this.resources = resources;
        dbHelper = new DBHelper(context);
        parser = new Parser();
        listViewConverter = new ListViewConverter();
    }

    public static PacketData getInstance() {
        return ourInstance;
    }

    public void initLoggedByPacketState() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.LOGGED_DETAILS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int state = cursor.getColumnIndex(DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED_BY_PACKET);
            IsLoggedByPacket.getInstance().saveLoggedState(cursor.getString(state));
        }
        cursor.close();
        Log.w(TAG, "Logged by packet state : logged - " + String.valueOf(IsLogged.getInstance().isLogged()));
        database.close();

//        if (loginAccessTokenIsOld()) {
//            refreshLoginAccessToken();
//        }
    }

    public void initPacketTokens() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TOKENS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int accessTokenIndex = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_PACKET_ACCESS_TOKEN);
            PacketAccessToken.getInstance().setPacketAccessToken(cursor.getString(accessTokenIndex));
            int refreshTokenIndex = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_PACKET_REFRESH_TOKEN);
            PacketRefreshToken.getInstance().setPacketRefreshToken(cursor.getString(refreshTokenIndex));
        }
        cursor.close();
        Log.w(TAG, "Packet access token is loaded as " + PacketAccessToken.getInstance().getPacketAccessToken() + ", and packet refresh token is saved as " + PacketRefreshToken.getInstance().getPacketRefreshToken());

        cursor = database.query(DBHelper.PACKET_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int packetId = cursor.getColumnIndex(DBHelper.PACKET_ID);
            this.packetId = cursor.getString(packetId);
            int packetPassword = cursor.getColumnIndex(DBHelper.PACKET_PASSWORD);
            this.packetPassword = cursor.getString(packetPassword);
        }
        database.close();
    }

    public void setPacketTokens(String response) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String accessToken = parser.parse(response, "access_token");
        String refreshToken = parser.parse(response, "refresh_token");
        database.execSQL("UPDATE " + DBHelper.TOKENS_TABLE + " SET " +
                DBHelper.TOKEN_COLUMN_LOGIN_ACCESS_TOKEN + " = \"" + LoginAccessToken.getInstance().getAccessToken() + "\", " +
                DBHelper.TOKEN_COLUMN_LOGIN_REFRESH_TOKEN + " = \"" + LoginRefreshToken.getInstance().getRefreshToken() + "\", " +
                DBHelper.TOKEN_COLUMN_PACKET_ACCESS_TOKEN + " = \"" + accessToken + "\", " +
                DBHelper.TOKEN_COLUMN_PACKET_REFRESH_TOKEN + " = \"" + refreshToken + "\" ");
        PacketAccessToken.getInstance().setPacketAccessToken(accessToken);
        PacketRefreshToken.getInstance().setPacketRefreshToken(refreshToken);

        Cursor cursor = database.query(DBHelper.TOKENS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int login = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_LOGIN_ACCESS_TOKEN);
            int refresh = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_LOGIN_REFRESH_TOKEN);
            int access = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_PACKET_ACCESS_TOKEN);
            int accessRefresh = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_PACKET_REFRESH_TOKEN);
            Log.w(TAG, "Packet and login tokens are saved as : Access - " + cursor.getString(login) + ", Refresh - " + cursor.getString(refresh) +
                    ", Packet Access - " + cursor.getString(access) + ", Packet Refresh - " + cursor.getString(accessRefresh));
        }
        cursor.close();
        database.close();
    }

    public void setLoggedByPacket() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("UPDATE " + DBHelper.LOGGED_DETAILS_TABLE + " SET " +
                DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED + " = \"" + String.valueOf(IsLogged.getInstance().isLogged()) + "\", " +
                DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED_BY_PACKET + " = \"true\" ");
        database.close();
    }

    public void setPacketIdAndPassword(String packetId, String packetPassword) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("UPDATE " + DBHelper.PACKET_TABLE + " SET " +
                DBHelper.PACKET_ID + " = \"" + packetId + "\", " +
                DBHelper.PACKET_PASSWORD + " = \"" + packetPassword + "\" ");
        database.close();
    }

    public String getPacketId() throws NoDataException {
        if (packetId != null) {
            return packetId;
        } else throw new NoDataException();
    }

    public String getPacketPassword() throws NoDataException {
        if (packetPassword != null) {
            return packetPassword;
        } else throw new NoDataException();

    }

    public void savePacketInfo(JSONObject response) throws JSONException {
        JSONArray array = response.getJSONArray("results");
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Log.w(TAG, "Packets JSON array: " + array.toString());
        Cursor cursor = database.query(DBHelper.USER_INFO_TABLE, null, null, null, null, null, null);
        String userEmail = null;
        if (cursor.moveToLast()) {
            int userIdIndex = cursor.getColumnIndex(DBHelper.USER_INFO_EMAIL);
            userEmail = cursor.getString(userIdIndex);
        }

        for (int i = 0; i < array.length(); i++) {
            JSONObject packet = array.getJSONObject(i);
            String id = packet.getString("id");
            String name = packet.getString("title");
            String password = packet.getString("pass");
            String dateEnd = packet.getString("date_end");
            String status = packet.getString("status");

            String query = "INSERT INTO " + DBHelper.PACKET_TABLE + " VALUES ( " +
                    "\"" + id + "\", " +
                    "\"" + name + "\", " +
                    "\"" + password + "\", " +
                    "\"" + dateEnd + "\", " +
                    "\"" + status + "\", " +
                    "\"" + userEmail + "\" " +
                    ")"
                    ;
            database.execSQL(query);
        }
        cursor.close();
        database.close();
    }

    public void loadPacketsFromDB() {
        Log.d(TAG, "Packet info is loaded from DB");
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.PACKET_TABLE, null, null, null, null, null, null);
        int i = 1;
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                final PacketListModel model = new PacketListModel();
                int id = cursor.getColumnIndex(DBHelper.PACKET_ID);
                int name = cursor.getColumnIndex(DBHelper.PACKET_NAME);
                int password = cursor.getColumnIndex(DBHelper.PACKET_PASSWORD);
                int dateEnd = cursor.getColumnIndex(DBHelper.PACKET_DATE_END);
                int status = cursor.getColumnIndex(DBHelper.PACKET_STATUS);

                model.setId(cursor.getString(id));
                model.setName(cursor.getString(name));
                model.setDate(DateConverter.timestampToDate(new Date(Long.parseLong(cursor.getString(dateEnd)) * 1000).toString()));
                model.setPassword(cursor.getString(password));
                model.setStatus(cursor.getColumnName(status));
                CabinetFragment.packetListArray.add(model);
                i++;
            }
            CabinetFragment.adapter = new PacketAdaptor(CabinetFragment.packetListViewActivity, CabinetFragment.packetListArray, resources);
            CabinetFragment.packets.setAdapter(CabinetFragment.adapter);
            listViewConverter.setListViewHeightBasedOnChildren(CabinetFragment.packets);
//            MainScreenActivity.pDialog.dismiss();
        }

        cursor.close();
        database.close();
    }

    public void resetPacketData() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("DELETE FROM " + DBHelper.PACKET_TABLE + " ;");
        database.execSQL("INSERT INTO " + DBHelper.PACKET_TABLE + " VALUES (\"null\", \"null\", \"null\", \"null\", \"null\", \"null\")");
        database.close();
    }

    public String getPacketData(String name, String from) {
        String result = null;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String query = "SELECT * FROM " + DBHelper.PACKET_TABLE + " WHERE " + DBHelper.PACKET_NAME + " = \"" + name + "\"";
        Log.d(TAG, "getPacketId: query = " + query);
        Cursor cursor = database != null ? database.rawQuery(query, null) : null;
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        while (true) {
            result = cursor.getString(cursor.getColumnIndex(from));
            if (cursor.isLast()) break;
            cursor.moveToNext();
        }
        cursor.close();
        Log.d(TAG, "getPacketId: result = " + result);
        return result;
    }
}
