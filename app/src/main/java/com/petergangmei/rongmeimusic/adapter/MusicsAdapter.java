package com.petergangmei.rongmeimusic.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.petergangmei.rongmeimusic.ItemClickInterface;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.petergangmei.rongmeimusic.R;
import com.petergangmei.rongmeimusic.model.Music;

import java.io.IOException;
import java.util.List;

public class MusicsAdapter extends RecyclerView.Adapter<MusicsAdapter.ViewHolder> {

    List<Music> musicList;
    private  ItemClickInterface itemClickInterface;
    Context context;

    public MusicsAdapter(List<Music> musicList, ItemClickInterface itemClickInterface, Context context) {
        this.musicList = musicList;
        this.itemClickInterface = itemClickInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MusicsAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Music music =  musicList.get(position);
        holder.songtitle.setText(music.getTitle());
        holder.singer.setText(music.getArtist());
        Glide.with(context).load(music.getCoverUrl()).into(holder.imageView);



        final MediaPlayer audioPlayer = new MediaPlayer();
        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            audioPlayer.setDataSource(music.getSongUrl());
            audioPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickInterface.onItemClick(music.getId(), music.getSongUrl(), music.getTitle(), music.getArtist());

            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView singer, songtitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            singer = itemView.findViewById(R.id.singer);
            songtitle = itemView.findViewById(R.id.title);
        }
    }
}
