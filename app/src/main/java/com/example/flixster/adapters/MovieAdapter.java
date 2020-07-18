package com.example.flixster.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.DetailActivity;
import com.example.flixster.R;
import com.example.flixster.YoutubePlayerActivity;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;
import okhttp3.internal.http2.Header;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private static final String TRAILERS_API = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    private final int SIMPLE = 0,POPULAR = 1;
    private Context context;
    private List<Movie> moviesList;
    private int radius = 30;

    public MovieAdapter(List<Movie> moviesList,Context context) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(Double.valueOf(moviesList.get(position).getRating())>5.0){
            return POPULAR;
        }else {
            return SIMPLE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case SIMPLE:
                View mView = inflater.inflate(R.layout.item_movie,parent,false);
                viewHolder = new ViewHolderSimple(mView);
                break;
            case POPULAR:
                View view = inflater.inflate(R.layout.popular_movie,parent,false);
                viewHolder = new ViewHolderPopular(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case SIMPLE:
                ViewHolderSimple viewHolderSimple = (ViewHolderSimple) holder;
                bindDatatoViewHolderSimple(viewHolderSimple,position);
                break;
            case POPULAR:
                ViewHolderPopular viewHolderPopular = (ViewHolderPopular) holder;
                bindDatatoViewHolderPopular(viewHolderPopular,position);
                break;
        }

    }

    private void bindDatatoViewHolderSimple(ViewHolderSimple viewHolderSimple, int position) {
        final Movie movies = moviesList.get(position);
        RelativeLayout container = viewHolderSimple.container;
        TextView tvTitle = viewHolderSimple.tvTitle;
        tvTitle.setText(movies.getTitle());
        TextView ratingBar = viewHolderSimple.ratingBar;
        ratingBar.setText(movies.getRating());
        TextView date = viewHolderSimple.date;
        date.setText(movies.getDate());
        String imageUrl;
        // if phone is in landscape
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            // imageUrl = back drop image
            imageUrl = movies.getBackdrop_path();
        }else{
            // else imageUrl  = poster image
            imageUrl = movies.getPosterPath();
        }
        int radius = 20;
        int margin = 5;
        ImageView ivPoster = viewHolderSimple.ivPoster;
        Glide.with(context)
                .load(imageUrl)
                .transform(new RoundedCornersTransformation(radius , margin))
                .into(ivPoster);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context , DetailActivity.class);
                i.putExtra("movie", Parcels.wrap(movies));
                context.startActivity(i);
            }
        });
    }

    public void  bindDatatoViewHolderPopular(ViewHolderPopular viewHolderPopular, int position){
        final Movie movies = moviesList.get(position);
        ImageView ivBackdrop = viewHolderPopular.backdrop;
        FrameLayout layout = viewHolderPopular.layout;

        TextView tvTitle = viewHolderPopular.tvPopTitle;
        tvTitle.setText(movies.getTitle());
        TextView tvOverview = viewHolderPopular.tvPopOverview;
        tvOverview.setText(movies.getOverview());

        String imgUrl = movies.getBackdrop_path();
        Glide.with(context)
                .load(imgUrl)
                .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.error))
                .apply(new RequestOptions().transforms(new CenterInside(),new RoundedCorners(radius)))
                .into(ivBackdrop);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FetchTrailerKey
                Log.i("IdAdapter", String.valueOf(movies.getMovieId()));
                fetchTrailerKey(movies.getMovieId());
            }
        });
        //when long clicked show details
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("movies", Parcels.wrap(movies));
                context.startActivity(intent);
                return true;
            }
        });
    }

    private void fetchTrailerKey(int movieId) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(TRAILERS_API,Integer.parseInt(String.valueOf(movieId))),new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONObject array =json.jsonObject;
                    if(array.length()==0){
                        return;
                    }
                    JSONObject jsonObject = array.getJSONObject(String.valueOf(0));
                    String trailer_key = jsonObject.getString("key");
                    Log.i("KeyAdapter","key "+trailer_key);

                    Intent intent = new Intent(context, YoutubePlayerActivity.class);
                    intent.putExtra("key",trailer_key);
                    context.startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context,"Error : "+e,Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }

        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public  class ViewHolderPopular extends RecyclerView.ViewHolder{
        public ImageView backdrop;
        public TextView tvPopTitle;
        public TextView tvPopOverview;
        public FrameLayout layout;

        public ViewHolderPopular(@NonNull View itemView) {
            super(itemView);
            backdrop = (ImageView) itemView.findViewById(R.id.ivBackdrop);
            tvPopTitle = (TextView) itemView.findViewById(R.id.tvPopTitle);
            tvPopOverview = (TextView) itemView.findViewById(R.id.tvPopOverview);
            layout = (FrameLayout) itemView.findViewById(R.id.trailerPopularLayout);
        }


    }
    public class ViewHolderSimple extends RecyclerView.ViewHolder{
        public ImageView ivPoster ;
        public TextView ratingBar;
        public TextView tvTitle;
        public TextView date;
       public  RelativeLayout container;

        public ViewHolderSimple(@NonNull View itemView) {
            super(itemView);
            ivPoster = (ImageView) itemView.findViewById(R.id.ivPoster);
            ratingBar = (TextView) itemView.findViewById(R.id.ratingBar);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }


}
