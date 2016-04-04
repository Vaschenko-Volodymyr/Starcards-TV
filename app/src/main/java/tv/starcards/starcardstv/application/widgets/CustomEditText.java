package tv.starcards.starcardstv.application.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.application.util.SearchToolbarUi;

public class CustomEditText extends EditText {

    Context context;

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP) {
            SearchToolbarUi.changeSearchToolbarUI(MainScreenActivity.instance, View.VISIBLE==this.getVisibility());
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
}
