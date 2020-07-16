package com.example.flixster.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flixster.DetailActivity;
import com.example.flixster.R;
import com.example.flixster.models.Movie;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    Context context;
    List<Movie>movies;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }
    // Involves the populating data into the item through holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MovieAdapter" , "onCreateViewHolder");
       View movieView =  LayoutInflater.from(context).inflate(R.layout.item_movie , parent , false);
        return new ViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("MovieAdapter" , "onBindViewHolder" + position);
        // Get the movie at the passed in  position
        Movie movie = movies.get(position);
        // Bind the movie data in viewHolder
        holder.bind(movie);

    }
    // Return the total count of items on the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
       TextView ratingBar;
        ImageView ivPoster;
        RelativeLayout container;
        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
           ivPoster  = itemView.findViewById(R.id.ivPoster);
            container  = itemView.findViewById(R.id.container);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            date = itemView.findViewById(R.id.date);

        }

        public void bind(final Movie movie) {
            tvTitle.setText(movie.getTitle());
            ratingBar.setText(movie.getRating());
            date.setText(movie.getDate());
            String imageUrl;
            // if phone is in landscape
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                // imageUrl = back drop image
                imageUrl = movie.getBackdrop_path();
            }else{
                // else imageUrl  = poster image
                imageUrl = movie.getPosterPath();
            }
            int radius = 20;
            int margin = 5;
            Glide.with(context)
                    .load(imageUrl)
                    .transform(new RoundedCornersTransformation(radius , margin))
                   .into(ivPoster);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context , DetailActivity.class);
                    i.putExtra("movie", Parcels.wrap(movie));
                    context.startActivity(i);
                }
            });
        }
    }
}
