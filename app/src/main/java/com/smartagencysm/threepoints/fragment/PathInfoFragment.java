package com.smartagencysm.threepoints.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartagencysm.threepoints.R;

public class PathInfoFragment extends Fragment {

    public static final String TAG = PathInfoFragment.class.getSimpleName();

    private TextView fromTxt;
    private TextView toTxt;
    private TextView distanceTxt;
    private TextView durationTxt;

    public static PathInfoFragment getInstance(String from, String to, String distance, String duration) {
         Bundle bundle = new Bundle();
         bundle.putString("From", from);
         bundle.putString("To", to);
         bundle.putString("Distance", distance);
         bundle.putString("Duration", duration);
         PathInfoFragment fragment = new PathInfoFragment();
         fragment.setArguments(bundle);
         return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.path_info_fragment, null);

        Bundle bundle = this.getArguments();

        fromTxt = (TextView) view.findViewById(R.id.from);
        toTxt = (TextView) view.findViewById(R.id.to);
        distanceTxt = (TextView) view.findViewById(R.id.distance);
        durationTxt = (TextView) view.findViewById(R.id.duration);

        fromTxt.setText(bundle.getString("From"));
        toTxt.setText(bundle.getString("To"));
        distanceTxt.setText(bundle.getString("Distance"));
        durationTxt.setText(bundle.getString("Duration"));

        return view;
    }

}
