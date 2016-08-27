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

        // TabSpec Çê∂ê¨Ç∑ÇÈ
        tabSpec1 = tabHost.newTabSpec("tab1");
        tabSpec1.setIndicator("tab1");
        // TabHost Ç…í«â¡
        tabHost.addTab(tabSpec1, SampleFragment.class, null);

        // TabSpec Çê∂ê¨Ç∑ÇÈ
        tabSpec2 = tabHost.newTabSpec("tab2");
        tabSpec2.setIndicator("tab2");
        // TabHost Ç…í«â¡
        tabHost.addTab(tabSpec2, SampleFragment.class, null);

        return view;
    }
}