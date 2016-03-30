package tv.starcards.starcardstv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import cn.pedant.SweetAlert.SweetAlertDialog;
import tv.starcards.starcardstv.application.API;
import tv.starcards.starcardstv.application.data.channelsdata.ChannelsData;
import tv.starcards.starcardstv.application.data.channelsdata.ChannelsDataRequest;
import tv.starcards.starcardstv.application.data.db.DBHelper;
import tv.starcards.starcardstv.application.data.packetdata.PacketData;
import tv.starcards.starcardstv.application.exceptions.NoDataException;
import tv.starcards.starcardstv.application.http.HttpGetWithPacketToken;
import tv.starcards.starcardstv.application.ui.fragments.CabinetFragment;
import tv.starcards.starcardstv.application.ui.fragments.ChannelsFragment;
import tv.starcards.starcardstv.application.ui.fragments.ContactsFragment;
import tv.starcards.starcardstv.application.ui.fragments.MessageFragment;
import tv.starcards.starcardstv.application.ui.fragments.SettingsFragment;
import tv.starcards.starcardstv.application.data.userdata.UserData;
import tv.starcards.starcardstv.application.data.state.IsLogged;
import tv.starcards.starcardstv.application.data.state.IsLoggedByPacket;
import tv.starcards.starcardstv.application.ui.models.PacketListModel;
import tv.starcards.starcardstv.application.ui.models.TvChannelListModel;
import tv.starcards.starcardstv.application.ui.vlc.FullscreenVlcPlayer;
import tv.starcards.starcardstv.application.util.NetWorkState;
import tv.starcards.starcardstv.application.util.Parser;

public class MainScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static NavigationView   navigationView;

    public static TextView         email;
    public static TextView         name;
    public static TextView         balance;
    public static TextView         bonus;

    public static boolean          justEntered = true;
    public static boolean          firstTimeChannelsLoad = true;

    private static final int       CHANNELS_ID = 0;
    private static final int       CABINET_ID = 1;
    private static final int       MESSAGE_ID = 2;
    private static final int       SETTINGS_ID = 3;
    private static final int       CONTACTS_ID = 4;

    private static final String    TAG = "MainScreenActivity";

    private ViewPager              viewPager;
    private ViewPagerAdapter       adapter;
    private String                 packetPassword;
    private String                 packetId;
    private String                 chosenPacketTitle;
    private TextView               toolbarText;
    private ChannelsDataRequest    channelsData;

    public static SweetAlertDialog pDialog;
    private NetWorkState state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_toolbar);
        setSupportActionBar(toolbar);

        toolbarText = (TextView) findViewById(R.id.toolbar_text);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        email = (TextView) header.findViewById(R.id.nav_header_user_email);
        email.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        name = (TextView) header.findViewById(R.id.nav_header_user_name);
        balance = (TextView) header.findViewById(R.id.nav_header_user_balance);
        bonus = (TextView) header.findViewById(R.id.nav_header_user_bonus);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        channelsData = new ChannelsDataRequest(this, packetId, getResources());

        UserData.getInstance().initUserData(this);
        PacketData.getInstance().initPacketData(this, getResources());
        ChannelsData.getInstance().initChannelsData(this, getResources());

        state = new NetWorkState(this);

        pDialog = new SweetAlertDialog(MainScreenActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
    }

    public void onResume() {
        super.onResume();
        if (state.networkIsAvailable()) {
            UserData.getInstance().initLoggedState();
            if (IsLogged.getInstance().isLogged()) {
                Log.w(TAG, "Inside on Resume: Brunch " + IsLogged.getInstance().isLogged());
                UserData.getInstance().initLoginTokens();
                UserData.getInstance().initUserInfo();
                PacketData.getInstance().initLoggedByPacketState();
                if (IsLoggedByPacket.getInstance().isLogged()) {
                    PacketData.getInstance().initPacketTokens();
                    try {
                        packetId = PacketData.getInstance().getPacketId();
                        packetPassword = PacketData.getInstance().getPacketPassword();
                        navigationView.getMenu().getItem(0).setChecked(true);
                        onNavigationItemSelected(navigationView.getMenu().getItem(0));
                    } catch (NoDataException e) {
                        navigationView.getMenu().getItem(1).setChecked(true);
                        onNavigationItemSelected(navigationView.getMenu().getItem(1));
                    }
                } else {
                    navigationView.getMenu().getItem(1).setChecked(true);
                    onNavigationItemSelected(navigationView.getMenu().getItem(1));
                    Log.w(TAG, "IS LOGGED BY PACKET: " + String.valueOf(IsLoggedByPacket.getInstance().isLogged()));
                }
            } else {
                Log.w(TAG, "Inside on Resume: Brunch " + IsLogged.getInstance().isLogged());
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
            }
        } else {
            showNetworkStateError();
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public void clearAll() {
            FragmentManager fragMan = getSupportFragmentManager();
            for (int i = 0; i < mFragmentList.size(); i++)
                fragMan.beginTransaction().remove(mFragmentList.get(i)).commit();
            mFragmentList.clear();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_current_packet) {
            if (packetId == null) {
                toolbarText.setText("Выберите пакет");
                adapter.clearAll();
                setupViewPager(viewPager, CABINET_ID);
            } else {
                toolbarText.setText("Пакет : " + packetId);
                adapter.clearAll();
                setupViewPager(viewPager, CHANNELS_ID);
            }
        } else if (id == R.id.nav_cabinet) {
            toolbarText.setText("Кабинет");
            adapter.clearAll();
            setupViewPager(viewPager, CABINET_ID);
        } else if (id == R.id.nav_messages) {
            toolbarText.setText("Сообщения");
            adapter.clearAll();
            setupViewPager(viewPager, MESSAGE_ID);
        } else if (id == R.id.nav_settings) {
            toolbarText.setText("Настройки");
            adapter.clearAll();
            setupViewPager(viewPager, SETTINGS_ID);
        } else if (id == R.id.nav_info) {
            toolbarText.setText("Контакты");
            adapter.clearAll();
            setupViewPager(viewPager, CONTACTS_ID);
        } else if (id == R.id.nav_quit) {
            UserData.getInstance().resetUserData();
            PacketData.getInstance().resetPacketData();
            ChannelsData.getInstance().resetChannelsData();
            Intent toLoginScreen = new Intent(this, Login.class);
            startActivity(toLoginScreen);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager, int id) {
        adapter.clearAll();
        viewPager.setAdapter(null);
        Fragment fragment;
        switch (id) {
            case 0:
                Bundle bundle = new Bundle();
                bundle.putString("packetId", packetId);
                fragment = new ChannelsFragment();
                fragment.setArguments(bundle);
                break;
            case 1:
                fragment = new CabinetFragment();
                break;
            case 2:
                fragment = new MessageFragment();
                break;
            case 3:
                fragment = new SettingsFragment();
                break;
            case 4:
                fragment = new ContactsFragment();
                break;
            default:
                fragment = new CabinetFragment();
                break;
        }
        adapter.addFragment(fragment, "FRAGMENT");
        viewPager.setAdapter(adapter);
    }

    public void onPacketsItemClick(int mPosition) {
        PacketListModel tempValues = CabinetFragment.packetListArray.get(mPosition);
        chosenPacketTitle = tempValues.getName();
        packetId = PacketData.getInstance().getPacketData(chosenPacketTitle, DBHelper.PACKET_ID);
        packetPassword = PacketData.getInstance().getPacketData(chosenPacketTitle, DBHelper.PACKET_PASSWORD);
        firstTimeChannelsLoad = true;
        loginInPacket();
    }

    public void onPacketItemLongClick(int position) {
        PacketListModel values = CabinetFragment.packetListArray.get(position);
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
        new SweetAlertDialog(this)
                .setTitleText(values.getName())
                .setContentText("Пакет доступен до\n" + values.getDate() + "\n\n"
                        + "Для доступа к пакету используйте \n"
                        + "Id пакета - " + values.getId() + "\n"
                        + "Пароль пакета - " + values.getPassword())
                .show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void setPacketTokensAndShowChannels(String response) {
        PacketData.getInstance().setPacketTokens(response);
        PacketData.getInstance().setLoggedByPacket();
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
    }

    public void onStarcardsPlayerClick(int position) {
        TvChannelListModel values = ChannelsFragment.channelsListArray.get(position);
        Intent intent = new Intent(this, FullscreenVlcPlayer.class);
        intent.putExtra("id", values.getId());
//        intent.putExtra("name", values.getTitle());
//        intent.putExtra("genre", values.getGenre());
//        intent.putExtra("number", values.getNumber());
//        intent.putExtra("ico", values.getLogo());
//        intent.putExtra("censored", String.valueOf(values.isCensored()));
//        intent.putExtra("available", String.valueOf(values.isAvailable()));
//        intent.putExtra("archivable", String.valueOf(values.isArchivable()));
//        intent.putExtra("favorite", String.valueOf(values.isFavorite()));
        intent.putExtra("url", values.getUrl());
        intent.putExtra("packetId", packetId);
        startActivity(intent);
    }

    public void onVLCPlayerClick(int position) {
        TvChannelListModel values = ChannelsFragment.channelsListArray.get(position);
        String url = API.PACKETS_SUMMARY + "/" + packetId + API.TV_CHANNELS + "/" + values.getId() + API.LINK;
        if (values.getUrl().equals("false")) {
            requestUrl(url);
        } else {
            goToVLCPlayer(values.getUrl());
        }
    }

    private void requestUrl(String url) {
        Log.d(TAG, "Request URL = " + url);
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest req = new HttpGetWithPacketToken(Request.Method.GET, url , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            handleUrl(response);
                        } catch (JSONException e) {
                            Log.w("JSONException", "wrong parsing in fillPackets");
                        }
                        Log.w("TV LINK", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Packet request error", error.toString());
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        rq.add(req);
    }

    private void handleUrl(JSONObject response) throws JSONException {
        Parser parser = new Parser();
        String result = response.getString("results");
        String url = parser.parse(result, "url");
        Log.w("url", url);
        goToVLCPlayer(url);
    }

    private void goToVLCPlayer(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("org.videolan.vlc");
        intent.setDataAndType(Uri.parse(url), "video/*");
        startActivity(intent);
    }

    public void onChannelItemLongClick(int position) {
        TvChannelListModel value = ChannelsFragment.channelsListArray.get(position);
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
        SweetAlertDialog dialog = new SweetAlertDialog(MainScreenActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
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
        Picasso.with(MainScreenActivity.this).load(value.getLogo()).into(img);
    }

    private void showNetworkStateError() {
        new SweetAlertDialog(MainScreenActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Невозможно")
                .setContentText("Необходимо подключение к интернету")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .show();

    }
}


