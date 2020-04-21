package com.example.knekit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class EpisodesListAdapter extends ArrayAdapter<Map<String, Object>> {
    private Context context;
    private int resource;

    public EpisodesListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Map<String, Object>> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Map<String, Object> episode = getItem(position);
        convertView = LayoutInflater.from(context).inflate(resource, parent, false);

        TextView title = convertView.findViewById(R.id.tv_episode_title);
        TextView description = convertView.findViewById(R.id.tv_episode_description);
        ImageView preview = convertView.findViewById(R.id.img_episode_preview);

        title.setText((String)episode.get("name"));
        description.setText((String)episode.get("overview"));
        Picasso.with(context)
                .load((String)episode.get("still_path"))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.mipmap.ic_launcher)
                .into(preview);

        return convertView;
    }
}
