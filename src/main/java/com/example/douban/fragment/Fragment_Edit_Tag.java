package com.example.douban.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.douban.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uwei on 2018/3/8.
 */

public class Fragment_Edit_Tag extends Fragment {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private List<Fragment_Edit_Tag_Types> mTagTypesList;
    private String[] mTitles = {"电影标签","图书标签"};
    private TabPagerAdapter mPagerAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_tag,container,false);
        mTabLayout = v.findViewById(R.id.tabLayout);
        mViewPager = v.findViewById(R.id.viewPager);
        init();
        return v;
    }

    public static Fragment_Edit_Tag newInstance(){
        return new Fragment_Edit_Tag();
    }
    private void init(){
        mTagTypesList = new ArrayList<>();
        mPagerAdapter = new TabPagerAdapter(getChildFragmentManager());
        mTagTypesList.add(new Fragment_Edit_Tag_Types().newInstance(0));
        mTagTypesList.add(new Fragment_Edit_Tag_Types().newInstance(1));
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(mPagerAdapter);
    }

    private class TabPagerAdapter extends FragmentPagerAdapter{

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mTagTypesList.get(position);
        }

        @Override
        public int getCount() {
            return mTagTypesList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }
}
