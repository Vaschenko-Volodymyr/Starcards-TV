package tv.starcards.starcardstv.application.http;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.application.API;
import tv.starcards.starcardstv.application.data.packetdata.PacketData;
import tv.starcards.starcardstv.application.data.tokens.LoginRefreshToken;
import tv.starcards.starcardstv.application.data.userdata.UserData;
import tv.starcards.starcardstv.application.data.userdata.UserEmail;
import tv.starcards.starcardstv.application.exceptions.NoDataException;
import tv.starcards.starcardstv.application.ui.fragments.CabinetFragment;
import tv.starcards.starcardstv.application.util.Parser;

public class RequestWithLoginToken {

    public static final int     USER_INFO_REQUEST = 1;
    public static final int     PACKETS_INFO_REQUEST = 2;
    public static final int     MESSAGES_REQUEST = 3;

    private static final String TAG = RequestWithLoginToken.class.getSimpleName();

    private int                 requestId;
    private int                 trials = 0;

    private RequestQueue        requestQueue;

    public RequestWithLoginToken() {
        requestQueue = Volley.newRequestQueue(MainScreenActivity.instance);
    }

    public void get(int id) {
        Log.d(TAG + ", method - get", "");
        requestId = id;
        String url;
        switch (requestId) {
            case USER_INFO_REQUEST:
                url = API.USER_INFO;
                break;
            case PACKETS_INFO_REQUEST:
                url = API.PACKETS_SUMMARY;
                break;
            default:
                url = API.API;
                break;
        }
        final JsonObjectRequest req = new GetWithLoginToken(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG + ", method - get", "response = " + response);
                        try {
                            if (response.getString("status").equals("ERROR")) {
                                Log.e(TAG + ", method - get", "ERROR");
                                refreshTokens();
                            } else {
                                trials = 0;
                                switch (requestId) {
                                    case USER_INFO_REQUEST:
                                        fillUserInfo(response);
                                        break;
                                    case PACKETS_INFO_REQUEST:
                                        fillPacketInfo(response);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG + ", method - get", "fillPacketInfo exception, " + e);
                        } catch (NoDataException e) {
                            Log.e(TAG + ", method - get", "fillUserInfo exception, " + e);
                        }
                        Log.w(TAG + ", method - get", "Response user info : " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG + ", method - get", "Volley error, " + error);
                        VolleyLog.e("Authorization failed: ", error.getMessage());
                        MainScreenActivity.pDialog.dismiss();
                        refreshTokens();
                    }
                });
        requestQueue.add(req);
    }

    private void fillUserInfo(JSONObject response) throws NoDataException {
        Parser parser = new Parser();
        MainScreenActivity.email.setText(UserEmail.getInstance().getUserEmail());
        String result;
        try {
            result = "{" + parser.parse(response.toString(), "results") + "}";
            UserData.getInstance().setUserData(result);
        } catch (NullPointerException e) {
            throw new NoDataException();
        }
    }

    private void fillPacketInfo(JSONObject response) throws JSONException {
        if (response.toString().contains("results")) {
            PacketData.getInstance().savePacketInfo(response);
            PacketData.getInstance().loadPacketsFromDB();
            CabinetFragment.mCabinetPullToRefreshView.setRefreshing(false);
            CabinetFragment.mCabinetPullToRefreshView.clearDisappearingChildren();
            MainScreenActivity.pDialog.dismiss();
        } else {
            Log.d(TAG, response.toString());
        }
    }

    private void refreshTokens() {
        trials++;
        if (trials < 3) {
            Log.d(TAG + ", method - refreshToken", "Trials = " + trials);
            Log.d(TAG + ", method - refreshToken", "Old refresh token = " + LoginRefreshToken.getInstance().getRefreshToken());
            StringRequest stringRequest = new RefreshLoginTokenRequest(Request.Method.POST, API.AUTH,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG + ", method - refreshToken", "Tokens are refreshed. Response - " + response);
                            UserData.getInstance().setLoginTokens(response);
                            get(requestId);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG + ", method - refreshToken", "Error... " + error);
                        }
                    }) {
            };
            Log.d(TAG + ", method - refreshToken", "New refresh token = " + LoginRefreshToken.getInstance().getRefreshToken());
            requestQueue.add(stringRequest);
        } else {
            Log.e(TAG + ", method - refreshToken", "Too much trials");
        }
    }
}
