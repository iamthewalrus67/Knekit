package com.example.knekit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>{
    private ArrayList<Movie> mMovieList;
    private Context mContext;

    public MoviesAdapter(Context context, ArrayList<Movie> movieList){
        mMovieList = movieList;
        mContext = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, parent, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie currentItem = mMovieList.get(position);

        String title = currentItem.getTitle();
        String imageUrl = currentItem.getImgUrl();

        holder.movieTitleListItem.setText(title);
        Picasso.with(mContext)
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.ic_launcher_background)
                .into(holder.movieImage);
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{

        TextView movieTitleListItem;
        TextView viewHolderIndex;
        ImageView movieImage;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            viewHolderIndex = itemView.findViewById(R.id.tv_movie_index);
            movieTitleListItem = itemView.findViewById(R.id.tv_movie_title);
            movieImage = itemView.findViewById(R.id.img_movie_image);
        }
    }
}
