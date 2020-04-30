package com.example.knekit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

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
    private CollectionReference favoritesReference;
    private CollectionReference watchlistReference;
    private ArrayAdapter<Integer> seasonSpinnerAdapter;
    private EpisodesListAdapter episodesListViewAdapter;
    private ArrayList<Map<String, Object>> episodes;
    private int id;
    private int numberOfSeasons;
    private Integer[] seasons;
    private DrawerLayout drawerLayout;
    private Button logOutButton;
    private Button favoritesMenuButton;
    private Button mainPageMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        //Выпадающее меню
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(getResources().getColor(R.color.transparent)); //Цвет затемнения фона
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        logOutButton = findViewById(R.id.button_log_out);
        favoritesMenuButton = findViewById(R.id.menu_button_favorites);
        mainPageMenuButton = findViewById(R.id.menu_button_main);
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
        movie = JSONHelper.getTVShowWithId(id);
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        favoritesReference = db.collection("users").document(firebaseUser.getEmail()).collection("favorites");
        watchlistReference = db.collection("users").document(firebaseUser.getEmail()).collection("watchlist");

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
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(moviePosterImageView);

        //Выбор сезона
        seasonSpinnerAdapter = new ArrayAdapter<Integer>(this, R.layout.spinner_item_seasons, seasons);
        seasonSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        chooseSeasonNumberSpinner.setAdapter(seasonSpinnerAdapter);
        chooseSeasonNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadEpisodes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Кнопка добавления в вотчлист
        addToWatchlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchlistReference.document((String)movie.get("name")).set(movie)
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

        //Выпадающая информация про эпизоды с анимацией
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


        setMenuListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Кнопка добавления/удаления сериала из избранных
        favoritesReference.document(String.valueOf(id)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    addToFavoritesButton.setText("Remove from favorites");
                    addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            favoritesReference.document(String.valueOf(id)).delete();
                        }
                    });
                }else{
                    addToFavoritesButton.setText("Add to favorites");
                    addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            favoritesReference.document(String.valueOf(id)).set(movie);
                        }
                    });
                }
            }
        });
    }



    private void loadEpisodes(){
        episodes = JSONHelper.getTVEpisodes(id, (Integer) chooseSeasonNumberSpinner.getSelectedItem());
        episodesListViewAdapter = new EpisodesListAdapter(this, R.layout.episode_list_item, episodes);
        episodesListView.setAdapter(episodesListViewAdapter);

    }

    //Слушатели кнопок выпадающего меню
    private void setMenuListeners(){
        //Кнопка выхода из аккаунта
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DetailedActivity.this, AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        favoritesMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedActivity.this, FavoritesActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intent);
            }
        });

        mainPageMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(DetailedActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
}
