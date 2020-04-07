package com.example.knekit;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private MoviesAdapter movieAdapter;
    private ArrayList<Map<String, Object>> movieList;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Button logOutButton;
    private int page;
    //private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        page = 1;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        //db = FirebaseFirestore.getInstance();

        logOutButton = findViewById(R.id.button_log_out);
        recyclerView = findViewById(R.id.rv_movie_list);
        recyclerView.setHasFixedSize(true);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(getResources().getColor(R.color.transparent)); //Цвет затемнения фона
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        movieList = new ArrayList<>();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        movieList = JSONHelper.getPopularTVShows(page);
        movieAdapter = new MoviesAdapter(this, movieList);
        recyclerView.setAdapter(movieAdapter);
        movieAdapter.setOnBottomReachedListener(new MoviesAdapter.OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                movieList.addAll(JSONHelper.getPopularTVShows(++page));
            }
        });

        movieAdapter.setOnItemClickListener(new MoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //db.collection("movies").document("movie").set(movieList.get(position));
            }
        });

        //Кнопка выхода из аккаунта
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
