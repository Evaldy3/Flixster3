package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {
   private static final String YOUTUBE_API_KEY = "AIzaSyAFLZuLldFslEtcStkPgvZ7E4NgQAb7w8E";
   private static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    List<Movie> movies;
    Movie movie;
   TextView tvTitle;
   ImageView ivPoster;
   TextView tvOverview;
    TextView ratingBar;
    YouTubePlayerView youTubePlayerView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        tvTitle =findViewById(R.id.tvTitle);
       tvOverview =findViewById(R.id.tvOverview);
       ratingBar =findViewById(R.id.ratingBar);
        youTubePlayerView = findViewById(R.id.player);
        ivPoster = findViewById(R.id.ivPoster);

       // Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie")) ;
       movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvTitle.setText(movie.getTitle());
       tvOverview.setText(movie.getOverview());
       ratingBar.setText(movie.getRating());

       Glide.with(DetailActivity.this)
               .load(movie.getPosterPath())
               .transform(new CircleCrop())
               .into(ivPoster);




        AsyncHttpClient client= new AsyncHttpClient() ;
        client.get(String.format(VIDEOS_URL, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray results =  json.jsonObject.getJSONArray("results");
                    if (results.length() == 0){
                        return;
                    }
                   String youtubeKey =  results.getJSONObject(0).getString("key");
                    Log.d("DetailActivity" , youtubeKey);
                    initializeYoutube(youtubeKey);
                } catch (JSONException e) {
                   Log.e("DetailActivity" , "Failed to parse JSON" , e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });

    }

    private void initializeYoutube(final String youtubeKey) {
        youTubePlayerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d("DetailActivity", "onInitializationSuccess");
                youTubePlayer.cueVideo(youtubeKey);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("DetailActivity", "onInitializationFailure");
            }
        });
    }
}