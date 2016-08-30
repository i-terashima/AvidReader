package com.gashfara.it.avidreader;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Adapter_library extends Fragment {

    public Adapter_library() {
    }

    public static Adapter_library newInstance() {
        Bundle args = new Bundle();
        Adapter_library fragment = new Adapter_library();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Fragment fragment = new Fragment_library();
        View view = inflater.inflate(R.layout.adapter_library, container, false);
        getChildFragmentManager().beginTransaction().add(R.id.fragment_library, fragment).commit();
        FloatingActionButton fab_search = (FloatingActionButton) view.findViewById(R.id.fab_search);

        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"Web検索", "バーコード", "ISBN"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("書籍の登録を選択してください")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Fragment fragment = new Adapter_webSearch();
                                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        break;
                                }
                                dialog.dismiss();           // item_which pressed
                            }
                        })
                        .show();
            }
        });
        return view;
    }
}