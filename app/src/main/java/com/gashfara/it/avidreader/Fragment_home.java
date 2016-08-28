package com.gashfara.it.avidreader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

public class Fragment_home extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FragmentTabHost tabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        tabHost.setup(getActivity(), getChildFragmentManager(), R.id.content);

        TabHost.TabSpec tabSpec1, tabSpec2;

        // TabSpec を生成する
        tabSpec1 = tabHost.newTabSpec("書庫");
        tabSpec1.setIndicator("書庫");
        // TabHost に追加
        tabHost.addTab(tabSpec1, Fragment_library.class, null);

        // TabSpec を生成する
        tabSpec2 = tabHost.newTabSpec("ストック");
        tabSpec2.setIndicator("ストック");
        // TabHost に追加
        tabHost.addTab(tabSpec2, Fragment_stock.class, null);

//        tabHost.setOnTabChangedListener(tabHost);

         return view;
    }
}