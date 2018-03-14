package com.example.douban.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.douban.R;
import com.example.douban.SharePre.SharePreUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uwei on 2018/1/31.
 */

public class Fragment_Book extends Fragment {
    private String TAG = "Fragment_Book";
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private RecyclerView mRecyclerView;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mBookTAgs = new ArrayList<>();
    public static Fragment newInstance() {
        return new Fragment_Book();
    }
    private MoviePagerAdapter mPagerAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String SeletedTag = SharePreUtil.getSelectedBookTag(getActivity()).trim();
        String bookSeletedTag = SeletedTag.substring(1,SeletedTag.length()-1);
        String[] selectedBookArr = bookSeletedTag.split(",");
        for(int i =0; i <selectedBookArr.length;i++)
        {
            mBookTAgs.add(selectedBookArr[i]);
            mFragmentList.add(new Fragment_Book_Types().newInstance(selectedBookArr[i]));
        }
        Log.i(TAG, mBookTAgs.toString());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        mViewPager = view.findViewById(R.id.viewPager);
        mTabLayout = view.findViewById(R.id.tabLayout);
        FragmentManager fm = getChildFragmentManager();
        mPagerAdapter = new MoviePagerAdapter(fm);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            mBookTAgs.clear();
            mFragmentList.clear();
            String SeletedTag = SharePreUtil.getSelectedBookTag(getActivity()).trim();
            String bookSeletedTag = SeletedTag.substring(1,SeletedTag.length()-1);
            String[] selectedBookArr = bookSeletedTag.split(",");
            for(int i =0; i <selectedBookArr.length;i++)
            {
                mBookTAgs.add(selectedBookArr[i]);
                mFragmentList.add(new Fragment_Book_Types().newInstance(selectedBookArr[i]));
            }
            mPagerAdapter.notifyDataSetChanged();
        }
    }

    public class MoviePagerAdapter extends FragmentPagerAdapter {

        public MoviePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mBookTAgs.get(position);
        }
    }
}
