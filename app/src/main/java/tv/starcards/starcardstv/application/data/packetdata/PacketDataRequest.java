package tv.starcards.starcardstv.application.data.packetdata;

import android.content.Context;
import android.content.res.Resources;
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

import java.util.Date;

import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.application.API;
import tv.starcards.starcardstv.application.ui.adaptors.PacketAdaptor;
import tv.starcards.starcardstv.application.ui.fragments.CabinetFragment;
import tv.starcards.starcardstv.application.ui.models.PacketListModel;
import tv.starcards.starcardstv.application.http.HttpGetWithLoginToken;
import tv.starcards.starcardstv.application.util.DateConverter;
import tv.starcards.starcardstv.application.util.ListViewConverter;

public class PacketDataRequest {

    public static final String  TAG = "PacketDataRequest";

    private Context             context;
    private Resources           resources;
    private DateConverter       dateConverter;
    private ListViewConverter   listViewConverter;

    public PacketDataRequest(Context context, Resources resources) {
        this.context = context;
        this.resources = resources;
        dateConverter = new DateConverter();
        listViewConverter = new ListViewConverter();
    }

    public void fillPackets() {
        requestPackets();
    }

    private void requestPackets() {
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest req = new HttpGetWithLoginToken(Request.Method.GET, API.PACKETS_SUMMARY, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.toString().contains("error")) {
                            Log.d(TAG, "Error" + response);
//                            RefreshLoginToken refresh = new RefreshLoginToken(context);
//                            refresh.refreshLoginToken();
//                            requestPackets();
                        } else {
                            try {
                                if (response.toString().contains("results")) {
                                    PacketData.getInstance().savePacketInfo(response);
                                    PacketData.getInstance().loadPacketsFromDB();
                                    MainScreenActivity.pDialog.dismiss();
                                } else {
                                    Log.d(TAG, response.toString());
                                }
                            } catch (JSONException e) {
                                Log.w(TAG, "JSONException: wrong parsing in fillPackets");
                            }
                        }
                        Log.w(TAG, "Packets response: " + response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error... " + error);
                        VolleyLog.e(TAG, "Error... " + error.getMessage());
                        MainScreenActivity.pDialog.dismiss();
                    }
                });
        rq.add(req);
    }
}
