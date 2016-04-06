package tv.starcards.starcardstv.application.data.tokens;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import tv.starcards.starcardstv.application.API;
import tv.starcards.starcardstv.application.data.packetdata.PacketData;
import tv.starcards.starcardstv.application.http.RefreshLoginTokenRequest;

public class RefreshPacketToken {

    private static final String TAG = "RefreshPacketToken";

    private Context context;

    public RefreshPacketToken(Context context) {
        this.context = context;
    }

    public void refreshPacketToken() {
        RequestQueue rq = Volley.newRequestQueue(context);
        StringRequest req = new RefreshLoginTokenRequest(Request.Method.POST, API.AUTH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
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

    private void setNewAccessLoginToken(String response) {
        PacketData.getInstance().setPacketTokens(response);
    }
}
