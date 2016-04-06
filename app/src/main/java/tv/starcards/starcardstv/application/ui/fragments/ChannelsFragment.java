package tv.starcards.starcardstv.application.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.squareup.picasso.Picasso;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.R;
import tv.starcards.starcardstv.application.data.channelsdata.ChannelsData;
import tv.starcards.starcardstv.application.data.db.DBHelper;
import tv.starcards.starcardstv.application.data.state.IsLoggedByPacket;
import tv.starcards.starcardstv.application.ui.adaptors.TvChannelsAdaptor;
import tv.starcards.starcardstv.application.ui.models.TvChannelListModel;
import tv.starcards.starcardstv.application.data.channelsdata.ChannelsDataRequest;
import tv.starcards.starcardstv.application.util.SearchToolbarUi;
import tv.starcards.starcardstv.application.widgets.Fab;

public class ChannelsFragment extends Fragment {

    public static MaterialSheetFab              materialSheetFab;
    public static TvChannelsAdaptor             adapter;
    public static Activity                      channelsListViewActivity = null;
    public static ArrayList<TvChannelListModel> channelsListArray;
    public static ListView                      channels;
    public static PullToRefreshView             mChannelsPullToRefreshView;
    public static Fab                           fab;
    public static ImageView                     notFoundImg;
    public static TextView                      notFoundText;

    public static ChannelsFragment              instance;

    private static final String                 TAG = "ChannelsFragment";
    public static boolean                       searchIsVisible = false;

    private String                              packetId;
    private ChannelsDataRequest                 channelsData;

    Resources                                   resources;

    public ChannelsFragment() {
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        instance = this;
        channelsListViewActivity = getActivity();
        resources = getResources();
        adapter = new TvChannelsAdaptor(this, channelsListArray, resources);
        adapter = null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        packetId = getArguments().getString("packetId");
        View v = inflater.inflate(R.layout.fragment_channels_activity, container, false);

        fab = (Fab) v.findViewById(R.id.fab);
        View sheetView = v.findViewById(R.id.fab_sheet);
        View overlay = v.findViewById(R.id.overlay);
        notFoundImg = (ImageView) v.findViewById(R.id.channels_not_found_img);
        notFoundText = (TextView) v.findViewById(R.id.channels_not_found_text);
        int sheetColor = getResources().getColor(R.color.colorPrimary);
        int fabColor = getResources().getColor(R.color.colorPrimaryDark);
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);
        channelsData = new ChannelsDataRequest(getContext(), packetId, resources);
        channelsListArray = new ArrayList<>();
        channels = (ListView) v.findViewById(R.id.tv_channels);
        mChannelsPullToRefreshView = (PullToRefreshView) v.findViewById(R.id.channels_refresh_layout);

        SearchToolbarUi.resetToolbar(getActivity());
        MainScreenActivity.searchImage.setVisibility(View.VISIBLE);

        MainScreenActivity.searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.search_image_animation);
                MainScreenActivity.searchImage.setAnimation(animation);
                searchIsVisible = SearchToolbarUi.changeSearchToolbarUI(getActivity(), searchIsVisible);
            }
        });

        MainScreenActivity.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ChannelsData.getInstance().loadChannelsFromDB(DBHelper.CHANNEL_NAME, s);
            }
        });

        // Initialize material sheet FAB
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                // Called when the material sheet's "show" animation starts.
            }

            @Override
            public void onSheetShown() {
                // Called when the material sheet's "show" animation ends.
            }

            @Override
            public void onHideSheet() {
                // Called when the material sheet's "hide" animation starts.
            }

            public void onSheetHidden() {
                // Called when the material sheet's "hide" animation ends.
            }
        });

        Log.w(TAG, "packetID = " + packetId);

        mChannelsPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mChannelsPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ChannelsData.getInstance().resetChannelsData();
                        requestChannelsData();
                        mChannelsPullToRefreshView.setRefreshing(false);
                        mChannelsPullToRefreshView.clearDisappearingChildren();
                    }
                }, 100);
            }
        });

        if (!IsLoggedByPacket.getInstance().isLogged() || MainScreenActivity.justEntered) {
            MainScreenActivity.justEntered = false;
            MainScreenActivity.pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            MainScreenActivity.pDialog.setTitleText("Loading");
            MainScreenActivity.pDialog.show();
            ChannelsData.getInstance().resetChannelsData();
            requestChannelsData();
        } else {
            ChannelsData.getInstance().loadChannelsFromDB();
        }

        return v;
    }

    private void requestChannelsData() {
        channelsListArray.clear();
        channelsData.fillChannels();
    }

    public void onChannelItemLongClick(int position) {
        TvChannelListModel value = ChannelsFragment.channelsListArray.get(position);
        Vibrator v = (Vibrator) this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
        SweetAlertDialog dialog = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        String title = value.getTitle();
        if (value.isFavorite()) {
            title = title + "(Ваш любимый)";
        }
        dialog.setTitleText(title);
        String message = "Канал номер " + value.getNumber() + ".\n"
                + "Жанр канала - " + value.getGenre() + ".\n";

        if (value.isCensored()) {
            message = message + "Канал для взрослых.\n";
        } else {
            message = message + "Канал без возрастных ограничений.\n";
        }

        if ((value.isArchivable())) {
            message = message + "Канал архивируется.\n";
        } else {
            message = message + ".Канал не архивируется\n";
        }

        if (value.isAvailable()) {
            message = message + "Канал доступен.\n";
        } else {
            message = message + "Канал временно недоступен.\n";
        }
        dialog.setContentText(message);
        dialog.setCustomImage(getResources().getDrawable(R.drawable.toolbar_back_arrow));
        dialog.show();
        TextView tv = (TextView) dialog.findViewById(cn.pedant.SweetAlert.R.id.content_text);
        tv.setGravity(Gravity.START);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        ImageView img = (ImageView) dialog.findViewById(cn.pedant.SweetAlert.R.id.custom_image);
        img.setMinimumWidth(50);
        img.setMinimumHeight(50);
        Picasso.with(this.getActivity()).load(value.getLogo()).into(img);
    }
}
