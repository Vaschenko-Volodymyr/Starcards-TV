package tv.starcards.starcardstv.application.ui.fragments;

import android.app.Activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;

import tv.starcards.starcardstv.R;
import tv.starcards.starcardstv.application.API;
import tv.starcards.starcardstv.application.ui.adaptors.MessageAdaptor;
import tv.starcards.starcardstv.application.http.GetWithLoginToken;
import tv.starcards.starcardstv.application.ui.models.MessagesListModel;
import tv.starcards.starcardstv.application.util.SearchToolbarUi;

public class MessageFragment extends Fragment {

    public static Activity messageListViewActivity = null;
    public static ArrayList<MessagesListModel> messageListArray = new ArrayList<>();
    private MessageAdaptor adapter;
    private ListView messages;
    private Resources resources;

    private View mLoadingView;
    private View mContentView;
    private int mShortAnimationDuration;

    private TextView theme, date, message;


    public MessageFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messages, container, false);

        SearchToolbarUi.resetToolbar(getActivity());

        messages = (ListView) v.findViewById(R.id.messames_container);

        requestMessages();
        return v;
    }

    private void requestMessages() {
        RequestQueue rq = Volley.newRequestQueue(getContext());
        JsonObjectRequest req = new GetWithLoginToken(Request.Method.GET, API.MESSAGE_HISTORY, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //fillMessages(response);
                        Log.w("RESPONSE_MESSAGES", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        rq.add(req);
    }
}
