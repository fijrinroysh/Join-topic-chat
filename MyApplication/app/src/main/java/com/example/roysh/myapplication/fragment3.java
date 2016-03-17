package com.example.roysh.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ROYSH on 3/9/2016.
 */
public class fragment3 extends Fragment {
    public static fragment3 newInstance(String name) {

            Bundle args = new Bundle();
            args.putString("name", name);

        fragment3 fragment = new fragment3();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main3, container,false);
        TextView txtview = (TextView) view.findViewById(R.id.text);
        txtview.setText(getArguments().getString("name"));
        return view;
    }
}
