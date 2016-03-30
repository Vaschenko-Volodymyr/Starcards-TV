package tv.starcards.starcardstv.application.data.userdata;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.application.API;
import tv.starcards.starcardstv.application.exceptions.NoDataException;
import tv.starcards.starcardstv.application.http.HttpGetWithLoginToken;
import tv.starcards.starcardstv.application.util.Parser;

public class UserInfoRequest {

    private static final String        TAG = "UserInfoRequest";
    private Context                    context;

    public UserInfoRequest(Context context) {
        this.context = context;
    }

    public void doRequest() {
        Log.d(TAG, "doRequest");
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest req = new HttpGetWithLoginToken(Request.Method.GET, API.USER_INFO, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        if (response.toString().contains("ERROR")) {
//                            RefreshLoginToken refresh = new RefreshLoginToken(context);
//                            refresh.refreshLoginToken();
//                            doRequest();
                        } else {
                            try {
                                fillUserInfo(response);
                            } catch (NoDataException e) {
                                Log.w(TAG, "Access token is old");
                            }
                        }

                        Log.w(TAG, "Response user info : " + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        rq.add(req);
    }

    private void fillUserInfo(JSONObject response) throws NoDataException {
        Parser parser = new Parser();
        MainScreenActivity.email.setText(UserEmail.getInstance().getUserEmail());
        String result = "";
        try {
            result = "{" + parser.parse(response.toString(), "results") + "}";
            UserData.getInstance().setUserData(result);
        } catch (NullPointerException e){
            throw new NoDataException();
        }
    }
}
