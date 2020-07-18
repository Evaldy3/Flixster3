package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.internal.http2.Header;

public class MainActivity extends AppCompatActivity {

    public static final String NOW_PLAYING_URL ="https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed" ;
    public static final String TAG = "MainActivity";

    List<Movie>list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rvMovies = findViewById(R.id.rvMovies);
        list = new ArrayList<>();
        // Create the adapter
        final MovieAdapter movieAdapter = new MovieAdapter(list, MainActivity.this);
        // Set a Layout manager on Recycle view
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        // Set the adapter on Recycler View
            rvMovies.setAdapter(movieAdapter);


        AsyncHttpClient client= new AsyncHttpClient() ;
       client.get(NOW_PLAYING_URL, new JsonHttpResponseHandler() {
           @Override
           public void onSuccess(int statusCode, Headers headers, JSON json ) {
               Log.d(TAG , "onSuccess");
               //JSONObject jsonObject = json.jsonObject:
               try {
                   JSONArray results = json.jsonObject.getJSONArray("results");
                   Log.i(TAG , "results" + results.toString());
                   list.addAll(Movie.fromJsonArray(results));
                   movieAdapter.notifyDataSetChanged();
                   Log.i(TAG , "Movies" + list.size());
               } catch (JSONException e) {
                 Log.e(TAG ,"Hit json exception",e);
               }

           }



           @Override
           public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

           }
       });



    }
}
