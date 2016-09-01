package com.gashfara.it.avidreader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Adapter_webSearch extends Fragment {

    public Adapter_webSearch() {
    }

    public static Adapter_webSearch newInstance() {
        Bundle args = new Bundle();
        Adapter_webSearch fragment = new Adapter_webSearch();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adapter_websearch, container, false);

        // searchView
        final android.support.v7.widget.SearchView searchText = (android.support.v7.widget.SearchView) view
                .findViewById(R.id.search_text);

        // Expand the SearchView with
        searchText.setIconified(false);

        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                Fragment fragment = Fragment_webSearch.newInstance(arg0);
                getChildFragmentManager().beginTransaction().add(R.id.fragment_websearch, fragment).commit();
                return false;
            }
        });
        return view;
    }
}