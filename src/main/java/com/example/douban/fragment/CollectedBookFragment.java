package com.example.douban.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.douban.NetUtils.FetchMovies;
import com.example.douban.R;
import com.example.douban.activity.BookCollect;
import com.example.douban.activity.BookDetailActivity;
import com.example.douban.po.BookInfo;
import com.example.douban.po.CollectedBook;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by uwei on 2018/3/5.
 */

public class CollectedBookFragment extends Fragment {
    private String TAG = "CollectedBookFragment";
    private RecyclerView mRecyclerView;
    private  List<CollectedBook> mCollectedBookList = new ArrayList<>();
    private  List<BookInfo> mBookInfoList = new ArrayList<>();
    private  BookCollect mBookCollect;
    private  BookAdapter mAdapter;
    private final static int NULLSTAR = 0x123;   //空星星
    private final static int HALFSTAR = 0x124;   //半星星
    private final static int FULLSTAR = 0x125;   //满星星
    public final static int REQEST_CODE = 0x126;


    public static CollectedBookFragment newInstance()
    {
        return new CollectedBookFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_collect, container, false);
        mRecyclerView = v.findViewById(R.id.book_recyler_view);
        init();
        return v;
    }


    public class BookAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.collect_book_item, parent, false);
            return new BookHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            BookHolder bookHolder = (BookHolder) holder;
            final BookInfo info = mBookInfoList.get(position);
            if(info == null){
                return;
            }
            Picasso.with(getActivity()).load(info.getImgUrl()).placeholder(R.mipmap.defaultimg).
                    into(((BookHolder) holder).img);
            if(bookHolder.mLayout.getChildCount()==0){
                setStar(info.getAverage(), (bookHolder).mLayout);
                TextView starNum = new TextView(getActivity());
                //starNum.setTextSize(18);
                starNum.setText("" + info.getAverage());
                bookHolder.mLayout.addView(starNum);
            }
            bookHolder.name.setText(info.getName());
            bookHolder.publisher.setText("出版社: " + info.getPublisher());
            bookHolder.author.setText("作者: " + info.getAuthor());
            bookHolder.date.setText("收藏时间: " + mCollectedBookList.get(position).getDate());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("book",  info);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,REQEST_CODE);
                }
            });
        }


        @Override
        public int getItemCount() {
            return mBookInfoList.size();
        }

        class BookHolder extends RecyclerView.ViewHolder {
            public ImageView img;
            public TextView name, author, publisher, date;
            public LinearLayout mLayout;

            public BookHolder(View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.iv_book_img);
                name = itemView.findViewById(R.id.tv_book_name);
                author = itemView.findViewById(R.id.tv_book_author);
                publisher = itemView.findViewById(R.id.tv_book_publisher);
                date = itemView.findViewById(R.id.tv_book_collect_date);
                mLayout = itemView.findViewById(R.id.ll_star_container);
            }
        }
    }



    private void init() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mAdapter = new BookAdapter();
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mBookCollect = BookCollect.getBookCollect(getActivity());
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                mCollectedBookList = mBookCollect.getCollectedBooks();
                for (int i = 0; i < mCollectedBookList.size(); i++) {
                    mBookInfoList.add(FetchMovies.GetBookInfoById(mCollectedBookList.get(i).getBookId()));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                mAdapter.notifyDataSetChanged();
            }
        };
        task.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.i(TAG, "onActivityResult");
//        Log.i(TAG,"resultCode===>"+resultCode + ",request==>" + requestCode);
        if(resultCode == 0){
            if(requestCode == REQEST_CODE){
                mCollectedBookList.clear();
                mBookInfoList.clear();
                AsyncTask task = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        mCollectedBookList = mBookCollect.getCollectedBooks();
                        for (int i = 0; i < mCollectedBookList.size(); i++) {
                            mBookInfoList.add(FetchMovies.GetBookInfoById(mCollectedBookList.get(i).getBookId()));
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        mAdapter.notifyDataSetChanged();
                    }
                };
                task.execute();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){

        }else {
            //Log.i("collectBootFragment", " is showing");
            mCollectedBookList.clear();
            mBookInfoList.clear();
            mCollectedBookList = mBookCollect.getCollectedBooks();
            AsyncTask task = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    for (int i = 0; i < mCollectedBookList.size(); i++) {
                        mBookInfoList.add(FetchMovies.GetBookInfoById(mCollectedBookList.get(i).getBookId()));
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    mAdapter.notifyDataSetChanged();
                }
            };
            task.execute();
        }
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

    @Override
    public void onResume() {
        super.onResume();
    }
}
