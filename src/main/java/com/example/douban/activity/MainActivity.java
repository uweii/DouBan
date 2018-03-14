package com.example.douban.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.douban.NetUtils.CheckNet;
import com.example.douban.NetUtils.FetchMovies;
import com.example.douban.R;
import com.example.douban.SharePre.SharePreUtil;
import com.example.douban.fragment.CollectedBookFragment;
import com.example.douban.fragment.Fragment_Book;
import com.example.douban.fragment.Fragment_Edit_Tag;
import com.example.douban.fragment.Fragment_Movie;
import com.example.douban.fragment.Fragment_Settings;
import com.example.douban.fragment.Fragment_Top_Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private  FragmentManager fm;
    private Fragment mMovieFragment;
    private Fragment mBookFragment;
    private Fragment mTopMovieFragment;
    private Fragment mCollectBook;
    private Fragment mEditTagFragment;
    private Fragment mSettingsFragment;
    private int mCurrent = 0;
    private LinearLayout mLayout;
    private List<String> mEveryDayInfo = new ArrayList<>();
    private TextView mEveryDayTitle;
    NavigationView navigationView;
    public static String themeTag;
    public static int intThemeTag;
    public static int intPaperTag;
    private long firstClickTime = 0,secondClickTime = 0;
    private boolean isFirstClick = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeTag = SharePreUtil.getThemeTag(this);
        intPaperTag = SharePreUtil.getWallPaperTag(this);
        if(themeTag.equals("0")){
            intThemeTag = 0;
            setTheme(R.style.dayTheme);
        }else{
            intThemeTag = 1;
            setTheme(R.style.nightTheme);
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headView = navigationView.getHeaderView(0); //先获取navigation的headView
        mLayout = headView.findViewById(R.id.nav_header_root);  //通过headView来获取里面的部件
        mEveryDayTitle = headView.findViewById(R.id.every_day_title);
        navigationView.setNavigationItemSelectedListener(this);
        mMovieFragment = Fragment_Movie.newInstance();
        fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragment_container,mMovieFragment).show(mMovieFragment).commit();
        //fm.beginTransaction().replace(R.id.fragment_container,mMovieFragment).commit();
        getSupportActionBar().setTitle("电影");
        navigationView.getMenu().getItem(0).setChecked(true);
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.i("MainActivity","onBitmapLoaded");
                mLayout.setBackground(new BitmapDrawable(bitmap));
                mEveryDayTitle.setText("每日一图："+mEveryDayInfo.get(1));
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.i("MainActivity","onPrepareLoad");
            }
        };
//        final ImageView imageView = new ImageView(this);
        if(!CheckNet.hasNetAccess(this)){
           Snackbar.make(findViewById(R.id.toolbar),"网络连接失败",Snackbar.LENGTH_SHORT).setAction("确定", null).show();
        }else {
            if(intPaperTag==1){
                AsyncTask task = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        return FetchMovies.EveryDayImg();
                    }
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        mEveryDayInfo = (List<String>) o;
                        final ImageView imageView = new ImageView(MainActivity.this);//创建一个ImageView
                        Picasso.with(MainActivity.this).load(mEveryDayInfo.get(0)).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                mLayout.setBackground(imageView.getDrawable()); //通过ImageView获取drawable，
                                mEveryDayTitle.setText("每日一图："+ mEveryDayInfo.get(1));//显示在LinerLayout里
                            }
                            @Override
                            public void onError() {

                            }
                        });
                    }
                };
                task.execute();
            }

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);  //不显示菜单
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        hideFragment();
        if (id == R.id.nav_movie) {
            // Handle the camera action
            fm.beginTransaction().show(mMovieFragment).commit();
            mCurrent = 0;
            getSupportActionBar().setTitle("电影");
        } else if (id == R.id.nav_books) {
            if(mBookFragment != null){
                fm.beginTransaction().show(mBookFragment).commit();
            }else{
                mBookFragment = Fragment_Book.newInstance();
                fm.beginTransaction().add(R.id.fragment_container,mBookFragment).show(mBookFragment).commit();
            }
            mCurrent = 1;
            getSupportActionBar().setTitle("图书");
        } else if (id == R.id.nav_topmovie) {
            if(mTopMovieFragment != null){
                fm.beginTransaction().show(mTopMovieFragment).commit();
            }else{
                mTopMovieFragment = Fragment_Top_Movie.newInsance();
                fm.beginTransaction().add(R.id.fragment_container,mTopMovieFragment).show(mTopMovieFragment).commit();
            }
            mCurrent = 2;
            getSupportActionBar().setTitle("电影榜单");
        } else if (id == R.id.nav_collection) {
            if(mCollectBook != null){
                fm.beginTransaction().show(mCollectBook).commit();
            }else{
                mCollectBook = CollectedBookFragment.newInstance();
                fm.beginTransaction().add(R.id.fragment_container,mCollectBook).show(mCollectBook).commit();
            }
            mCurrent = 3;
            getSupportActionBar().setTitle("图书收藏");
        } else if (id == R.id.nav_edittag) {
            if(mEditTagFragment != null){
                fm.beginTransaction().show(mEditTagFragment).commit();
            }else {
                mEditTagFragment = Fragment_Edit_Tag.newInstance();
                fm.beginTransaction().add(R.id.fragment_container,mEditTagFragment).show(mEditTagFragment).commit();
            }
            mCurrent = 4;
            getSupportActionBar().setTitle("更改标签");
        } else if (id == R.id.nav_setting) {
            if(mSettingsFragment != null){
                fm.beginTransaction().show(mSettingsFragment).commit();
            }else{
                mSettingsFragment = Fragment_Settings.newInstance();
                fm.beginTransaction().add(R.id.fragment_container,mSettingsFragment).show(mSettingsFragment).commit();
            }
            mCurrent = 5;
            getSupportActionBar().setTitle("设置");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void hideFragment(){
       switch (mCurrent){
           case 0:
               fm.beginTransaction().hide(mMovieFragment).commit();
               break;
           case 1:
               fm.beginTransaction().hide(mBookFragment).commit();
               break;
           case 2:
               fm.beginTransaction().hide(mTopMovieFragment).commit();
               break;
           case 3:
               fm.beginTransaction().hide(mCollectBook).commit();
               break;
           case 4:
               fm.beginTransaction().hide(mEditTagFragment).commit();
               break;
           case 5:
               fm.beginTransaction().hide(mSettingsFragment).commit();
               break;
       }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(isFirstClick){
                firstClickTime = System.currentTimeMillis();
                showToast(MainActivity.this,"再按一次退出程序");
                isFirstClick = false;
                return true;
            }else {
                secondClickTime = System.currentTimeMillis();
                if(secondClickTime-firstClickTime<1500){
                    finish();
                }else {
                    firstClickTime = secondClickTime;
                    showToast(MainActivity.this,"再按一次退出程序");
                    return true;
                }
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    public  void showToast(Context context,String msg)
    {
        Toast toast = new Toast(context);
        View v = LayoutInflater.from(context).inflate(R.layout.toast_layout,null);
        ((TextView)v.findViewById(R.id.toast_msg)).setText(msg);
        toast.setView(v);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
