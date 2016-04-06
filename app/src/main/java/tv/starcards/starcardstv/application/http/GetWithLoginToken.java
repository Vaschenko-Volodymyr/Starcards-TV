package tv.starcards.starcardstv.application.http;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import tv.starcards.starcardstv.application.data.tokens.LoginAccessToken;

public class GetWithLoginToken extends JsonObjectRequest {
    public GetWithLoginToken(int method, String url, JSONObject j, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, j, listener, errorListener);

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Accept-Language", "ru-RU");
        String token = "Bearer " + LoginAccessToken.getInstance().getAccessToken();
        headers.put("Login", token);
        return headers;
    }

}
