package com.example.douban.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.douban.Cache.SaveData;
import com.example.douban.R;
import com.example.douban.SharePre.SharePreUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uwei on 2018/1/31.
 */

public class Fragment_Movie extends Fragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private RecyclerView mRecyclerView;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mMovieTags = new ArrayList<>();
    public static Fragment newInstance() {
        return new Fragment_Movie();
    }
    private MoviePagerAdapter mPagerAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String SeletedTag = SharePreUtil.getSelectedMovieTag(getActivity()).trim();
        String movieSeletedTag = SeletedTag.substring(1,SeletedTag.length()-1);
        String[] selectedMovieArr = movieSeletedTag.split(",");
        for(int i =0; i <selectedMovieArr.length;i++)
        {
            mMovieTags.add(selectedMovieArr[i]);
            mFragmentList.add(new Fragment_Movie_Types().newInstance(selectedMovieArr[i]));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        mViewPager = view.findViewById(R.id.viewPager);
        mTabLayout = view.findViewById(R.id.tabLayout);
        FragmentManager fm = getChildFragmentManager();
        mPagerAdapter = new MoviePagerAdapter(fm);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
       /* mTabLayout.getTabAt(0).setText("爱情");
        mTabLayout.getTabAt(1).setText("喜剧");
        mTabLayout.getTabAt(2).setText("动画");
        mTabLayout.getTabAt(3).setText("科幻");
        mTabLayout.getTabAt(4).setText("动作");
        mTabLayout.getTabAt(5).setText("经典");*/
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            mMovieTags.clear();
            mFragmentList.clear();
            String SeletedTag = SharePreUtil.getSelectedMovieTag(getActivity()).trim();
            String movieSeletedTag = SeletedTag.substring(1,SeletedTag.length()-1);
            String[] selectedMovieArr = movieSeletedTag.split(",");
            for(int i =0; i <selectedMovieArr.length;i++)
            {
                mMovieTags.add(selectedMovieArr[i]);
                mFragmentList.add(new Fragment_Movie_Types().newInstance(selectedMovieArr[i]));
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
        public long getItemId(int position) {
            return mFragmentList.get(position).hashCode();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mMovieTags.get(position);
        }
    }
}
