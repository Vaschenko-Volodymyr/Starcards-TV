package tv.starcards.starcardstv.application.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by time on 14.03.16.
 */
public class NetWorkState {

    private Context context;

    public NetWorkState(Context context) {
        this.context = context;
    }

    public boolean networkIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
