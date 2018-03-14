package com.example.douban.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.douban.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uwei on 2018/3/4.
 */

public class Fragment_Top_Movie extends Fragment {
    private String TAG = "Fragment_Top_Movie";
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<Fragment> mPagerList = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();
    private String[] mStrings = {"正在热映","即将上映","Top250"};
    public static Fragment_Top_Movie newInsance(){
        return new Fragment_Top_Movie();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG,"onCreateView");
        mTitles.add("正在热映");
        mTitles.add("即将上映");
        mTitles.add("Top250");
        mPagerList.add(new Fragment_Top_Movie_Types().newInstance(1));
        mPagerList.add(new Fragment_Top_Movie_Types().newInstance(2));
        mPagerList.add(new Fragment_Top_Movie_Types().newInstance(3));
        View rootView = inflater.inflate(R.layout.fragment_top_movie,container,false);
        mTabLayout = rootView.findViewById(R.id.tabLayout_top_movie);
        mViewPager = rootView.findViewById(R.id.top_movie_pager);
        FragmentManager fm = getChildFragmentManager();
        mViewPager.setAdapter(new MoviePagerAdapter(fm));
        mTabLayout.setupWithViewPager(mViewPager);
        return rootView;
    }

    class MoviePagerAdapter extends FragmentPagerAdapter{
        public MoviePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mPagerList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mStrings[position];
        }

        @Override
        public int getCount() {
            return mPagerList.size();
        }
    }


}

