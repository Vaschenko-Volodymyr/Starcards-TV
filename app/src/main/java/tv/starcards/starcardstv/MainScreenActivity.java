package tv.starcards.starcardstv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import tv.starcards.starcardstv.application.data.channelsdata.ChannelsData;
import tv.starcards.starcardstv.application.data.packetdata.PacketData;
import tv.starcards.starcardstv.application.data.state.SavedState;
import tv.starcards.starcardstv.application.exceptions.NoDataException;
import tv.starcards.starcardstv.application.ui.fragments.CabinetFragment;
import tv.starcards.starcardstv.application.ui.fragments.ChannelsFragment;
import tv.starcards.starcardstv.application.ui.fragments.ContactsFragment;
import tv.starcards.starcardstv.application.ui.fragments.MessageFragment;
import tv.starcards.starcardstv.application.ui.fragments.SettingsFragment;
import tv.starcards.starcardstv.application.data.userdata.UserData;
import tv.starcards.starcardstv.application.data.state.IsLogged;
import tv.starcards.starcardstv.application.data.state.IsLoggedByPacket;
import tv.starcards.starcardstv.application.util.NetWorkState;
import tv.starcards.starcardstv.application.util.SearchToolbarUi;

public class MainScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String      TAG = "Starcards-Tv App. ";
    public static Context            sApplicationContext;
    public static MainScreenActivity instance;
// __________________________________________________________________
    public static NavigationView   navigationView;
    public static SweetAlertDialog pDialog;

    private DrawerLayout           drawer;
    public static Toolbar          toolbar;
    public static EditText         search;
    public static ImageView        searchImage;
    public static TextView         toolbarText;

    public static TextView         email;
    public static TextView         name;
    public static TextView         balance;
    public static TextView         bonus;

//    public static String           packetPassword;
    public static String           packetId;
    public static String           packetTitle;

    public static boolean          justEntered = false;

    private static final int       CHANNELS_ID = 0;
    private static final int       CABINET_ID = 1;
    private static final int       MESSAGE_ID = 2;
    private static final int       SETTINGS_ID = 3;
    private static final int       CONTACTS_ID = 4;

    private ViewPager              viewPager;
    private ViewPagerAdapter       adapter;
    private NetWorkState           state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);

        instance = this;
        sApplicationContext = getApplicationContext();

        // Toolbar widgets
        toolbar = (Toolbar) findViewById(R.id.app_bar_toolbar);
        toolbarText = (TextView) findViewById(R.id.toolbar_text);
        search = (EditText) findViewById(R.id.toolbar_search);
        searchImage = (ImageView) findViewById(R.id.toolbar_search_img);

        // Drawer widgets

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);
        email = (TextView) header.findViewById(R.id.nav_header_user_email);
        email.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        name = (TextView) header.findViewById(R.id.nav_header_user_name);
        balance = (TextView) header.findViewById(R.id.nav_header_user_balance);
        bonus = (TextView) header.findViewById(R.id.nav_header_user_bonus);

        // Screen widgets
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Handling widgets
        setSupportActionBar(toolbar);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Other inits and actions
        UserData.getInstance().initUserData(this);
        PacketData.getInstance().initPacketData(this, getResources());
        ChannelsData.getInstance().initChannelsData(this, getResources());

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pDialog = new SweetAlertDialog(MainScreenActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        state = new NetWorkState(this);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
    }

    public void onResume() {
        super.onResume();

        if (state.networkIsAvailable()) {
            UserData.getInstance().initLoggedState();
            UserData.getInstance().loadUserData();
            if (SavedState.getInstance().isFirstTimeEntered()) {
                if (IsLogged.getInstance().isLogged()) {
                    Log.w(TAG, "Inside on Resume: Brunch " + IsLogged.getInstance().isLogged());
                    UserData.getInstance().initLoginTokens();
                    UserData.getInstance().initUserInfo();
                    PacketData.getInstance().initLoggedByPacketState();
                    if (IsLoggedByPacket.getInstance().isLogged()) {
                        PacketData.getInstance().initPacketTokens();
                        try {
                            packetId = PacketData.getInstance().getPacketId();
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
                setupViewPager(viewPager, SavedState.getInstance().getParagraph());
            }

        } else {
            showNetworkStateError();
        }
    }

    public void onSaveInstanceState(Bundle bundle) {

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
        } else if (ChannelsFragment.searchIsVisible) {
            ChannelsFragment.searchIsVisible = SearchToolbarUi.changeSearchToolbarUI(this, ChannelsFragment.searchIsVisible);
        } else {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_current_packet) {
            if (packetTitle == null) {
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.toolbar_text_shacking);
                toolbarText.setText("Выберите пакет");
                toolbarText.startAnimation(animation);
                adapter.clearAll();
                setupViewPager(viewPager, CABINET_ID);
            } else {
                toolbarText.setText("Пакет : " + packetTitle);
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
        SavedState.getInstance().setParagraph(id);
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

    public static Context getContext() {
        return sApplicationContext;
    }

    public static String AppTag() {
        return TAG;
    }

}