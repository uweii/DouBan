package com.example.douban.activity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActorDetailActivity extends AppCompatActivity {
    private ActorInfo mActorInfo;
    private  final String mActorBaseUrl = "https://movie.douban.com/celebrity/";
    private final String mActorInfoBaseUrl = "https://api.douban.com/v2/movie/celebrity/";
    private String mName_en;   //英文名
    private String mGender;   //性别
    private String mBornPlace;   //出生地
    MovieInfo mInfo;
    private String mSummary;   //演员简介
    private List<MovieInfo> mMovieRepresentList = new ArrayList<>();   //代表作品
    private List<String> mImgUrl = new ArrayList<>();    //演员照片地址
    private RecyclerView mActorImg, mRepresentRecyler;
    private ActorImgAdapter mImgAdapter;
    private RepresentMoviesAdapter mRepresentMoviesAdapter;
    private TextView nameTextView, enNameTextView, sexTextView, bornTextView;
    private TextView shortTextView, longTextView,show_hide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mActorImg = findViewById(R.id.actor_img_recyler);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(position ==0){
                    return 2;
                }
                return 1;
            }
        });
        mActorImg.setItemAnimator(new DefaultItemAnimator());
        mActorImg.setLayoutManager(gridLayoutManager);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mActorInfo = (ActorInfo) bundle.getSerializable("actor");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        init();
    }

    private void init(){
        nameTextView = findViewById(R.id.actor_name);
        enNameTextView = findViewById(R.id.actor_name_en);
        sexTextView = findViewById(R.id.actor_sex);
        bornTextView = findViewById(R.id.actor_born_place);
        shortTextView = findViewById(R.id.actor_short_summy);
        longTextView = findViewById(R.id.actor_long_summy);
        show_hide = findViewById(R.id.show_hide);
        mRepresentRecyler = findViewById(R.id.represent_movies);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRepresentRecyler.setLayoutManager(manager);
        mImgAdapter = new ActorImgAdapter();
        mRepresentMoviesAdapter = new RepresentMoviesAdapter();
        show_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shortTextView.getVisibility()==View.VISIBLE){
                    shortTextView.setVisibility(View.GONE);
                    longTextView.setVisibility(View.VISIBLE);
                    show_hide.setText("隐藏");
                }else {
                    longTextView.setVisibility(View.GONE);
                    shortTextView.setVisibility(View.VISIBLE);
                    show_hide.setText("显示");
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = mActorBaseUrl + mActorInfo.getActorId();
                mImgUrl.add(mActorInfo.getActorImg());
                try {
                    Document doc = Jsoup.connect(url).get();
                    Elements photos = doc.select("div #photos");
                    Elements pics = photos.select("ul li");
                    mSummary = doc.select("div #intro").select("div .bd").text();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            shortTextView.setText(mSummary);
                            longTextView.setText(mSummary);
                            show_hide.setText("显示");
                        }
                    });
                    for(int i = 0; i < pics.size(); i++){
                        String imgUrl = pics.get(i).select("a").select("img").attr("src");
                        mImgUrl.add(imgUrl);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mActorImg.setAdapter(mImgAdapter);
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(mActorInfoBaseUrl+mActorInfo.getActorId()).build();
                try {
                    Response response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        try {
                            JSONObject resultObj = new JSONObject(response.body().string());
                            mName_en = resultObj.getString("name_en");
                            mGender = resultObj.getString("gender");
                            mBornPlace = resultObj.getString("born_place");
                            JSONArray work = resultObj.getJSONArray("works");
                            for(int i=0;i<work.length();i++){
                                JSONObject subject = work.getJSONObject(i).getJSONObject("subject");
                                String name = subject.getString("title");
                                String imgUrl = subject.getJSONObject("images").getString("small");
                                String id = subject.getString("id");
                                String altUrl = subject.getString("alt");
                                mMovieRepresentList.add(new MovieInfo(imgUrl,id,altUrl,name));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                nameTextView.setText(mActorInfo.getActorName());
                enNameTextView.setText(mName_en);
                sexTextView.setText("性别: " + mGender);
                bornTextView.setText("出生地: " + mBornPlace);
                mRepresentRecyler.setAdapter(mRepresentMoviesAdapter);
            }
        };
        task.execute();
    }

    private class ActorImgAdapter extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(ActorDetailActivity.this);
            View v = inflater.inflate(R.layout.actor_img_item,parent,false);
            return new ImgHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(position ==0){
                Picasso.with(ActorDetailActivity.this).load(mImgUrl.get(position)).resize(450,500).centerCrop().into(((ImgHolder)holder).mImageView);
            }else{

                Picasso.with(ActorDetailActivity.this).load(mImgUrl.get(position)).resize(225,250).centerCrop().into(((ImgHolder)holder).mImageView);
            }
        }

        @Override
        public int getItemCount() {
            return mImgUrl.size();
        }
        public class ImgHolder extends RecyclerView.ViewHolder{
            public ImageView mImageView;
            public ImgHolder(View itemView) {
                super(itemView);
                mImageView = itemView.findViewById(R.id.actor_img);
            }
        }
    }

    private class RepresentMoviesAdapter extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(ActorDetailActivity.this);
            View v = inflater.inflate(R.layout.actor_item,parent,false);
            return new RepresentHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            Picasso.with(ActorDetailActivity.this)
                    .load(mMovieRepresentList.get(position).getImgUrl()).fit().centerCrop().into(((RepresentHolder)holder).img);
            ((RepresentHolder)holder).name.setText(mMovieRepresentList.get(position).getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AsyncTask task = new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            mInfo = FetchMovies.getMovieInfoById(mMovieRepresentList.get(position).getId());
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("movie",mInfo);
                            Intent intent = new Intent(ActorDetailActivity.this,MovieDetailActivity.class);
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
            return mMovieRepresentList.size();
        }

        public class RepresentHolder extends RecyclerView.ViewHolder{
            public ImageView img;
            public TextView name;
            public RepresentHolder(View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.actor_image);
                name = itemView.findViewById(R.id.actor_name);
            }
        }
    }
}
