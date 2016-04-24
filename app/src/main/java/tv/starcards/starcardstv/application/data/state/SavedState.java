package tv.starcards.starcardstv.application.data.state;

import android.content.Context;
import android.content.SharedPreferences;

import tv.starcards.starcardstv.MainScreenActivity;

public class SavedState {
    private static final String TAG = SavedState.class.getSimpleName();
    private static SavedState ourInstance = new SavedState();
    private boolean firstTimeEntered = true;

    private static final String PARAGRAPH = "paragraph";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;

    private SavedState() {
        mSharedPreferences = MainScreenActivity.getContext().getSharedPreferences(MainScreenActivity.AppTag(), Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static SavedState getInstance() {
        return ourInstance;
    }

    public void setParagraph(int id) {
        editor.putInt(PARAGRAPH, id);
        editor.apply();
    }

    public int getParagraph() {
        return mSharedPreferences.getInt(PARAGRAPH, 0);
    }

    public boolean isFirstTimeEntered() {
        if (firstTimeEntered) {
            firstTimeEntered=!firstTimeEntered;
            return !firstTimeEntered;
        } else {
            return firstTimeEntered;
        }
    }
}
