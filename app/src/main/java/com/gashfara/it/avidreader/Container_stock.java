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

public class Container_stock extends Fragment {

    public Container_stock() {
    }

    public static Container_stock newInstance() {
        Bundle args = new Bundle();
        Container_stock fragment = new Container_stock();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Fragment fragment = Fragment_stock.newInstance();
        View view = inflater.inflate(R.layout.container_stock, container, false);
        getChildFragmentManager().beginTransaction().add(R.id.fragment_stock, fragment).commit();
        FloatingActionButton fab_search = (FloatingActionButton) view.findViewById(R.id.fab_add_stock);

        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"item_0", "item_1", "item_2"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("Selector")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Fragment fragment = Fragment_stock.newInstance();
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