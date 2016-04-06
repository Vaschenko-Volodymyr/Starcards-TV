package tv.starcards.starcardstv.application.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.R;
import tv.starcards.starcardstv.application.data.db.DBHelper;
import tv.starcards.starcardstv.application.data.packetdata.PacketData;
import tv.starcards.starcardstv.application.data.state.IsLogged;
import tv.starcards.starcardstv.application.http.GetBearerLogin;
import tv.starcards.starcardstv.application.ui.adaptors.PacketAdaptor;
import tv.starcards.starcardstv.application.ui.models.PacketListModel;
import tv.starcards.starcardstv.application.util.SearchToolbarUi;

public class CabinetFragment extends Fragment {

    public static Activity packetListViewActivity = null;
    public static ArrayList<PacketListModel> packetListArray;
    public static PacketAdaptor     adapter;
    public static ListView          packets;
    public static PullToRefreshView mCabinetPullToRefreshView;

    public static CabinetFragment   instance;

    private static final String     TAG = "CabinetFragment";

    private Resources               resources;
    private GetBearerLogin          request;

    private String                  packetPassword;
    private String                  packetId;
    private String                  packetTitle;
    private String                  chosenPacketTitle;

    public CabinetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        packetListViewActivity = getActivity();
        resources = getResources();
        adapter = new PacketAdaptor(this, packetListArray, resources);
        adapter = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cabinet_activity, container, false);

        SearchToolbarUi.resetToolbar(getActivity());

        request = new GetBearerLogin(getContext());

        packetListArray = new ArrayList<>();
        packets = (ListView) v.findViewById(R.id.packets);
        mCabinetPullToRefreshView = (PullToRefreshView) v.findViewById(R.id.cabinet_refresh_layout);

        mCabinetPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCabinetPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PacketData.getInstance().resetPacketData();
                        requestPacketData();
                        mCabinetPullToRefreshView.setRefreshing(false);
                        mCabinetPullToRefreshView.clearDisappearingChildren();
                        Log.d(TAG, "End of refreshing");
                    }
                }, 100);
            }
        });

        if ((!IsLogged.getInstance().isLogged() || MainScreenActivity.justEntered)) {
            MainScreenActivity.justEntered = false;
            MainScreenActivity.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            MainScreenActivity.pDialog.setTitleText("Loading");
            MainScreenActivity.pDialog.show();
            PacketData.getInstance().resetPacketData();
            requestPacketData();
        } else {
            PacketData.getInstance().loadPacketsFromDB();
        }

        return v;
    }

    private void requestPacketData() {
        packetListArray.clear();
        request.doRequest(GetBearerLogin.PACKETS_INFO_REQUEST);
    }

    public void onPacketsItemClick(int mPosition) {
        PacketListModel tempValues = CabinetFragment.packetListArray.get(mPosition);
        chosenPacketTitle = tempValues.getName();
        packetId = PacketData.getInstance().getPacketData(chosenPacketTitle, DBHelper.PACKET_ID);
        packetPassword = PacketData.getInstance().getPacketData(chosenPacketTitle, DBHelper.PACKET_PASSWORD);
        loginInPacket();
    }

    private void loginInPacket() {
        String packetLoginUrl = "http://api28.starcards.tv/user/token";
        final String contentType = "Content-Type";
        final String contentTypeParameter = "application/x-www-form-urlencoded";
        final String type = "grant_type";
        final String password = "password";
        final String keyUsername = "username";
        final String keyPassword = "password";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, packetLoginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, "loginInPacket(): Server is reachable. " + response);
                        if (response.contains("ERROR")) {
                            Log.e(TAG, "loginInPacket(): Access denied. See details: " + response);
                        } else if (response.contains("access_token")) {
                            Log.d(TAG, "loginInPacket(): Access accepted. See details: " + response);
                            setPacketTokensAndShowChannels(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put(contentType, contentTypeParameter);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(type, password);
                params.put(keyUsername, packetId);
                params.put(keyPassword, packetPassword);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void setPacketTokensAndShowChannels(String response) {
        PacketData.getInstance().setPacketTokens(response);
        PacketData.getInstance().setLoggedByPacket();
        MainScreenActivity.navigationView.getMenu().getItem(0).setChecked(true);
        MainScreenActivity.instance.onNavigationItemSelected(MainScreenActivity.navigationView.getMenu().getItem(0));
    }

    public void onPacketItemLongClick(int position) {
        PacketListModel values = CabinetFragment.packetListArray.get(position);
        Vibrator v = (Vibrator) this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
        new SweetAlertDialog(MainScreenActivity.instance)
                .setTitleText(values.getName())
                .setContentText("Пакет доступен до\n" + values.getDate() + "\n\n"
                        + "Для доступа к пакету используйте \n"
                        + "Id пакета - " + values.getId() + "\n"
                        + "Пароль пакета - " + values.getPassword())
                .show();
    }
}