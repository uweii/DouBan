package com.example.douban.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.douban.DbUtils.DbSchema;
import com.example.douban.NetUtils.FetchMovies;
import com.example.douban.R;
import com.example.douban.fragment.CollectedBookFragment;
import com.example.douban.po.BookInfo;
import com.example.douban.po.CollectedBook;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {
    private String TAG = "BookDetailActivity";
    private ImageView book_detail_img;
    private TextView book_name, book_detail_info;
    private TextView short_summary, long_summary, show_hide;
    private TextView author_info;
    private RecyclerView book_also_like_recyler;
    private BookInfo mBookInfo, mBookAlsoLike;
    private List<BookInfo> mBookInfoList;
    private BookAlsoLikeAdapter mBookAlsoLikeAdapter;
    private BookCollect mBookCollect;
    private FloatingActionButton fab;
    private boolean hasCollected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        mBookCollect = BookCollect.getBookCollect(this);
        setContentView(R.layout.activity_book_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasCollected){
                    fab.setImageResource(R.mipmap.rb3);
                    mBookCollect.delBook(mBookInfo.getId());
                    hasCollected = false;
                    Snackbar.make(view, "取消收藏！", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else {
                    fab.setImageResource(R.mipmap.rb1);
                    collectBook(BookDetailActivity.this);  //处理收藏的方法
                    hasCollected = true;
                    Snackbar.make(view, "收藏成功！", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mBookInfo = (BookInfo) bundle.get("book");
        mBookAlsoLikeAdapter = new BookAlsoLikeAdapter();
        mBookInfoList = new ArrayList<>();
        book_detail_img = findViewById(R.id.book_detail_img);
        book_name = findViewById(R.id.book_name);
        book_detail_info = findViewById(R.id.book_detail_info);
        short_summary = findViewById(R.id.short_summary);
        long_summary = findViewById(R.id.long_summary);
        show_hide = findViewById(R.id.show_hide);
        author_info = findViewById(R.id.author_info);
        book_also_like_recyler = findViewById(R.id.book_also_like_recyler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        book_also_like_recyler.setLayoutManager(manager);
        book_also_like_recyler.setAdapter(mBookAlsoLikeAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(mBookInfo.getAltUrl()).get();
                    Elements content = document.select("div#wrapper").select("div#content").select("div.grid-16-8.clearfix").select("div.article").select("div.related_info").select("div#db-rec-section.block5.subject_show.knnlike").select("div.content.clearfix").select("dl");
                    for (int i = 0; i < content.size(); i++) {
                        if (i != 5 && i != 11) {
                            Element element = content.get(i);
                            String id = element.select("dt").select("a").attr("href").substring(32, 39);
                            String imgUrl = element.select("dt").select("a").select("img").attr("src");
                            String name = element.select("dd").select("a").text();
                            mBookInfoList.add(new BookInfo(id, imgUrl, name));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mBookAlsoLikeAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Picasso.with(this).load(Uri.parse(mBookInfo.getImgUrl())).fit().centerCrop().into(book_detail_img);
        book_name.setText(mBookInfo.getName());
        String book_detail_info_string = "作者: " + mBookInfo.getAuthor() + "\n"
                + "出版社: " + mBookInfo.getPublisher() + "\n"
                + "页数: " + mBookInfo.getPages();
        book_detail_info.setText(book_detail_info_string);
        short_summary.setText(mBookInfo.getBookSummary());
        long_summary.setText(mBookInfo.getBookSummary());
        show_hide.setText("展开");
        author_info.setText(mBookInfo.getAuthor_intro());
        show_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (short_summary.getVisibility() == View.VISIBLE) {
                    short_summary.setVisibility(View.GONE);
                    long_summary.setVisibility(View.VISIBLE);
                    show_hide.setText("隐藏");
                } else {
                    short_summary.setVisibility(View.VISIBLE);
                    long_summary.setVisibility(View.GONE);
                    show_hide.setText("展开");
                }
            }
        });
        if(mBookCollect.hasCollected(mBookInfo.getId())){
            hasCollected = true;
            fab.setImageResource(R.mipmap.rb1);
        }
    }

    private class BookAlsoLikeAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(BookDetailActivity.this);
            View v = inflater.inflate(R.layout.actor_item, parent, false);
            return new BookAlsoLikeHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            BookAlsoLikeHolder bookAlsoLikeHolder = (BookAlsoLikeHolder) holder;
            Picasso.with(BookDetailActivity.this).load(mBookInfoList.get(position).getImgUrl()).fit().centerCrop().into(bookAlsoLikeHolder.img);
            bookAlsoLikeHolder.name.setText(mBookInfoList.get(position).getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncTask task = new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            return FetchMovies.GetBookInfoById(mBookInfoList.get(position).getId());
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            mBookAlsoLike = (BookInfo) o;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("book", mBookAlsoLike);
                            Intent intent = new Intent(BookDetailActivity.this, BookDetailActivity.class);
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
            return mBookInfoList.size();
        }
    }

    public class BookAlsoLikeHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView name;

        public BookAlsoLikeHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.actor_image);
            name = itemView.findViewById(R.id.actor_name);
        }
    }

    @Override
    protected void onDestroy() {
        //Log.i(TAG,"onDestroy");
        setResult(CollectedBookFragment.REQEST_CODE);
        super.onDestroy();

    }

    private void collectBook(Context context){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String newDate = format.format(date);
        mBookCollect.addBook(mBookInfo.getId(),newDate);
    }
}
