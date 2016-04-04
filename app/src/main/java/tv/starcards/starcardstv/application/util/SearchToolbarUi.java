package tv.starcards.starcardstv.application.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.R;
import tv.starcards.starcardstv.application.ui.fragments.ChannelsFragment;

public class SearchToolbarUi {

    public static void changeSearchToolbarUI(Activity activity, boolean searchIsVisible) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!searchIsVisible) {
            ChannelsFragment.fab.setVisibility(View.INVISIBLE);
            MainScreenActivity.search.setVisibility(View.VISIBLE);

            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            MainScreenActivity.toolbarText.setVisibility(View.INVISIBLE);
            MainScreenActivity.toolbar.setBackgroundColor(activity.getResources().getColor(R.color.lightColorPrimary));
            MainScreenActivity.search.requestFocus();
        } else {
            imm.hideSoftInputFromWindow(MainScreenActivity.search.getWindowToken(), 0);
            MainScreenActivity.search.setVisibility(View.INVISIBLE);
            MainScreenActivity.toolbarText.setVisibility(View.VISIBLE);
            MainScreenActivity.toolbar.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
            ChannelsFragment.fab.setVisibility(View.VISIBLE);
        }
        ChannelsFragment.searchIsVisible = !ChannelsFragment.searchIsVisible;
    }
}
