package tv.starcards.starcardstv.application.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.R;
import tv.starcards.starcardstv.application.API;
import tv.starcards.starcardstv.application.data.tokens.LoginRefreshToken;
import tv.starcards.starcardstv.application.data.userdata.UserData;
import tv.starcards.starcardstv.application.http.RefreshLoginTokenRequest;
import tv.starcards.starcardstv.application.util.SearchToolbarUi;

public class ContactsFragment extends Fragment {

    private static final String TAG = "ContactsFragment";

    private Button refresh;

    public ContactsFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);

        refresh = (Button) v.findViewById(R.id.contacts_refresh_token_test);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTokens();
            }
        });
        SearchToolbarUi.resetToolbar(getActivity());

        return v;
    }

    private void refreshTokens() {
        StringRequest stringRequest = new RefreshLoginTokenRequest(Request.Method.POST, API.AUTH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, response);
                        UserData.getInstance().setLoginTokens(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Login", "Error... " + error);
                    }
                }){
        };

        Log.d(TAG, LoginRefreshToken.getInstance().getRefreshToken());

        RequestQueue requestQueue = Volley.newRequestQueue(MainScreenActivity.instance);
        requestQueue.add(stringRequest);
    }
}
