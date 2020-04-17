package com.example.knekit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class DetailedActivity extends AppCompatActivity {
    private Map<String, Object> movie;
    private ImageView moviePosterImageView;
    private TextView movieTitleTextView;
    private TextView movieDescriptionTextView;
    private TextView numberOfSeasonsTextView;
    private Button addToFavoritesButton;
    private Button addToWatchlistButton;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        moviePosterImageView = findViewById(R.id.img_detail_movie_poster);
        movieTitleTextView = findViewById(R.id.tv_detail_movie_title);
        movieDescriptionTextView = findViewById(R.id.tv_detail_movie_description);
        numberOfSeasonsTextView = findViewById(R.id.tv_detail_number_of_seasons);
        addToFavoritesButton = findViewById(R.id.button_add_to_favorites);
        addToWatchlistButton = findViewById(R.id.button_add_to_watchlist);
        movie = JSONHelper.getTVShowWithId(getIntent().getIntExtra("id", 1));
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        movieTitleTextView.setText((String)movie.get("name"));
        movieDescriptionTextView.setText((String)movie.get("overview"));
        if((int)movie.get("number_of_seasons")>1){
            numberOfSeasonsTextView.setText(movie.get("number_of_seasons")+" Seasons");
        }
        Picasso.with(this)
                .load((String)movie.get("poster_path"))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.mipmap.ic_launcher)
                .into(moviePosterImageView);

        /*addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").document(firebaseUser.getEmail()).collection("favorites").document((String)movie.get("name")).set(movie)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DetailedActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailedActivity.this, "Task failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });*/
    }
}
