package com.gashfara.it.avidreader;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Fragment_home extends Fragment implements ViewPager.OnPageChangeListener {

    public Fragment_home() {
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;

                switch (position) {
                    case 0:
                       fragment = TestFragment.newInstance(position + 1);
                        break;
                    case 1:
                        fragment = Adapter_library.newInstance();
                        break;
                    case 2:
                        fragment = Adapter_stock.newInstance();
                        break;
                    default:
                        break;
                }

                return fragment;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "ホーム";
                    case 1:
                        return "書庫";
                    case 2:
                        return "ストック";
                    default:
                        break;
                }
                return "";
            }

            @Override
            public int getCount() {
                return 3;
            }

        };

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        //オートマチック方式: これだけで両方syncする
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("MainActivity", "onPageSelected() position=" + position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
