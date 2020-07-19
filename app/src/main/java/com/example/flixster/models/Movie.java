package com.example.flixster.models;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
@Parcel
public class Movie  {
    int movieId;
    String backdrop_path;
    String posterPath;
    String title;
    String overview;
    String   rating;
    String date;
    public Movie(){


    }
    public Movie(JSONObject jsonObject) throws JSONException {
        backdrop_path= jsonObject.getString("backdrop_path");
        posterPath = jsonObject.getString("poster_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        rating =jsonObject.getString("vote_average");
        movieId = jsonObject.getInt("id");
        date = jsonObject.getString("release_date");
    }
     public static List<Movie> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Movie>list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            list.add(new Movie(jsonArray.getJSONObject(i)));
        }
        return list;
     }

    public String getPosterPath() {
        return  String.format("https://image.tmdb.org/t/p/w342/%s",posterPath);
    }
    public String getBackdrop_path(){
        return String.format("https://image.tmdb.org/t/p/w342/%s",backdrop_path);
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getRating() { return rating; }

    public int getMovieId() {
        return movieId;
    }

    public String getDate() { return date; }
}
