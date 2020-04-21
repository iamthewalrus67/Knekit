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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private MoviesAdapter movieAdapter;
    private ArrayList<Map<String, Object>> movieList;
    private FirebaseAuth firebaseAuth;
    private Button logOutButton;
    private Spinner filterSpinner;
    private TextView testTextView;
    private ArrayAdapter<String> spinnerAdapter;
    private String option;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        page = 1;
        option = "popular";

        firebaseAuth = FirebaseAuth.getInstance();

        testTextView = findViewById(R.id.tv_test);
        filterSpinner = findViewById(R.id.spinner_main_filter);
        logOutButton = findViewById(R.id.button_log_out);
        recyclerView = findViewById(R.id.rv_movie_list);
        recyclerView.setHasFixedSize(true);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(getResources().getColor(R.color.transparent)); //Цвет затемнения фона
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        movieList = JSONHelper.getTVShows(page, option);
        movieAdapter = new MoviesAdapter(MainActivity.this, movieList);
        recyclerView.setAdapter(movieAdapter);

        spinnerAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, getResources().getStringArray(R.array.string_array_main_filter));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        filterSpinner.setAdapter(spinnerAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        page = 1;
                        option = "popular";
                        testTextView.append(option);
                        break;
                    case 1:
                        page = 1;
                        option = "top_rated";
                        testTextView.append(option);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        movieAdapter.setOnBottomReachedListener(new MoviesAdapter.OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                movieList.addAll(JSONHelper.getTVShows(++page, option));
            }
        });

        movieAdapter.setOnItemClickListener(new MoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                intent.putExtra("id", (Integer) movieList.get(position).get("id"));
                startActivity(intent);
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

    private void loadTVShows(String option){
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
