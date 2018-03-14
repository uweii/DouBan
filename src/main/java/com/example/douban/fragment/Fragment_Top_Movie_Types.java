package com.example.douban.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.douban.Cache.SaveData;
import com.example.douban.NetUtils.FetchMovies;
import com.example.douban.R;
import com.example.douban.activity.MovieDetailActivity;
import com.example.douban.po.MovieInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.Adapter;
import static android.support.v7.widget.RecyclerView.OnClickListener;
import static android.support.v7.widget.RecyclerView.OnScrollListener;
import static android.support.v7.widget.RecyclerView.VISIBLE;
import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by uwei on 2018/1/31.
 */


public class Fragment_Top_Movie_Types extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static String TAG = "Fragment_Movie_Types";
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private FloatingActionButton mFloatingActionButton;
    private String tag;
    private int lastItemPosition;  //最后一个item的位置
    private int mCurrentStart = 0;  //请求电影的起始位置
    private int NormalView = 0;  //正常item
    private int FootView = 1;    //加载view
    private final static int NULLSTAR = 0x123;   //空星星
    private final static int HALFSTAR = 0x124;   //半星星
    private final static int FULLSTAR = 0x125;   //满星星
    private List<MovieInfo> mMovieInfos = new ArrayList<>();
    private TypeAdapter adapter;
    public int mMovieType;  //电影种类
    private Fragment_Top_Movie_Types fragment;

    public Fragment newInstance(int movieType) {
        fragment = new Fragment_Top_Movie_Types();
        fragment.mMovieType = movieType;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TypeAdapter();
        switch (this.mMovieType) {
            case 1:
                tag = "in_theaters";
                break;
            case 2:
                tag = "coming_soon";
                break;
            case 3:
                tag = "top250";
                break;
        }
        try {
            List<MovieInfo> cache = SaveData.getData(getActivity(),tag,2);
            if(cache!=null){
                mMovieInfos = cache;
                mCurrentStart = mMovieInfos.size()/20-1;
            }else {
                AsyncTask<String, Integer, List<MovieInfo>> task = new AsyncTask<String, Integer, List<MovieInfo>>() {
                    @Override
                    protected List<MovieInfo> doInBackground(String... strings) {
                        return new FetchMovies().getTopMoviesByTag(tag, mCurrentStart);
                    }

                    @Override
                    protected void onPostExecute(List<MovieInfo> movieInfos) {
                        super.onPostExecute(movieInfos);
                        mMovieInfos = movieInfos;
                        SaveData.setData(getActivity(),movieInfos,2,tag);
                        adapter.notifyDataSetChanged();
                        Log.i(TAG, "网络请求结束");
                    }
                };
                task.execute();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_types, container, false);
        mRecyclerView = view.findViewById(R.id.recyler_view);
        mRefreshLayout = view.findViewById(R.id.swipeRefresh);
        mFloatingActionButton = view.findViewById(R.id.go_up);
        mFloatingActionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.smoothScrollToPosition(0);  //滑动到第一项
            }
        });
        mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRefreshLayout.setOnRefreshListener(this);
        final GridLayoutManager lm = new GridLayoutManager(getActivity(), 3);
        lm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            //如果是最后一个item，则设置占据3列，否则占据1列
            @Override
            public int getSpanSize(int position) {
                boolean isFooter = position == adapter.getItemCount() - 1;
                return isFooter ? 3 : 1;
            }
        });
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (lastItemPosition + 1 == lm.getItemCount()) {
                        //Log.d(TAG, "我在加载更多");
                        mCurrentStart++;
                        loadMore();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastItemPosition = lm.findLastVisibleItemPosition();
                if (dy > 0 && mFloatingActionButton.getVisibility() == VISIBLE) {
                    mFloatingActionButton.hide();
                } else if (dy < 0 && mFloatingActionButton.getVisibility() != VISIBLE) {
                    mFloatingActionButton.show();
                }
            }
        });
        return view;
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(true);
        AsyncTask<String, Integer, List<MovieInfo>> task = new AsyncTask<String, Integer, List<MovieInfo>>() {
            @Override
            protected List<MovieInfo> doInBackground(String... strings) {
                return new FetchMovies().getTopMoviesByTag(tag, 0);
            }

            @Override
            protected void onPostExecute(List<MovieInfo> movieInfos) {
                super.onPostExecute(movieInfos);
                mMovieInfos.clear();
                mMovieInfos = movieInfos;
                adapter.notifyDataSetChanged();
                SaveData.setData(getActivity(),mMovieInfos,2,tag);
                mRefreshLayout.setRefreshing(false);
                Log.i(TAG, "网络请求结束");
            }
        };
        task.execute();
    }


    public class TypeAdapter extends Adapter<ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            if (viewType == NormalView) {
                View v = inflater.inflate(R.layout.movie_item, parent, false);
                return new TypeHolder(v);
            } else {
                View v = inflater.inflate(R.layout.footview, parent, false);
                return new FootHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            if (holder instanceof TypeHolder) {
                ((TypeHolder) holder).movieName.setText(mMovieInfos.get(position).getName());
                Picasso.with(getActivity()).load(mMovieInfos.get(position).getImgUrl()).fit().centerCrop().into(((TypeHolder) holder).movieImg);
                if (((TypeHolder) holder).starContainer.getChildCount() == 0) {
                    setStar(mMovieInfos.get(position).getAverage(), ((TypeHolder) holder).starContainer);
                    TextView starNum = new TextView(getActivity());
                    //starNum.setTextSize(18);
                    starNum.setText("" + mMovieInfos.get(position).getAverage());
                    ((TypeHolder) holder).starContainer.addView(starNum);
                }
                holder.itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("movie",  mMovieInfos.get(position));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            } else {
                ((FootHolder) holder).mTip.setText("正在努力加载中...");
            }
        }

        @Override
        public int getItemCount() {
            if (mMovieInfos != null) {
                return mMovieInfos.size() + 1;
            } else {
                return 0;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position + 1 == getItemCount()) {
                return FootView;
            } else {
                return NormalView;
            }
        }

        class TypeHolder extends ViewHolder {    //普通电影的holder
            public ImageView movieImg;
            public LinearLayout starContainer;
            public TextView movieName;

            public TypeHolder(View itemView) {
                super(itemView);
                movieImg = itemView.findViewById(R.id.movie_img);
                movieName = itemView.findViewById(R.id.movie_name);
                starContainer = itemView.findViewById(R.id.star_container);
            }
        }

        class FootHolder extends ViewHolder {  //提示加载的holder
            public TextView mTip;

            public FootHolder(View itemView) {
                super(itemView);
                mTip = itemView.findViewById(R.id.tv_tip);
            }
        }

    }

    private void loadMore() {
        final AsyncTask<String, Integer, List<MovieInfo>> task = new AsyncTask<String, Integer, List<MovieInfo>>() {
            @Override
            protected List<MovieInfo> doInBackground(String[] objects) {
                return new FetchMovies().getTopMoviesByTag(tag, mCurrentStart * 20);
            }

            @Override
            protected void onPostExecute(List<MovieInfo> o) {
                super.onPostExecute(o);
                mMovieInfos.addAll(o);
                SaveData.setData(getActivity(),mMovieInfos,2,tag);
                adapter.notifyDataSetChanged();
            }
        };
        task.execute();
    }

    /*设置显示评分的星星*/
    private void setStar(double average, LinearLayout container) {
        if (average == 0) {
            for (int i = 0; i < 5; i++) {
                container.addView(makeImgViewByType(NULLSTAR));
            }
        } else if (0 < average && average < 2) {
            container.addView(makeImgViewByType(HALFSTAR));
            for (int i = 0; i < 4; i++) {
                container.addView(makeImgViewByType(NULLSTAR));
            }
        } else if (average == 2) {
            container.addView(makeImgViewByType(FULLSTAR));
            for (int i = 0; i < 4; i++) {
                container.addView(makeImgViewByType(NULLSTAR));
            }
        } else if (2 < average && average < 4) {
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(HALFSTAR));
            for (int i = 0; i < 3; i++) {
                container.addView(makeImgViewByType(NULLSTAR));
            }
        } else if (average == 4) {
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
            for (int i = 0; i < 3; i++) {
                container.addView(makeImgViewByType(NULLSTAR));
            }
        } else if (4 < average && average < 6) {
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(HALFSTAR));
            for (int i = 0; i < 2; i++) {
                container.addView(makeImgViewByType(NULLSTAR));
            }
        } else if (average == 6) {
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
            for (int i = 0; i < 2; i++) {
                container.addView(makeImgViewByType(NULLSTAR));
            }
        } else if (6 < average && average < 8) {
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(HALFSTAR));
            container.addView(makeImgViewByType(NULLSTAR));
        } else if (average == 8) {
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(NULLSTAR));
        } else if (8 < average && average < 10) {
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(HALFSTAR));
        } else {
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
            container.addView(makeImgViewByType(FULLSTAR));
        }

    }

    private ImageView makeImgViewByType(int type) {
        ImageView imageView = new ImageView(getActivity());
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(20, 30);
        imageView.setLayoutParams(params);
        switch (type) {
            case NULLSTAR:
                imageView.setImageResource(R.mipmap.rb3);
                break;
            case HALFSTAR:
                imageView.setImageResource(R.mipmap.rb2);
                break;
            case FULLSTAR:
                imageView.setImageResource(R.mipmap.rb1);
                break;
        }
        return imageView;
    }
}
