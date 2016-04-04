package tv.starcards.starcardstv.application.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tv.starcards.starcardstv.R;
import tv.starcards.starcardstv.application.util.SearchToolbarUi;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        SearchToolbarUi.resetToolbar(getActivity());

        return v;
    }
}
