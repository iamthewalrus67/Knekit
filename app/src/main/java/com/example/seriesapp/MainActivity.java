package com.example.seriesapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private MoviesAdapter moviesAdapter;
    private ArrayList<Movie> movieList;
    private String JSONUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        JSONUrl = "https://api.themoviedb.org/3/trending/tv/week?api_key=45b65d61b990414499da78ba05f16d4e";
        JSONParser jsonParser = new JSONParser();
        jsonParser.execute(JSONUrl);
    }


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    class JSONParser extends AsyncTask<String, Void, String>{
        URL url = null;
        HttpURLConnection httpURLConnection = null;
        StringBuilder result = new StringBuilder();

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while(line!=null){
                    result.append(line);
                    line=bufferedReader.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject object = new JSONObject(s);
                JSONArray jsonArray = object.getJSONArray("results");
                String title="";
                String baseImgUrl = "https://image.tmdb.org/t/p/w500";
                String posterPath="";
                String fullUrl = "";
                for (int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    title = jsonObject.getString("name");
                    posterPath = jsonObject.getString("poster_path");
                    fullUrl = baseImgUrl+posterPath;
                    movieList.add(new Movie(title, fullUrl));
                }
                moviesAdapter = new MoviesAdapter(MainActivity.this, movieList);
                recyclerView.setAdapter(moviesAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
