package tv.starcards.starcardstv.application.data.channelsdata;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.application.data.db.DBHelper;
import tv.starcards.starcardstv.application.ui.adaptors.TvChannelsAdaptor;
import tv.starcards.starcardstv.application.ui.fragments.ChannelsFragment;
import tv.starcards.starcardstv.application.ui.models.TvChannelListModel;
import tv.starcards.starcardstv.application.util.DateConverter;
import tv.starcards.starcardstv.application.util.ExpandableListViewConverter;
import tv.starcards.starcardstv.application.util.ListViewConverter;
import tv.starcards.starcardstv.application.util.Parser;

public class ChannelsData {

    private static final String TAG = "PacketData";
    private static ChannelsData ourInstance = new ChannelsData();

    private DBHelper                    dbHelper;
    private Parser                      parser;
    private DateConverter               dateConverter;
    private Resources                   resources;
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
        Log.w(TAG, "SaveChannelsToDB - Packets JSON array: " + array.toString());

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
            query = query + "\"" + packetId + "\") ";
            database.execSQL(query);
            Log.d(TAG, query);
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
                                ", monitoring_status = " + cursor.getString(monitoring_status);
                Log.d(TAG, "Got from DB - channel " + i + ": " + message);
                i++;
            }
        }

        cursor.close();
        database.close();
    }

    public void loadChannelsFromDB() {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
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
                int packerId = cursor.getColumnIndex(DBHelper.CHANNEL_PACKET_ID);

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
                                ", packetId = " + cursor.getString(packerId);
                Log.d(TAG, "Got from DB - channel " + i + ": " + message);
                i++;

                final TvChannelListModel model = new TvChannelListModel();
                model.setId(cursor.getString(id));
                model.setTitle(cursor.getString(name));
                model.setGenre(cursor.getString(genre_id));
                model.setNumber(cursor.getString(number));
                model.setUrl(cursor.getString(url));

                if (cursor.getInt(archive)==0) {
                    model.setArchive(false);
                } else {
                    model.setArchive(true);
                }

                model.setArchiveRange(cursor.getString(archive_range));

                if (cursor.getString(pvr).equals("false")) {
                    model.setPvr(false);
                } else {
                    model.setPvr(true);
                }

                if (cursor.getString(censored).equals("false")) {
                    model.setCensored(false);
                } else {
                    model.setCensored(true);
                }

                if (cursor.getString(favorite).equals("false")) {
                    model.setFavorite(false);
                } else {
                    model.setFavorite(true);
                }

                model.setLogo(cursor.getString(logo).replace("\\", ""));

                if (cursor.getString(monitoring_status).equals("false")) {
                    model.setAvailable(false);
                } else {
                    model.setAvailable(true);
                }
                model.setPacketId(cursor.getString(packerId));
                ChannelsFragment.channelsListArray.add(model);
            }
            ChannelsFragment.adapter = new TvChannelsAdaptor(ChannelsFragment.channelsListViewActivity, ChannelsFragment.channelsListArray, resources);
            ChannelsFragment.channels.setAdapter(ChannelsFragment.adapter);
            converter.setListViewHeightBasedOnChildren(ChannelsFragment.channels);
        }
        cursor.close();
        database.close();
    }

    public void resetChannelsData() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("DELETE FROM " + DBHelper.CHANNELS_TABLE + " ;");
        database.execSQL("INSERT INTO " + DBHelper.CHANNELS_TABLE + " VALUES (\"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\" )");
        database.close();
    }
}
