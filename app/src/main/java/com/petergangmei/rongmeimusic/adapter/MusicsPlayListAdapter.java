package com.petergangmei.rongmeimusic.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.petergangmei.rongmeimusic.ItemClickInterface;
import com.petergangmei.rongmeimusic.R;
import com.petergangmei.rongmeimusic.model.Music;

import java.io.IOException;
import java.util.List;

public class MusicsPlayListAdapter extends RecyclerView.Adapter<MusicsPlayListAdapter.ViewHolder> {

    List<Music> musicList;
    private  ItemClickInterface itemClickInterface;
    Context context;

    public MusicsPlayListAdapter(List<Music> musicList, ItemClickInterface itemClickInterface, Context context) {
        this.musicList = musicList;
        this.itemClickInterface = itemClickInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MusicsPlayListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Music music =  musicList.get(position);
        holder.songtitle.setText(music.getTitle());
        holder.singer.setText(music.getArtist());
        holder.count.setText(""+music.getId());
        if (music.getCoverUrl().equals("null")){
            Glide.with(context).load(R.drawable.rongmei_music_logo1000).into(holder.imageView);
        }else {
            Glide.with(context).load(music.getCoverUrl()).into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ppl = music.getGenre();
                String currentPlayList;
                if (ppl.equals("Gospel")){
                    currentPlayList = "GosplayPlayLIST";
                }else {
                    currentPlayList = "TopSongsPlayLIST";
                }
                itemClickInterface.onItemClick(position,currentPlayList,music.getId(), music.getSongUrl(), music.getTitle(), music.getArtist());
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView singer, songtitle,count;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            singer = itemView.findViewById(R.id.singer);
            songtitle = itemView.findViewById(R.id.title);
            count = itemView.findViewById(R.id.count);
        }
    }
}
