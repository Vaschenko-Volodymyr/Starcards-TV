package tv.starcards.starcardstv.application.data.channelsdata;


import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.application.API;
import tv.starcards.starcardstv.application.data.channelsdata.ChannelsData;
import tv.starcards.starcardstv.application.data.db.DBHelper;
import tv.starcards.starcardstv.application.ui.adaptors.TvChannelsAdaptor;
import tv.starcards.starcardstv.application.ui.fragments.ChannelsFragment;
import tv.starcards.starcardstv.application.ui.models.TvChannelListModel;
import tv.starcards.starcardstv.application.http.HttpGetWithPacketToken;
import tv.starcards.starcardstv.application.util.ListViewConverter;

public class ChannelsDataRequest {

    public static final  String  TAG = "ChannelsDataRequest";

    private String              packetId;
    private Context             context;
    private Resources           resources;
    private DBHelper            dbHelper;
    private ListViewConverter   converter;

    public ChannelsDataRequest(Context context, String packetId, Resources resources) {
        this.context = context;
        this.packetId = packetId;
        this.resources = resources;
        this.dbHelper = new DBHelper(context);
        converter = new ListViewConverter();
    }

    public void fillChannels() {
        requestPacketTvChannels();
    }

    private void requestPacketTvChannels() {
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest req = new HttpGetWithPacketToken(Request.Method.GET, API.PACKETS_SUMMARY + "/" + packetId + API.TV_CHANNELS, null,
            new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ChannelsData.getInstance().saveChannelsToDB(response, packetId);
                    ChannelsData.getInstance().loadChannelsFromDB();
                    MainScreenActivity.pDialog.dismiss();
                } catch (JSONException e) {
                    Log.w(TAG, "Wrong parsing in fillPackets");
                }
            }
        },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Packet request error: " + error.toString());
                VolleyLog.e(TAG, "Error... " + error.getMessage());
                MainScreenActivity.pDialog.dismiss();
//                RefreshLoginToken refresh = new RefreshLoginToken(context);
//                refresh.refreshLoginToken();
//                requestPacketTvChannels();
                }
            }
        );
        rq.add(req);
    }
}
