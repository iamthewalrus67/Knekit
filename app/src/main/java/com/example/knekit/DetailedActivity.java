package com.example.knekit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DetailedActivity extends AppCompatActivity {
    private Map<String, Object> movie;
    private ListView episodesListView;
    private ImageView moviePosterImageView;
    private TextView movieTitleTextView;
    private TextView movieDescriptionTextView;
    private TextView numberOfSeasonsTextView;
    private Button addToFavoritesButton;
    private Button addToWatchlistButton;
    private TextView showSeasonInfoTextView;
    private Spinner chooseSeasonNumberSpinner;
    private LinearLayout seasonInfoLinearLayout;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private ArrayAdapter<Integer> seasonSpinnerAdapter;
    private EpisodesListAdapter episodesListViewAdapter;
    private ArrayList<Map<String, Object>> episodes;
    private int id;
    private int numberOfSeasons;
    private Integer[] seasons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        episodesListView = findViewById(R.id.list_view_episodes);
        moviePosterImageView = findViewById(R.id.img_detail_movie_poster);
        movieTitleTextView = findViewById(R.id.tv_detail_movie_title);
        movieDescriptionTextView = findViewById(R.id.tv_detail_movie_description);
        numberOfSeasonsTextView = findViewById(R.id.tv_detail_number_of_seasons);
        addToFavoritesButton = findViewById(R.id.button_add_to_favorites);
        addToWatchlistButton = findViewById(R.id.button_add_to_watchlist);
        chooseSeasonNumberSpinner = findViewById(R.id.spinner_choose_season);
        seasonInfoLinearLayout = findViewById(R.id.linear_layout_season_info);
        showSeasonInfoTextView = findViewById(R.id.button_show_season_info);
        id = getIntent().getIntExtra("id", 1);
        //episodes = JSONHelper.getTVEpisodes(id, 1);
        movie = JSONHelper.getTVShowWithId(id);
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        numberOfSeasons = (int)movie.get("number_of_seasons");
        seasons = new Integer[numberOfSeasons];
        for (int i = numberOfSeasons; i >=1; i--){
            seasons[numberOfSeasons-i]=i;
        }

        movieTitleTextView.setText((String)movie.get("name"));
        movieDescriptionTextView.setText((String)movie.get("overview"));
        if(numberOfSeasons>1){
            numberOfSeasonsTextView.setText(numberOfSeasons+" Seasons");
        }

        //Постер
        Picasso.with(this)
                .load((String)movie.get("poster_path"))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.mipmap.ic_launcher)
                .into(moviePosterImageView);

        //Выбор сезона
        seasonSpinnerAdapter = new ArrayAdapter<Integer>(this, R.layout.spinner_item_seasons, seasons);
        seasonSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        chooseSeasonNumberSpinner.setAdapter(seasonSpinnerAdapter);
        chooseSeasonNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //episodes = JSONHelper.getTVEpisodes((int) id, seasons[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        episodes = JSONHelper.getTVEpisodes(id, (Integer) chooseSeasonNumberSpinner.getSelectedItem());

        //Список эпизодов
        episodesListViewAdapter = new EpisodesListAdapter(this, R.layout.episode_list_item, episodes);
        episodesListView.setAdapter(episodesListViewAdapter);

        //Кнопка добавления в избранные
        addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
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
        });

        //Кнопка добавления в вотчлист
        addToWatchlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").document(firebaseUser.getEmail()).collection("watchlist").document((String)movie.get("name")).set(movie)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DetailedActivity.this, "Added to watchlist", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailedActivity.this, "Task failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //Выпадающая информация про сериал с анимацией
        showSeasonInfoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seasonInfoLinearLayout.getVisibility()==View.VISIBLE) {
                    showSeasonInfoTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_24dp, 0);
                    seasonInfoLinearLayout.animate()
                            .translationY(seasonInfoLinearLayout.getHeight())
                            .alpha(0.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    seasonInfoLinearLayout.setVisibility(View.GONE);
                                }
                            });
                }else{
                    showSeasonInfoTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_24dp, 0);
                    seasonInfoLinearLayout.animate()
                                .translationY(0)
                                .alpha(1.0f)
                                .setDuration(300)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        seasonInfoLinearLayout.setVisibility(View.VISIBLE);
                                    }
                                });
                }
            }
        });
    }
}
