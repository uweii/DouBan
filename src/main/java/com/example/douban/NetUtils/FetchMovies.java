package com.example.douban.NetUtils;

import android.util.Log;

import com.example.douban.po.BookInfo;
import com.example.douban.po.MovieInfo;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by uwei on 2018/1/31.
 */

public class FetchMovies {
    private String TAG = "FetchMovies";
    private String MOVIE_BASE_URL = "https://api.douban.com/v2/movie/search?tag="; //按tag查询电影
    private String BOOK_BASE_URL = "https://api.douban.com/v2/book/search?tag="; //按tag查询书籍
    private static String GET_MOVIE_DETAIL_URL = "https://api.douban.com/v2/movie/subject/";   //返回电影的json
    private static String GET_BOOK_DETAIL_URL = "https://api.douban.com/v2/book/";    //返回book的json
    private static String GET_TOP_MOVIE_BASE_URL = "https://api.douban.com/v2/movie/"; //查询电影排行url前缀
    private static String EVERYDAY_IMG_URL = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
    public List<MovieInfo> getMoviesByTag(String tag, int start) {
        List<MovieInfo> movieInfoList = new ArrayList<>();
        String url = MOVIE_BASE_URL + tag;
        url = url + "&start=" + start;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                //Log.i(TAG,result);
                JSONObject resultObject = new JSONObject(result);
                JSONArray subjects = resultObject.getJSONArray("subjects");
                for (int i = 0; i < subjects.length(); i++) {
                    JSONObject movieOject = subjects.getJSONObject(i);
                    JSONObject ratingObject = movieOject.getJSONObject("rating");
                    double movieAverage = ratingObject.getDouble("average");
                    String movieName = movieOject.getString("title");
                    JSONObject imgObject = movieOject.getJSONObject("images");
                    String movieImg = imgObject.getString("small");
                    String id = movieOject.getString("id");
                    String movieAlt = movieOject.getString("alt");
                    JSONArray genresArray = movieOject.getJSONArray("genres");
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < genresArray.length(); j++) {    //获取电影类型
                        sb.append(genresArray.get(j));
                        if (j != genresArray.length() - 1) {
                            sb.append("/");
                        }
                    }
                    //Log.d(TAG, sb.toString());
                    String movieYear = movieOject.getString("year");
                    MovieInfo movieInfo = new MovieInfo(movieImg, id, movieName, movieAverage, movieAlt, sb.toString(), movieYear);
                    movieInfoList.add(movieInfo);
                }
               // Log.i(TAG,movieInfoList.get(0).getAverage()+"");
                return movieInfoList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MovieInfo getMovieInfoById(String id) {
        String movieUrl = GET_MOVIE_DETAIL_URL + id;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(movieUrl).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                //Log.i(TAG,result);
                JSONObject resultObject = new JSONObject(result);
                String movieImg = resultObject.getJSONObject("images").getString("small");
                String movieName = resultObject.getString("title");
                String movieAlt = resultObject.getString("alt");
                JSONArray genresArray = resultObject.getJSONArray("genres");
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < genresArray.length(); j++) {    //获取电影类型
                    sb.append(genresArray.get(j));
                    if (j != genresArray.length() - 1) {
                        sb.append("/");
                    }
                }
                String movieYear = resultObject.getString("year");
                MovieInfo movieInfo = new MovieInfo(movieImg, id, movieName, movieAlt, sb.toString(), movieYear);
                return movieInfo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<BookInfo> getBooksByTag(String tag, int start) {
        List<BookInfo> bookInfoList = new ArrayList<>();
        String url = BOOK_BASE_URL + tag;
        url = url + "&start=" + start;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                //Log.i(TAG,result);
                JSONObject resultObject = new JSONObject(result);
                JSONArray books = resultObject.getJSONArray("books");
                for (int i = 0; i < books.length(); i++) {
                    JSONObject movieOject = books.getJSONObject(i);
                    JSONObject ratingObject = movieOject.getJSONObject("rating");
                    double bookAverage = ratingObject.getDouble("average");
                    String bookName = movieOject.getString("title");
                    JSONObject imgObject = movieOject.getJSONObject("images");
                    String movieImg = imgObject.getString("large");
                    String id = movieOject.getString("id");
                    String author_intro = movieOject.getString("author_intro");
                    String summary = movieOject.getString("summary");
                    String bookAlt = movieOject.getString("alt");
                    String apiUrl = movieOject.getString("url");
                    String author = movieOject.getJSONArray("author").getString(0);
                    String pages = movieOject.getString("pages");
                    String publisger = movieOject.getString("publisher");
                    //Log.d(TAG, sb.toString());
                    BookInfo bookInfo = new BookInfo(movieImg, bookAverage, id, author, pages, bookAlt, publisger, bookName, apiUrl, author_intro, summary);
                    bookInfoList.add(bookInfo);
                }
                return bookInfoList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BookInfo GetBookInfoById(String id) {
        String url = GET_BOOK_DETAIL_URL + id;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                JSONObject resultObject = new JSONObject(result);
                JSONObject ratingObject = resultObject.getJSONObject("rating");
                double bookAverage = ratingObject.getDouble("average");
                String bookName = resultObject.getString("title");
                JSONObject imgObject = resultObject.getJSONObject("images");
                String movieImg = imgObject.getString("large");
                String mId = resultObject.getString("id");
                String author_intro = resultObject.getString("author_intro");
                String summary = resultObject.getString("summary");
                String bookAlt = resultObject.getString("alt");
                String apiUrl = resultObject.getString("url");
                String author = resultObject.getJSONArray("author").getString(0);
                String pages = resultObject.getString("pages");
                String publisger = resultObject.getString("publisher");
                BookInfo bookInfo = new BookInfo(movieImg, bookAverage, mId, author, pages, bookAlt, publisger, bookName, apiUrl, author_intro, summary);
                return bookInfo;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MovieInfo> getTopMoviesByTag(String tag, int start) {
        List<MovieInfo> movieInfoList = new ArrayList<>();
        String url = GET_TOP_MOVIE_BASE_URL + tag;
        url = url + "?start=" + start;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                //Log.i(TAG,result);
                JSONObject resultObject = new JSONObject(result);
                JSONArray subjects = resultObject.getJSONArray("subjects");
                for (int i = 0; i < subjects.length(); i++) {
                    JSONObject movieOject = subjects.getJSONObject(i);
                    JSONObject ratingObject = movieOject.getJSONObject("rating");
                    double movieAverage = ratingObject.getDouble("average");
                    String movieName = movieOject.getString("title");
                    JSONObject imgObject = movieOject.getJSONObject("images");
                    String movieImg = imgObject.getString("small");
                    String id = movieOject.getString("id");
                    String movieAlt = movieOject.getString("alt");
                    JSONArray genresArray = movieOject.getJSONArray("genres");
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < genresArray.length(); j++) {    //获取电影类型
                        sb.append(genresArray.get(j));
                        if (j != genresArray.length() - 1) {
                            sb.append("/");
                        }
                    }
                    //Log.d(TAG, sb.toString());
                    String movieYear = movieOject.getString("year");
                    MovieInfo movieInfo = new MovieInfo(movieImg, id, movieName, movieAverage, movieAlt, sb.toString(), movieYear);
                    movieInfoList.add(movieInfo);
                }
                return movieInfoList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> EveryDayImg(){
        List<String> list = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(EVERYDAY_IMG_URL).build();
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                String result = response.body().string();
                JSONObject object = new JSONObject(result);
                JSONObject imgObj = object.getJSONArray("images").getJSONObject(0);
                String url = imgObj.getString("url");
                String finalUrl = "https://cn.bing.com" + url;
                String title = imgObj.getString("copyright");
                int end = title.indexOf("(");
                String finalTitle = title.substring(0,end);
                list.add(finalUrl);
                list.add(finalTitle);
                return list;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
