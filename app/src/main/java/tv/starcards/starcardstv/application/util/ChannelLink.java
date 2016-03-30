package tv.starcards.starcardstv.application.util;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import tv.starcards.starcardstv.application.http.HttpGetWithPacketToken;

public class ChannelLink {

    private Context context;
    private String url;

    public ChannelLink(Context context, String url) {
        this.context = context;
        this.url = url;
        if (url=="false") {
            requestUrl();
        }
    }

    public String  getUrl() {
        return url;
    }

    private void requestUrl() {
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest req = new HttpGetWithPacketToken(Request.Method.GET, url , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            handleUrl(response);
                        } catch (JSONException e) {
                            Log.w("JSONException", "wrong parsing in fillPackets");
                        }
                        Log.w("TV LINK", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Packet request error", error.toString());
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        rq.add(req);
    }

    private void handleUrl(JSONObject response) throws JSONException {
        Parser parser = new Parser();
        String result = "{" + parser.parse(response.toString(), "results") + "}";
        String localUrl = parser.parse(result, "url");
        Log.w("url", localUrl);
        url = localUrl;
    }
}
