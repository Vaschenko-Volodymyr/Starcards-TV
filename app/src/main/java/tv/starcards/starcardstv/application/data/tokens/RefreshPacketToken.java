package tv.starcards.starcardstv.application.data.tokens;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import tv.starcards.starcardstv.application.API;
import tv.starcards.starcardstv.application.data.packetdata.PacketData;
import tv.starcards.starcardstv.application.data.userdata.UserData;
import tv.starcards.starcardstv.application.http.HttpRefreshLoginToken;

public class RefreshPacketToken {

    private static final String TAG = "RefreshPacketToken";

    private Context context;

    public RefreshPacketToken(Context context) {
        this.context = context;
    }

    public void refreshPacketToken() {
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest req = new HttpRefreshLoginToken(Request.Method.POST, API.AUTH, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        setNewAccessLoginToken(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error during refreshing packet access token");
                    }
                });
        rq.add(req);
    }

    private void setNewAccessLoginToken(JSONObject response) {
        PacketData.getInstance().setPacketTokens(response.toString());
    }
}
