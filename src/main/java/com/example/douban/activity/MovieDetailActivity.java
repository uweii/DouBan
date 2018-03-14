package com.example.douban.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.douban.NetUtils.FetchMovies;
import com.example.douban.R;
import com.example.douban.po.ActorInfo;
import com.example.douban.po.MovieInfo;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by uwei on 2018/2/4.
 */

public class MovieDetailActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    MovieInfo movieInfo;
    private TextView mMovieInfo;
    private TextView mMovieSummary;
    private TextView mMovieSummaryAll;
    private TextView mShowHide;
    private RecyclerView mActorRecyler;  //演员的recylerview
    private RecyclerView mMovieLikeRecyler;  //喜欢这部电影也喜欢的recylerview
    private MovieInfo mMovie;
    private ImageView mImageView;
    private String MOVIEDATAIL_BASE_URL = "https://api.douban.com/v2/movie/subject/";
    private String mCountry;  //电影出产的国家
    private String mAka;      //电影原名
    private List<ActorInfo> mActorInfoList = new ArrayList<>();  //电影演员
    private List<MovieInfo> mMovieLikeList = new ArrayList<>();  //喜欢这部影片的同样喜欢
    private String mSummary;  //电影简介
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private MovieLikeAdapter mMovieLikeAdapter = new MovieLikeAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mMovie = (MovieInfo) bundle.getSerializable("movie");
        setContentView(R.layout.activity_movie_detail);
        mtoolbar = findViewById(R.id.toolbar);
        mImageView = findViewById(R.id.movie_img);
        mMovieInfo = findViewById(R.id.tv_movie_info);
        mMovieSummary = findViewById(R.id.tv_movie_summary);
        mMovieSummaryAll = findViewById(R.id.tv_movie_summary_all);
        mShowHide = findViewById(R.id.show_hide);
        mActorRecyler = findViewById(R.id.actor_recyler_view);
        mMovieLikeRecyler = findViewById(R.id.movie_like_recyler_view);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager lm1 = new LinearLayoutManager(this);
        lm1.setOrientation(LinearLayoutManager.HORIZONTAL);
        mActorRecyler.setLayoutManager(lm);
        mMovieLikeRecyler.setLayoutManager(lm1);
        setSupportActionBar(mtoolbar);
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        Picasso.with(this).load(mMovie.getImgUrl()).fit().centerCrop().into(mImageView);
        mCollapsingToolbarLayout.setTitle("《" + mMovie.getName() + "》");
        //getSupportActionBar().setDisplayShowTitleEnabled(false);  //隐藏Toolar放入title
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandTitleSize);
        mtoolbar.setNavigationIcon(R.mipmap.back);  //设置ToolBar的返回图标
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {  //点击法返回图标
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMovieSummary.getVisibility() == View.VISIBLE) {
                    mShowHide.setText("隐藏");
                    mMovieSummary.setVisibility(View.GONE);
                    mMovieSummaryAll.setVisibility(View.VISIBLE);
                } else {
                    mShowHide.setText("显示");
                    mMovieSummary.setVisibility(View.VISIBLE);
                    mMovieSummaryAll.setVisibility(View.GONE);
                }

            }
        });
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                init();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                StringBuilder sb = new StringBuilder();
                sb.append("电影类型: " + mMovie.getGenres());
                sb.append("\n");
                sb.append("原名: " + mAka);
                sb.append("\n");
                sb.append("上映国家: " + mCountry);
                sb.append("\n");
                sb.append("上映时间: " + mMovie.getYear());
                mMovieInfo.setText(sb.toString());
                mMovieSummary.setText(mSummary);
                mMovieSummaryAll.setText(mSummary);
                mShowHide.setText("展开");
                mActorRecyler.setAdapter(new ActorAdapter());
                mMovieLikeRecyler.setAdapter(mMovieLikeAdapter);
            }
        };
        task.execute();

    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(mMovie.getAltUrl()).get();
                    Elements info = doc.select("div .recommendations-bd");
                    Elements info_like = info.select("dl");
                    System.out.println(info_like.size());
                    for (int i = 0; i < info_like.size(); i++) {
                        String altUrl = info_like.get(i).select("dt").select("a").attr("href");
                        String imgUrl = info_like.get(i).select("dt").select("img").attr("src");
                        String name = info_like.get(i).select("dd").select("a").text();
                        String id = altUrl.substring(33, 40);
                        mMovieLikeList.add(new MovieInfo(imgUrl, id, altUrl, name));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMovieLikeAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        String movieJsonUrl = MOVIEDATAIL_BASE_URL + mMovie.getId();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(movieJsonUrl).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                try {
                    JSONObject resultObject = new JSONObject(result);
                    mCountry = resultObject.getJSONArray("countries").getString(0);
                    mAka = resultObject.getJSONArray("aka").getString(0);
                    mSummary = resultObject.getString("summary");
                    JSONArray actorArray = resultObject.getJSONArray("casts");
                    String director_alt = ((JSONObject) resultObject.getJSONArray("directors").get(0)).getString("alt");
                    String director_name = ((JSONObject) resultObject.getJSONArray("directors").get(0)).getString("name");
                    String director_id = ((JSONObject) resultObject.getJSONArray("directors").get(0)).getString("id");
                    String director_img = ((JSONObject) resultObject.getJSONArray("directors").get(0)).getJSONObject("avatars").getString("small");
                    ActorInfo director = new ActorInfo(director_img, director_name, director_id, director_alt);
                    mActorInfoList.add(director);
                    for (int i = 0; i < actorArray.length(); i++) {
                        String actorAlt = ((JSONObject) actorArray.get(i)).getString("alt");
                        String actorImg = ((JSONObject) actorArray.get(i)).getJSONObject("avatars").getString("small");
                        String actorName = ((JSONObject) actorArray.get(i)).getString("name");
                        String actorId = ((JSONObject) actorArray.get(i)).getString("id");
                        ActorInfo actorInfo = new ActorInfo(actorImg, actorName, actorId, actorAlt);
                        mActorInfoList.add(actorInfo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ActorAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(MovieDetailActivity.this);
            View v = inflater.inflate(R.layout.actor_item, parent, false);
            return new ActorHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (position == 0) {
                String directorName = mActorInfoList.get(position).getActorName() + "\n" + "   导演";
                ((ActorHolder) holder).actorName.setText(directorName);
                Picasso.with(MovieDetailActivity.this).load(mActorInfoList.get(position).getActorImg()).into(((ActorHolder) holder).actorImg);
            } else {
                ((ActorHolder) holder).actorName.setText(mActorInfoList.get(position).getActorName());
            }
            Picasso.with(MovieDetailActivity.this).load(mActorInfoList.get(position).getActorImg()).into(((ActorHolder) holder).actorImg);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("actor",mActorInfoList.get(position));
                    Intent intent = new Intent(MovieDetailActivity.this, ActorDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mActorInfoList.size();
        }

        class ActorHolder extends RecyclerView.ViewHolder {
            public ImageView actorImg;
            public TextView actorName;

            public ActorHolder(View itemView) {
                super(itemView);
                actorImg = itemView.findViewById(R.id.actor_image);
                actorName = itemView.findViewById(R.id.actor_name);
            }
        }
    }

    private class MovieLikeAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(MovieDetailActivity.this);
            View v = inflater.inflate(R.layout.actor_item, parent, false);
            return new ActorHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((ActorHolder) holder).actorName.setText(mMovieLikeList.get(position).getName());
            Picasso.with(MovieDetailActivity.this).load(mMovieLikeList.get(position).getImgUrl()).into(((ActorHolder) holder).actorImg);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncTask task = new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            movieInfo = FetchMovies.getMovieInfoById(mMovieLikeList.get(position).getId());
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("movie",movieInfo);
                            Intent intent = new Intent(MovieDetailActivity.this,MovieDetailActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    };
                    task.execute();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mMovieLikeList.size();
        }

        class ActorHolder extends RecyclerView.ViewHolder {
            public ImageView actorImg;
            public TextView actorName;

            public ActorHolder(View itemView) {
                super(itemView);
                actorImg = itemView.findViewById(R.id.actor_image);
                actorName = itemView.findViewById(R.id.actor_name);
            }
        }
    }
}