package com.petergangmei.rongmeimusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.StaticLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.SpinKitView;
import com.petergangmei.rongmeimusic.adapter.MusicsAdapter;
import com.petergangmei.rongmeimusic.model.Music;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickInterface{
    private static String URL = "http://music001.pythonanywhere.com/api";
    RecyclerView recyclerView;
    List<Music> musicList;
    MusicsAdapter musicsAdapter;
    ImageView playBtn;

    private ImageView btnPlayPause, btnSkipPrevious, btnSkipNext;
    private TextView songduration, totalsongduration, songTitletv, songArtisttv;
    private MediaPlayer mediaPlayer;
    private Handler  handler = new Handler();
    private SeekBar playerSeekBar;
    private ProgressBar loadingProgress;
    private LinearLayout playerLay;

    private String  positionV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.recyclerView);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnSkipPrevious = findViewById(R.id.btnSkipPrevious);
        btnSkipNext = findViewById(R.id.btnSkipNext);
        songduration = findViewById(R.id.songduration);
        totalsongduration = findViewById(R.id.totalsongduration);
        playerSeekBar = findViewById(R.id.playerSeekBar);
        loadingProgress = findViewById(R.id.loadingProgress);
        songTitletv = findViewById(R.id.songTitle);
        songArtisttv = findViewById(R.id.songArtist);
        mediaPlayer = new MediaPlayer();

        playerSeekBar.setMax(100);
        playerLay = findViewById(R.id.playerLay);


        callSplashDialog();
        extractMusics();
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                }else {
                    mediaPlayer.start();
                    btnPlayPause.setImageResource(R.drawable.ic_pause);
                    updateseekBar();
                }
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                btnPlayPause.setImageResource(R.drawable.ic_pause);
                totalsongduration.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
                mediaPlayer.start();
                loadingProgress.setVisibility(View.GONE);
                btnPlayPause.setVisibility(View.VISIBLE);
                updateseekBar();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                GetNextSong();
            }
        });

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    int _progress = (progress * mediaPlayer.getDuration())/100;
                    mediaPlayer.seekTo(_progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnSkipPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetPreviousSong();
            }
        });
        btnSkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetNextSong();
            }
        });

    }

    private void callSplashDialog() {
        final Dialog dialog = new Dialog(MainActivity.this,android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.splash_dialog);
        dialog.show();

       Handler h = new Handler();
       h.postDelayed(new Runnable() {
           @Override
           public void run() {
               dialog.hide();
           }
       },800);
    }

    private void GetPreviousSong() {
        RequestQueue queue = Volley.newRequestQueue(this);
        int dv = Integer.parseInt(positionV);
        final int newval = dv-1;
        String newURL = URL + "/"+newval;
        Log.d("TAG", "onResponse: next song "+newURL);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, newURL, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String data = response.toString();
                            JSONObject object = new JSONObject(data);
                            mediaPlayer.reset();
                            Log.d("TAG", "onResponse: next song "+object.getString("title")+String.valueOf(newval));
                            loadingProgress.setVisibility(View.VISIBLE);
                            btnPlayPause.setVisibility(View.GONE);
                            mediaPlayer.setDataSource(object.getString("songURL").toString());
                            mediaPlayer.prepareAsync();
                            songArtisttv.setText(object.getString("artist"));
                            songTitletv.setText(object.getString("title"));
                            songArtisttv.setVisibility(View.VISIBLE);
                            songTitletv.setVisibility(View.VISIBLE);
                            positionV = String.valueOf(newval);
                        }catch (Exception e){

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onErrorResponse: "+error.getMessage());
            }
        });
        queue.add(request);
    }

    private void GetNextSong() {
        RequestQueue queue = Volley.newRequestQueue(this);
        int dv = Integer.parseInt(positionV);
        final int newval = dv+1;
        String newURL = URL + "/"+newval;
        Log.d("TAG", "onResponse: next song "+newURL);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, newURL, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String data = response.toString();
                            JSONObject object = new JSONObject(data);
                            String urls = object.getString("songURL");
                            Log.d("TAG", "onResponse: next song "+object.getString("title")+positionV+urls);
                            loadingProgress.setVisibility(View.VISIBLE);
                            btnPlayPause.setVisibility(View.GONE);
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(object.getString("songURL").toString());
                            mediaPlayer.prepareAsync();
                            songArtisttv.setText(object.getString("artist"));
                            songTitletv.setText(object.getString("title"));
                            songArtisttv.setVisibility(View.VISIBLE);
                            songTitletv.setVisibility(View.VISIBLE);
                            positionV = String.valueOf(newval);
                        }catch (Exception e){

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onErrorResponse: "+error.getMessage());
            }
        });
        queue.add(request);
    }

    final Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateseekBar();
            long currentDuartion = mediaPlayer.getCurrentPosition();
            songduration.setText(milliSecondsToTimer(currentDuartion));

        }
    };
    private void updateseekBar(){
        if (mediaPlayer.isPlaying()){
            playerSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration())*100));
            handler.postDelayed(updater, 1000);

        }
    }
    private String milliSecondsToTimer(long milliSeconds){
        String timerString = "";
        String secondsString;
        int hours = (int)(milliSeconds/(1000*60*60));
        int minutes = (int)(milliSeconds %(1000*60*60))/(1000*60);
        int seconds = (int)((milliSeconds %(1000*60*60)) % (1000*60) / 1000);

        if(hours>0){
            timerString = hours + ":";
        }
        if (seconds<10){
            secondsString = "0"+ seconds;
        }else {
            secondsString =""+seconds;
        }
        timerString = timerString +minutes + ":"+secondsString;
        return timerString;
    }
    private void extractMusics() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                musicList = new ArrayList<>();
                musicList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject musicObject = response.getJSONObject(i);
                        Music music = new Music();
                        music.setId(musicObject.getString("id").toString());
                        music.setTitle(musicObject.getString("title").toString());
                        music.setArtist(musicObject.getString("artist"));
                        music.setGenre(musicObject.getString("genre").toString());
                        music.setCoverUrl(musicObject.getString("coverURL").toString());
                        music.setSongUrl(musicObject.getString("songURL").toString());
                        Log.d("tag", "onResponse: for loop:"+musicObject.getString("coverURL").toString());
                        musicList.add(music);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("tag", "ontry case error: "+e.getMessage());
                    }
                }
                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2,GridLayoutManager.HORIZONTAL, false));
                musicsAdapter =new MusicsAdapter(musicList, MainActivity.this , MainActivity.this );
                recyclerView.setAdapter(musicsAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: "+error.getMessage());
            }
        });
        queue.add(jsonArrayRequest);

    }


    @Override
    public void onItemClick(String id, String songURL, String songTitle, String songArtist) {
        loadingProgress.setVisibility(View.VISIBLE);
        btnPlayPause.setVisibility(View.GONE);
        mediaPlayer.reset();
        positionV = id;
        Log.d("TAG", "onItemClick: "+positionV);
        try {
            mediaPlayer.setDataSource(songURL);
            mediaPlayer.prepareAsync();
            songArtisttv.setText(songArtist);
            songTitletv.setText(songTitle);
            songArtisttv.setVisibility(View.VISIBLE);
            songTitletv.setVisibility(View.VISIBLE);
            playerLay.setVisibility(View.VISIBLE);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
