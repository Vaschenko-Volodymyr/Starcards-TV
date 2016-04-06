package tv.starcards.starcardstv.application.ui.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;

import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.R;
import tv.starcards.starcardstv.application.data.packetdata.PacketData;
import tv.starcards.starcardstv.application.data.state.IsLogged;
import tv.starcards.starcardstv.application.http.GetBearerLogin;
import tv.starcards.starcardstv.application.ui.adaptors.PacketAdaptor;
import tv.starcards.starcardstv.application.ui.models.PacketListModel;
import tv.starcards.starcardstv.application.util.SearchToolbarUi;

public class CabinetFragment extends Fragment {

    public static Activity packetListViewActivity = null;
    public static ArrayList<PacketListModel> packetListArray;
    public static PacketAdaptor adapter;
    public static ListView packets;
    public static PullToRefreshView mCabinetPullToRefreshView;

    private static final String TAG = "CabinetFragment";

    private Resources resources;
    private GetBearerLogin request;

    public CabinetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        packetListViewActivity = getActivity();
        resources = getResources();
        adapter = new PacketAdaptor(packetListViewActivity, packetListArray, resources);
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
}