package tv.starcards.starcardstv.application.http;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tv.starcards.starcardstv.application.data.tokens.LoginAccessToken;
import tv.starcards.starcardstv.application.data.tokens.LoginRefreshToken;

public class RefreshLoginTokenRequest extends StringRequest {
    public RefreshLoginTokenRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }

    @Override
    protected Map<String,String> getParams(){
        Map<String,String> params = new HashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", LoginRefreshToken.getInstance().getRefreshToken());
        return params;
    }
}
