package tv.starcards.starcardstv.application.http;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tv.starcards.starcardstv.application.data.tokens.LoginAccessToken;
import tv.starcards.starcardstv.application.data.tokens.LoginRefreshToken;

public class HttpRefreshLoginToken extends JsonObjectRequest {
    public HttpRefreshLoginToken (int method, String url, JSONObject j, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, j, listener, errorListener);

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
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
