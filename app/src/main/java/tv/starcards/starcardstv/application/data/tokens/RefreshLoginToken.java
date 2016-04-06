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
import tv.starcards.starcardstv.application.data.userdata.UserData;
import tv.starcards.starcardstv.application.http.RefreshLoginTokenRequest;

public class RefreshLoginToken {

    private static final String TAG = "RefreshLoginToken";

    private Context context;

    public RefreshLoginToken(Context context) {
        this.context = context;
    }

    public void refreshLoginToken() {
        RequestQueue rq = Volley.newRequestQueue(context);
        StringRequest req = new RefreshLoginTokenRequest(Request.Method.POST, API.AUTH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.toString().contains("error")) {
                            Log.d(TAG, response.toString());
                        } else {
                            Log.d(TAG, response);
                            setNewAccessLoginToken(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error during refreshing login access token");
                    }
                });
        rq.add(req);
    }

    private void setNewAccessLoginToken(String response) {
        UserData.getInstance().setLoginTokens(response);
    }
}
