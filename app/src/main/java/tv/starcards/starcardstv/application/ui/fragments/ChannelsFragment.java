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
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;

import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.R;
import tv.starcards.starcardstv.application.data.channelsdata.ChannelsData;
import tv.starcards.starcardstv.application.data.state.IsLoggedByPacket;
import tv.starcards.starcardstv.application.ui.adaptors.TvChannelsAdaptor;
import tv.starcards.starcardstv.application.ui.models.TvChannelListModel;
import tv.starcards.starcardstv.application.data.channelsdata.ChannelsDataRequest;

public class ChannelsFragment extends Fragment {

    public static TvChannelsAdaptor             adapter;
    public static Activity                      channelsListViewActivity = null;
    public static ArrayList<TvChannelListModel> channelsListArray;
    public static ListView                      channels;
    public static PullToRefreshView             mPullToRefreshView;

    private static final String TAG = "ChannelsFragment";

    private String                              packetId;
    private ChannelsDataRequest                 channelsData;

    Resources                                   resources;
    TextView                                    search;

    public ChannelsFragment() {
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        channelsListViewActivity = getActivity();
        resources = getResources();
        adapter = new TvChannelsAdaptor(channelsListViewActivity, channelsListArray, resources);
        adapter = null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        packetId = getArguments().getString("packetId");
        View v = inflater.inflate(R.layout.fragment_channels_activity, container, false);

        channelsData = new ChannelsDataRequest(getContext(), packetId, resources);

        Log.w(TAG, "packetID = " + packetId);

        channelsListArray = new ArrayList<>();
        channels = (ListView) v.findViewById(R.id.tv_channels);

        mPullToRefreshView = (PullToRefreshView) v.findViewById(R.id.channels_refresh_layout);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ChannelsData.getInstance().resetChannelsData();
                        requestChannelsData();
                        mPullToRefreshView.setRefreshing(false);
                        mPullToRefreshView.clearDisappearingChildren();
                    }
                }, 300);
            }
        });

        if (!IsLoggedByPacket.getInstance().isLogged()) {
            MainScreenActivity.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            MainScreenActivity.pDialog.setTitleText("Loading");
            MainScreenActivity.pDialog.show();
            ChannelsData.getInstance().resetChannelsData();
            ChannelsData.getInstance().resetChannelsData();
            requestChannelsData();
        } else {
            ChannelsData.getInstance().loadChannelsFromDB();
        }

//        fab = (FloatingActionButton) v.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                fab.setImageDrawable(getResources().getDrawable(R.mipmap.channels_filter_visible));
//                Snackbar.make(view, "Тут будут фильтры по каналам", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//
//            }
//        });
        search = (TextView) v.findViewById(R.id.channels_toolbar_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("Search in process");
            }
        });
        return v;
    }

    private void requestChannelsData() {
        channelsListArray.clear();
        channelsData.fillChannels();
    }
}
