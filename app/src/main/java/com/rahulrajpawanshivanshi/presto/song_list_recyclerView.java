package com.rahulrajpawanshivanshi.presto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class song_list_recyclerView extends RecyclerView.Adapter<song_list_recyclerView.ViewHolder> {
    Context context;
    ArrayList<String> songs_list, url_list, movie_list, image_list, artist_list;
    song_list_recyclerViewInterface song_list_recyclerViewInterface;

    public song_list_recyclerView(Context context, ArrayList<String> songs_list, ArrayList<String> url_list, ArrayList<String> movie_list, ArrayList<String> image_list, ArrayList<String> artist_list, song_list_recyclerViewInterface song_list_recyclerViewInterface) {
        this.context = context;
        this.songs_list = songs_list;
        this.url_list = url_list;
        this.movie_list = movie_list;
        this.image_list = image_list;
        this.artist_list = artist_list;
        this.song_list_recyclerViewInterface=song_list_recyclerViewInterface;
    }

    @NonNull
    @Override
    public song_list_recyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_recycler_view, parent, false);
        return new ViewHolder(view, song_list_recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.recycler_song_name.setText(songs_list.get(position));
        holder.recycler_song_artist.setText(artist_list.get(position)+" "+movie_list.get(position));
        Picasso.get().load(image_list.get(position)).into(holder.recycler_song_image);
    }

    @Override
    public int getItemCount() {
        return songs_list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView recycler_song_image;
        TextView recycler_song_name, recycler_song_artist;
        public ViewHolder(@NonNull View itemView, song_list_recyclerViewInterface recyclerViewInterface) {
            super(itemView);
            recycler_song_image=itemView.findViewById(R.id.recycler_song_image);
            recycler_song_name=itemView.findViewById(R.id.recycler_song_name);
            recycler_song_artist=itemView.findViewById(R.id.recycler_ARTIST_NAME);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface!=null){
                        int pos=getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
