package tv.starcards.starcardstv.application.data.channelsdata;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.application.data.db.DBHelper;
import tv.starcards.starcardstv.application.ui.adaptors.TvChannelsAdaptor;
import tv.starcards.starcardstv.application.ui.fragments.ChannelsFragment;
import tv.starcards.starcardstv.application.ui.models.TvChannelListModel;
import tv.starcards.starcardstv.application.util.DateConverter;
import tv.starcards.starcardstv.application.util.ExpandableListViewConverter;
import tv.starcards.starcardstv.application.util.ListViewConverter;
import tv.starcards.starcardstv.application.util.Parser;
import tv.starcards.starcardstv.application.util.Translit;

public class ChannelsData {

    private static final String TAG = ChannelsData.class.toString();
    private static ChannelsData ourInstance = new ChannelsData();

    private DBHelper dbHelper;
    private Parser parser;
    private DateConverter dateConverter;
    private Resources resources;
    private ListViewConverter converter;

    private ChannelsData() {
    }

    public void initChannelsData(Context context, Resources resources) {
        this.resources = resources;
        dbHelper = new DBHelper(context);
        parser = new Parser();
        dateConverter = new DateConverter();
        converter = new ListViewConverter();
    }

    public static ChannelsData getInstance() {
        return ourInstance;
    }

    public void saveChannelsToDB(JSONObject response, String packetId) throws JSONException {
        JSONArray array = response.getJSONArray("results");
        Log.w(TAG, "SaveChannelsToDB: Packets JSON array: " + array.toString());

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        for (int i = 0; i < array.length(); i++) {
            JSONObject channel = array.getJSONObject(i);
            String query = "INSERT INTO " + DBHelper.CHANNELS_TABLE + " VALUES ( " +
                    "\"" + channel.getString("id") + "\", " +
                    "\"" + channel.getString("name") + "\", " +
                    "\"" + channel.getString("genre_id") + "\", " +
                    "\"" + channel.getString("number") + "\", " +
                    "\"" + channel.getString("url") + "\", ";

            if (channel.getString("archive").equals("0")) {
                query = query + "\"false\", ";
            } else {
                query = query + "\"true\", ";
            }
            query = query + "\"" + channel.getString("archive_range") + "\", ";
            if (channel.getString("pvr").equals("0")) {
                query = query + "\"false\", ";
            } else {
                query = query + "\"true\", ";
            }
            if (channel.getString("censored").equals("0")) {
                query = query + "\"false\", ";
            } else {
                query = query + "\"true\", ";
            }
            if (channel.getString("favorite").equals("0")) {
                query = query + "\"false\", ";
            } else {
                query = query + "\"true\", ";
            }

            query = query + "\"" + channel.getString("logo").replace("\\", "") + "\", ";
            if (channel.getString("monitoring_status").equals("0")) {
                query = query + "\"false\", ";
            } else {
                query = query + "\"true\", ";
            }
            query = query + "\"" + packetId + "\", ";
            query = query + "\"" + Translit.translit(channel.getString("name")) + "\") ";
            database.execSQL(query);
            Log.d(TAG, "saveChannelsToDB: " + query);
        }

        Cursor cursor = database.query(DBHelper.CHANNELS_TABLE, null, null, null, null, null, null);
        int i = 1;
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                int id = cursor.getColumnIndex(DBHelper.CHANNEL_ID);
                int name = cursor.getColumnIndex(DBHelper.CHANNEL_NAME);
                int genre_id = cursor.getColumnIndex(DBHelper.CHANNEL_GENRE_ID);
                int number = cursor.getColumnIndex(DBHelper.CHANNEL_NUMBER);
                int url = cursor.getColumnIndex(DBHelper.CHANNEL_URL);
                int archive = cursor.getColumnIndex(DBHelper.CHANNEL_ARCHIVE);
                int archive_range = cursor.getColumnIndex(DBHelper.CHANNEL_ARCHIVE_RANGE);
                int pvr = cursor.getColumnIndex(DBHelper.CHANNEL_PVR);
                int censored = cursor.getColumnIndex(DBHelper.CHANNEL_CENSORED);
                int favorite = cursor.getColumnIndex(DBHelper.CHANNEL_FAVORITE);
                int logo = cursor.getColumnIndex(DBHelper.CHANNEL_LOGO);
                int monitoring_status = cursor.getColumnIndex(DBHelper.CHANNEL_MONITORING_STATUS);
                int nameTranslit = cursor.getColumnIndex(DBHelper.CHANNEL_NAME_TRANSLIT);

                String message =
                        " id = " + cursor.getString(id) +
                                ", name = " + cursor.getString(name) +
                                ", genre_id = " + cursor.getString(genre_id) +
                                ", number = " + cursor.getString(number) +
                                ", url = " + cursor.getString(url) +
                                ", archive = " + cursor.getString(archive) +
                                ", archive_range = " + cursor.getString(archive_range) +
                                ", prv = " + cursor.getString(pvr) +
                                ", censored = " + cursor.getString(censored) +
                                ", favorite = " + cursor.getString(favorite) +
                                ", logo = " + cursor.getString(logo) +
                                ", monitoring_status = " + cursor.getString(monitoring_status) +
                                ", name_translit = " + cursor.getString(nameTranslit);
                Log.d(TAG, "Got from DB - channel " + i + ": " + message);
                i++;
            }
        }

        cursor.close();
        database.close();
    }

    public void loadChannelsFromDB() {
        loadChannelsFromDB("", "");
    }

    public void loadChannelsFromDB(String column, CharSequence condition) {
        Map<Integer, Map<String, String>> channel = new HashMap<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor;
        String query = "SELECT * FROM " + DBHelper.CHANNELS_TABLE + " ";
        if (!column.equals("")) {
            if (column.equals(DBHelper.CHANNEL_NAME)) {
                condition = Translit.translit(condition.toString());
                column = DBHelper.CHANNEL_NAME_TRANSLIT;
                String advancedQuery;
                // SQL query goes like : SELECT * FROM column WHERE current_column LIKE '%condition' ORDER BY column
                advancedQuery = query + "WHERE " + column + " LIKE \"%" + condition + "\" " +
                                           "OR " + column + " LIKE \"%" + condition + "%\" " +
                                           "OR " + column + " LIKE \""  + condition + "%\" " +
                                           "OR " + column + " LIKE \""  + condition + "\" ";
                cursor = database.rawQuery(advancedQuery, null);
                Log.w(TAG, "loadChannelsFromDB: SQL LIKE query -> " + advancedQuery);
            }
            /*else if(column.equals(DBHelper.CHANNEL_FAVORITE)) {
                String advancedQuery = "SELECT * FROM " + DBHelper.CHANNELS_TABLE + " " +
                                        "WHERE " + column + " = \"" + condition + "\"";
                cursor = database.rawQuery(advancedQuery, null);
            }*/
            else {
                // TODO: other select with LIKE conditions
                String advancedQuery = "SELECT * FROM " + DBHelper.CHANNELS_TABLE + " " +
                        "WHERE " + column + " = \"" + condition + "\"";
                cursor = database.rawQuery(advancedQuery, null);

            }
            putInMap(channel, cursor);
        } else {
            cursor = database.rawQuery(query, null);
            Log.w(TAG, "loadChannelsFromDB: SQL LIKE query -> " + query);
            putInMap(channel, cursor);
        }
        attachChannelDataToRow(channel);
        cursor.close();
        database.close();
    }

    private void putInMap(Map<Integer, Map<String, String>> channel, Cursor cursor) {
        int i = channel.size();
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> info = new HashMap<>();

                info.put("id", cursor.getString(cursor.getColumnIndex(DBHelper.CHANNEL_ID)));
                info.put("name", cursor.getString(cursor.getColumnIndex(DBHelper.CHANNEL_NAME)));
                info.put("genre_id", cursor.getString(cursor.getColumnIndex(DBHelper.CHANNEL_GENRE_ID)));
                info.put("number", cursor.getString(cursor.getColumnIndex(DBHelper.CHANNEL_NUMBER)));
                info.put("url", cursor.getString(cursor.getColumnIndex(DBHelper.CHANNEL_URL)));
                info.put("archive", cursor.getString(cursor.getColumnIndex(DBHelper.CHANNEL_ARCHIVE)));
                info.put("archive_range", cursor.getString(cursor.getColumnIndex(DBHelper.CHANNEL_ARCHIVE_RANGE)));
                info.put("pvr", cursor.getString(cursor.getColumnIndex(DBHelper.CHANNEL_PVR)));
                info.put("censored", cursor.getString(cursor.getColumnIndex(DBHelper.CHANNEL_CENSORED)));
                info.put("favorite", cursor.getString(cursor.getColumnIndex(DBHelper.CHANNEL_FAVORITE)));
                info.put("logo", cursor.getString(cursor.getColumnIndex(DBHelper.CHANNEL_LOGO)));
                info.put("monitoring_status", cursor.getString(cursor.getColumnIndex(DBHelper.CHANNEL_MONITORING_STATUS)));
                info.put("name_translit", cursor.getString(cursor.getColumnIndex(DBHelper.CHANNEL_NAME_TRANSLIT)));
                info.put("packetId", cursor.getString(cursor.getColumnIndex(DBHelper.CHANNEL_PACKET_ID)));

                channel.put(i++, info);
            } while (cursor.moveToNext());
        }
    }

    private void attachChannelDataToRow(Map<Integer, Map<String, String>> channel) {
        ChannelsFragment.channelsListArray.clear();
        if (channel.size() >= 1) {
            ChannelsFragment.notFoundText.setVisibility(View.INVISIBLE);
            ChannelsFragment.notFoundImg.setVisibility(View.INVISIBLE);
            ChannelsFragment.channels.setVisibility(View.VISIBLE);
            for (int i = 0; i < channel.size(); i++) {
                Map<String, String> info = channel.get(i);
                final TvChannelListModel model = new TvChannelListModel();
                Log.d(TAG, "attachChannelDataToRow: Channel = " + info.toString());
                if (!info.get("id").equals("null")) {
                    model.setId(info.get("id"));
                    model.setTitle(info.get("name"));
                    model.setGenre(info.get("genre_id"));
                    model.setNumber(info.get("number"));
                    model.setUrl(info.get("url"));

                    if (info.get("archive").equals("false")) {
                        model.setArchive(false);
                    } else {
                        model.setArchive(true);
                    }

                    model.setArchiveRange(info.get("archive_range"));

                    if (info.get("pvr").equals("false")) {
                        model.setPvr(false);
                    } else {
                        model.setPvr(true);
                    }

                    if (info.get("censored").equals("false")) {
                        model.setCensored(false);
                    } else {
                        model.setCensored(true);
                    }

                    if (info.get("favorite").equals("false")) {
                        model.setFavorite(false);
                    } else {
                        model.setFavorite(true);
                    }

                    model.setLogo(info.get("logo").replace("\\", ""));

                    if (info.get("monitoring_status").equals("false")) {
                        model.setAvailable(false);
                    } else {
                        model.setAvailable(true);
                    }
                    model.setPacketId(info.get("packerId"));
                    ChannelsFragment.channelsListArray.add(model);
                }
            }
        } else {
            ChannelsFragment.notFoundText.setVisibility(View.VISIBLE);
            ChannelsFragment.notFoundImg.setVisibility(View.VISIBLE);
            ChannelsFragment.channels.setVisibility(View.INVISIBLE);
        }
        ChannelsFragment.adapter = new TvChannelsAdaptor(ChannelsFragment.instance, ChannelsFragment.channelsListArray, resources);
        ChannelsFragment.adapter.updateResults(ChannelsFragment.channelsListArray);
        ChannelsFragment.channels.setAdapter(ChannelsFragment.adapter);
        converter.setListViewHeightBasedOnChildren(ChannelsFragment.channels);
    }

    public void setChannelFavorite(String number, boolean isFavorite) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String query = "UPDATE " + DBHelper.CHANNELS_TABLE + " " +
                       "SET " + DBHelper.CHANNEL_FAVORITE + " = \'" + String.valueOf(isFavorite) + "\' " +
                       "WHERE " + DBHelper.CHANNEL_NUMBER + " = " + number + " ";
        database.execSQL(query);
        Log.d(TAG, ": setChannelFavorite -> query = " + query);
    }

    public void resetChannelsData() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("DELETE FROM " + DBHelper.CHANNELS_TABLE + " ;");
        database.execSQL("INSERT INTO " + DBHelper.CHANNELS_TABLE + " VALUES (\"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\" )");
        database.close();
    }
}
