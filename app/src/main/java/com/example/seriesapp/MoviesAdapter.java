package com.example.seriesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>{

    private int numberOfItems;
    private static int viewHolderCount;

    public MoviesAdapter(int numberItems){
        numberOfItems = numberItems;
        viewHolderCount = 0;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int listItemId = R.layout.movie_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(listItemId, parent, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        viewHolder.viewHolderIndex.setText("View holder index:" + viewHolderCount);

        viewHolderCount++;

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return numberOfItems;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{

        TextView movieTitleListItem;
        TextView viewHolderIndex;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            viewHolderIndex = itemView.findViewById(R.id.tv_movie_index);
            movieTitleListItem = itemView.findViewById(R.id.tv_movie_title);
        }

        void bind(int listIndex){
            movieTitleListItem.setText(String.valueOf(listIndex));
        }
    }
}
