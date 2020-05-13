package com.petergangmei.rongmeimusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.StaticLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petergangmei.rongmeimusic.adapter.MusicsAdapter;
import com.petergangmei.rongmeimusic.adapter.MusicsPlayListAdapter;
import com.petergangmei.rongmeimusic.model.Music;
import com.petergangmei.rongmeimusic.model.User;
import com.petergangmei.rongmeimusic.notification.CreateNotification;
import com.petergangmei.rongmeimusic.services.NotificationActionServices;
import com.petergangmei.rongmeimusic.services.OnClearFromRecentService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickInterface{
    private static final String TAG = "myTAG";
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;

    private String playListToShow = "TopSongsPlayLIST";
    private String userNameV, userEmailV, musicListentCountV;

    private boolean FreshStart =true;

     int totalSongsV;
     int totalSongsVG;
    int totalSearchResultV;

    Bitmap bitmap;
    int callsplash = 0;
     Dialog dialongshare;
    private static String URL = "http://rongmeimusic.pythonanywhere.com/api/";
    RecyclerView recyclerView,recyclerViewGospel,recyclerViewPlayList,recyclerViewGospelPlayList;
    List<Music> musicList, gospelplayList, searchResultList;

    MusicsAdapter musicsAdapter;
    MusicsPlayListAdapter musicsPlayListAdapter;


    //toolbar section
    private ImageView musicSearch,btnBackSL;
    private LinearLayout logoLay;
    private RelativeLayout searchLay;
    private EditText searchField;
    private ProgressBar searchingPB;

    private Toolbar tool;
    private ImageView btnPlayPause, btnSkipPrevious, btnSkipNext, coverImageMini, btnPlayPauseMini,showPlayListBtn;
    private TextView songduration, totalsongduration, songTitletv, songArtisttv;
    private MediaPlayer mediaPlayer;
    private Handler  handler = new Handler();
    private SeekBar playerSeekBar;
    private ProgressBar loadingProgress,playProgressing,playProgressingMini;
    private TextView loadingText,songTitleMini,songArtistMini;
    private LinearLayout playerLayMini,playerLay,loadingLay,topSongsLay;

    //playerlayplaylist
    private RelativeLayout playerLayPlayList, customTool;
    private ImageView coverImageExtended,imageCloud,btnUserDialog;

    //user profile section
    TextView musiclistenCount, userName;

    AdView adView;
    NotificationManager notificationManager;

    //Dialog
    private Dialog customUserDialog;
    private Dialog searchDialog;

    //Strings
    String fromIntent = "null";

    private int  positionV, positionVG;
    BroadcastReceiver broadcastReceiver;
    NotificationActionServices notificationActionServices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();

        //toolbar section
        musicSearch = findViewById(R.id.musicSearch);
        logoLay = findViewById(R.id.logoLay);
        searchLay = findViewById(R.id.searchLay);
        searchField= (EditText) findViewById(R.id.searhField);
        searchingPB = findViewById(R.id.searchingPB);

        //playerlayPlaylist
        playerLayPlayList = findViewById(R.id.playerLayPlayList);
        customTool = findViewById(R.id.customTool);
        coverImageExtended = findViewById(R.id.coverImageExtended);
        showPlayListBtn = findViewById(R.id.showPlayListBtn);
        btnBackSL = findViewById(R.id.btnBackSL);

        tool = findViewById(R.id.tool);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewGospel = findViewById(R.id.recyclerViewGospel);
        recyclerViewPlayList = findViewById(R.id.recyclerViewPlayList);
        recyclerViewGospelPlayList = findViewById(R.id.recyclerViewGospelPlayList);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnSkipPrevious = findViewById(R.id.btnSkipPrevious);
        btnSkipNext = findViewById(R.id.btnSkipNext);
        songduration = findViewById(R.id.songduration);
        totalsongduration = findViewById(R.id.totalsongduration);
        playerSeekBar = findViewById(R.id.playerSeekBar);
        loadingProgress = findViewById(R.id.loadingProgress);
        loadingText = findViewById(R.id.loadingText);
        songTitletv = findViewById(R.id.songTitle);
        songArtisttv = findViewById(R.id.songArtist);
        playProgressing = findViewById(R.id.playProgressing);

        btnUserDialog = findViewById(R.id.userDialog);

        //user profile section
        musiclistenCount = findViewById(R.id.musiclistenCount);
        userName = findViewById(R.id.userName);

        //playerslaymini
        playerLayMini = findViewById(R.id.playerLayMini);
        coverImageMini = findViewById(R.id.coverImageMini);
        songTitleMini = findViewById(R.id.songTitleMini);
        songArtistMini = findViewById(R.id.songArtistMini);
        btnPlayPauseMini = findViewById(R.id.btnPlayPauseMini);
        imageCloud = findViewById(R.id.imageCloud);
        playProgressingMini= findViewById(R.id.playProgressingMini);


        mediaPlayer = new MediaPlayer();
        playerSeekBar.setMax(100);
        //layouts
        playerLay = findViewById(R.id.playerLay);
        loadingLay = findViewById(R.id.loadingLay);
        topSongsLay = findViewById(R.id.topSongsLay);

        //admob banner ads
        admobSection();



        //broadcast receiver
        receiveBroadCase();

        //toobarsection
        musicSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSearchFilterSection();
            }
        });

        //show user profile dialog
        btnUserDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showuserProfileDialog();
            }
        });
        //one sone completed
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                GetNextSong();
            }
        });
        //player seekbar
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
        //play and pause song
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playANDpause();
            }
        });
        //play and pause mini
        btnPlayPauseMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playANDpause();
            }
        });
        //btn previous a
        btnSkipPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetPreviousSong();
            }
        });
        // bnt next
        btnSkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetNextSong();
            }
        });
    }


    private void songSearchFilterSection() {

        if (musicSearch.getTag().toString().equals("showSL")){
            logoLay.setVisibility(View.GONE);
            searchLay.setVisibility(View.VISIBLE);
            musicSearch.setImageResource(R.drawable.ic_music_search_black);
            musicSearch.setTag("search");
            searchField.requestFocus();
            showSoftKeyBoard(searchField);
        }else if (musicSearch.getTag().toString().equals("search")){
            filterSearchKey();
        }
        searchField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    filterSearchKey();
                    return true;
                }
                return false;
            }
        });

        //close search lay
        btnBackSL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchField.getText().length()== 0){
                    logoLay.setVisibility(View.VISIBLE);
                    searchLay.setVisibility(View.GONE);
                    musicSearch.setImageResource(R.drawable.ic_music_search);
                    musicSearch.setTag("showSL");
                    hideSoftKeyBoard();
                }else {
                    searchField.setText("");
                }
            }
        });
    }


    private void filterSearchKey() {
        musicSearch.setVisibility(View.GONE);
        searchingPB.setVisibility(View.VISIBLE);

        //query song
        //topgospel songs
        String searchURL = "http://rongmeimusic.pythonanywhere.com/api/filter/"+searchField.getText().toString();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonArrayRequest jArrayRequest = new JsonArrayRequest(Request.Method.GET, searchURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                searchResultList = new ArrayList<>();
                searchResultList.clear();
                totalSearchResultV = response.length();
                for (int i=0; i <response.length(); i++){
                    try {
                        JSONObject musicObject = response.getJSONObject(i);
                        Music music = new Music();
                        music.setId(i);
                        music.setSongId(musicObject.getString("id").toString());
                        music.setTitle(musicObject.getString("title").toString());
                        music.setArtist(musicObject.getString("artist"));
                        music.setGenre(musicObject.getString("genre").toString());
                        music.setCoverUrl(musicObject.getString("coverURL").toString());
                        music.setSongUrl(musicObject.getString("songURL").toString());
//                        Log.d("tag", ": for loop:"+musicObject.getString("coverURL").toString());
                        searchResultList.add(music);
                    }catch (Exception e){

                    }
                }
                showSearchResult();


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });
        rq.add(jArrayRequest);


    }

    private void showSearchResult() {
        searchingPB.setVisibility(View.GONE);
        musicSearch.setVisibility(View.VISIBLE);
        searchDialog = new Dialog(MainActivity.this);
        searchDialog.setContentView(R.layout.dialog_search_contain);
        searchDialog.show();
        ImageView btnBack;
        RecyclerView recyclerViewResult;
        TextView resultCout;
        btnBack = searchDialog.findViewById(R.id.btnBack);
        recyclerViewResult = searchDialog.findViewById(R.id.recyclerViewSearchResult);
        resultCout = searchDialog.findViewById(R.id.result);
        resultCout.setText(totalSearchResultV+" Result found.");

//        close search dialog
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog.dismiss();
                searchingPB.setVisibility(View.GONE);
                musicSearch.setVisibility(View.VISIBLE);
            }
        });
        //playlist
        recyclerViewResult.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        musicsPlayListAdapter = new MusicsPlayListAdapter(searchResultList, MainActivity.this, MainActivity.this);
        recyclerViewResult.setAdapter(musicsPlayListAdapter);
    }

    //show soft keyboard
    private void showSoftKeyBoard(EditText searhField) {
        searhField.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searhField, InputMethodManager.SHOW_IMPLICIT);
    }
    //hide soft keyboard
    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
    }

    private void showuserProfileDialog() {
        customUserDialog = new Dialog(MainActivity.this,android.R.style.Theme_Light);
        customUserDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customUserDialog.setContentView(R.layout.dialog_custom);
        customUserDialog.show();

        TextView userName, musiclistenCount, userEmail, appVerson;
        ImageView btnBack = customUserDialog.findViewById(R.id.btnBack);
        userName = customUserDialog.findViewById(R.id.userName);
        musiclistenCount = customUserDialog.findViewById(R.id.musiclistenCount);
        userEmail = customUserDialog.findViewById(R.id.userEmail);

        appVerson = customUserDialog.findViewById(R.id.appVerson);
        userName.setText(userNameV);
        userEmail.setText(userEmailV);
        appVerson.setText("Version: "+BuildConfig.VERSION_NAME);
        musiclistenCount.setText(musicListentCountV+" songs lisented");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customUserDialog.dismiss();
            }
        });
    }

    //check user authenitcation
    private void anonymousAuthenticationNuserInfo() {

        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }else {
            if (callsplash ==0){
                callSplashDialog();
                callsplash = 1;
            }

            createChannel();
            extractMusics();
            playerLayMiniContrusction();
            //getUserProfileDetail
            ref.child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        User user = dataSnapshot.getValue(User.class);
                        userNameV = user.getName();
                        userEmailV = user.getEmail();
                        musicListentCountV = ""+user.getTotalViews();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    private void admobSection() {
        //admob initialize
        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
//        realid ca-app-pub-8387213182374507/5448885887
        //trialid ca-app-pub-3940256099942544/6300978111
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void playerLayMiniContrusction() {
        //showplaylist
        showPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+showPlayListBtn.getTag().toString()+" showplaylist: "+playListToShow);
              if (showPlayListBtn.getTag().toString().equals("show")){
                  if (playListToShow.equals("TopSongsPlayLIST")){
                      recyclerViewPlayList.setVisibility(View.VISIBLE);
                  }else if (playListToShow.equals("GosplayPlayLIST")){
                      recyclerViewGospelPlayList.setVisibility(View.VISIBLE);
                  }
                  showPlayListBtn.setTag("hide");
                  imageCloud.setVisibility(View.INVISIBLE);
              }else {
                  if (playListToShow.equals("TopSongsPlayLIST")){
                      recyclerViewPlayList.setVisibility(View.GONE);
                  }else if (playListToShow.equals("GosplayPlayLIST")){
                      recyclerViewGospelPlayList.setVisibility(View.GONE);
                  }
                  imageCloud.setVisibility(View.VISIBLE);
                  showPlayListBtn.setTag("show");
              }
            }
        });
        //collepes mediaplayer
        customTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerLayMini.setVisibility(View.VISIBLE);
                topSongsLay.setVisibility(View.VISIBLE);
                tool.setVisibility(View.VISIBLE);
                playerLayPlayList.setVisibility(View.GONE);
                playerLay.setVisibility(View.GONE);
            }
        });
        //onExpanding mediaplayer
        playerLayMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerLayMini.setVisibility(View.GONE);
                topSongsLay.setVisibility(View.GONE);
                tool.setVisibility(View.GONE);
                playerLayPlayList.setVisibility(View.VISIBLE);
                playerLay.setVisibility(View.VISIBLE);
            }
        });
    }

    private void playANDpause() {
        if (mediaPlayer.isPlaying()){
            handler.removeCallbacks(updater);
            mediaPlayer.pause();
            btnPlayPause.setImageResource(R.drawable.ic_play);
            btnPlayPauseMini.setImageResource(R.drawable.ic_play);
            if (FreshStart){
            CreateNotification.createNotification(MainActivity.this, musicList.get(positionV), R.drawable.ic_play, 1,musicList.size());
                //setfreshstart value
                FreshStart = false;
            }else {
                if (positionV==0){
                    int positionVV =0;
                    CreateNotification.createNotification(MainActivity.this, musicList.get(positionVV), R.drawable.ic_play, 1,musicList.size());
                }else {
                    int positionVV = positionV;
                    CreateNotification.createNotification(MainActivity.this, musicList.get(positionVV), R.drawable.ic_play, 1,musicList.size());

                }
            }
        }else {
            if (FreshStart){
            CreateNotification.createNotification(MainActivity.this, musicList.get(positionV), R.drawable.ic_pause, 1,musicList.size());
                //setfreshstart value
                FreshStart = false;
            }else {
                if (positionV==0){
                    int positionVV = 0;
                    CreateNotification.createNotification(MainActivity.this, musicList.get(positionVV), R.drawable.ic_pause, 1,musicList.size());
                }else {
                    int positionVV = positionV;
                    CreateNotification.createNotification(MainActivity.this, musicList.get(positionVV), R.drawable.ic_pause, 1,musicList.size());
                }
            }
            mediaPlayer.start();
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            btnPlayPauseMini.setImageResource(R.drawable.ic_pause);
            updateseekBar();
        }
    }

    //receive broatcast
    private void receiveBroadCase() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getExtras().getString("actionname")){
                    case "actionprevious":
                        GetPreviousSong();
                        break;
                    case "actionplay":
                        playANDpause();
                        break;
                    case "actionnext":
                        GetNextSong();
                        break;
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
    };

    private void createChannel(){
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                    "KOD Dev", NotificationManager.IMPORTANCE_LOW);
            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

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
               dialog.dismiss();
           }
       },2500);
    }

    //check music playser prepared
    private void checkMusicPrepare(final int id) {
        //onprepared song
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                updateseekBar();
                //setfreshstart value
                positionV = id;
                positionVG =  id;
                FreshStart = false;
                updateSongsTotalViews(id);


                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnPlayPause.setImageResource(R.drawable.ic_pause);
                        btnPlayPauseMini.setImageResource(R.drawable.ic_pause);
                        totalsongduration.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
                        playProgressing.setVisibility(View.GONE);
                        btnPlayPause.setVisibility(View.VISIBLE);
                        playProgressingMini.setVisibility(View.GONE);
                        btnPlayPauseMini.setVisibility(View.VISIBLE);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() { updatesongListenCount(); }
                        },4000);

                    }
                },1000);
                //showshare button in 5 second
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playerSeekBar.setEnabled(true);
                        CreateNotification.createNotification(MainActivity.this, musicList.get(positionV), R.drawable.ic_pause, 1,musicList.size());
                    }
                },3000);
            }
        });
    }

    private void updateSongsTotalViews(int id) {
        if (playListToShow.equals("TopSongsPlayLIST")){
            String  upsongURL = "http://rongmeimusic.pythonanywhere.com/api/"+musicList.get(id).getSongId()+"/update";

            Log.d(TAG, "update URL"+upsongURL);
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, upsongURL, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("tag", "onErrorResponse: "+error.getMessage());
                }
            });
            queue.add(jsonArrayRequest);
        }else if (playListToShow.equals("GosplayPlayLIST")){
            String  upsongURL = "http://rongmeimusic.pythonanywhere.com/api/"+gospelplayList.get(id).getSongId()+"/update";
            Log.d(TAG, "update URL"+upsongURL);
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, upsongURL, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("tag", "onErrorResponse: "+error.getMessage());
                }
            });
            queue.add(jsonArrayRequest);
        }


    }

    private void updatesongListenCount() {
        ref.child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);

                    long totalsongView = user.getTotalViews() +1;
                    long currentView = user.getViews() +1;

                    HashMap<String, Object > hashMap = new HashMap<>();
                    hashMap.put("views", currentView);
                    hashMap.put("totalViews", totalsongView);
                    ref.child("Users").child(firebaseUser.getUid()).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    anonymousAuthenticationNuserInfo();
                                }
                            });

                    if (currentView >50){
                        shareapptoUnlockMoresongs();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void shareapptoUnlockMoresongs() {
        //pasue song in 5 second
        mediaPlayer.pause();
        btnPlayPause.setImageResource(R.drawable.ic_play);
        btnPlayPauseMini.setImageResource(R.drawable.ic_play);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FreshStart){
                    CreateNotification.createNotification(MainActivity.this, musicList.get(positionV), R.drawable.ic_play, 1,musicList.size());
                    //setfreshstart value
                    FreshStart = false;
                }else {
                    if (positionV==0){
                        int positionVV =0;
                        CreateNotification.createNotification(MainActivity.this, musicList.get(positionVV), R.drawable.ic_play, 1,musicList.size());
                    }else {
                        int positionVV = positionV;
                        CreateNotification.createNotification(MainActivity.this, musicList.get(positionVV), R.drawable.ic_play, 1,musicList.size());

                    }
                }
            }
        },2000);

        //show share dialog
        dialongshare = new Dialog(MainActivity.this);
        dialongshare = new Dialog(MainActivity.this);
        dialongshare.setContentView(R.layout.dialog_share);
        dialongshare.setCancelable(false);
        dialongshare.show();
        final ImageView imageView = dialongshare.findViewById(R.id.sharelogo);
        Button sharewithOther = dialongshare.findViewById(R.id.sharewithOther);
        sharewithOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Drawable drawable = imageView.getDrawable();
                bitmap = ((BitmapDrawable)drawable).getBitmap();
                String bitmapath = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap, "Share image via", "From music.rongmei.co");

                Uri uri = Uri.parse(bitmapath);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpeg");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Stream latest Ronglu with Rongmei music. Download for free " +
                        "https://bit.ly/2YSe1pB (android)");
                startActivity(Intent.createChooser(shareIntent, "Share using"));

                //hid shar dialog after 8 seconds
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialongshare.dismiss();
                        //reset the views
                        HashMap<String ,Object> hashMap = new HashMap<>();
                        hashMap.put("views", 0);
                        ref.child("Users").child(firebaseUser.getUid()).updateChildren(hashMap);
                    }
                },8000);

            }
        });
    }
    //getnext song
    private void GetNextSong() {
        int dv = positionV;
        int dvg = positionVG;
        final int positionVV = dv+1;
        final int positionVVG = dvg+1;
        //getnext song
        try {
            if (playListToShow.equals("TopSongsPlayLIST")){
                if (totalSongsV!=positionVV){
                    CreateNotification.createNotification(MainActivity.this, musicList.get(positionV+1), R.drawable.ic_pause, 1,musicList.size());
                    mediaPlayer.reset();
                songduration.setText(milliSecondsToTimer(0));
                updateseekBar();
                playerSeekBar.setEnabled(false);
                playProgressing.setVisibility(View.VISIBLE);
                btnPlayPause.setVisibility(View.GONE);
                playProgressingMini.setVisibility(View.VISIBLE);
                btnPlayPauseMini.setVisibility(View.GONE);
                String thisSongURL = null;

                thisSongURL = musicList.get(positionVV).getSongUrl();
                Log.d(TAG, ": next song "+positionVV + " newval "+positionVV + " playlist:" +playListToShow + "url:"+musicList.get(positionVV).getSongUrl());
                mediaPlayer.setDataSource(thisSongURL);
                mediaPlayer.prepareAsync();

                songArtisttv.setText(musicList.get(positionVV).getArtist());
                songTitletv.setText(musicList.get(positionVV).getTitle());
                    songTitleMini.setText(musicList.get(positionVV).getTitle());
                    songArtistMini.setText(musicList.get(positionVV).getArtist());
                if (musicList.get(positionV).getCoverUrl().equals("null")){
                    Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(coverImageMini);
                    Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(coverImageExtended);
                    Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(imageCloud);
                }else {
                    Glide.with(this).load(musicList.get(positionVV).getCoverUrl()).into(coverImageMini);
                    Glide.with(this).load(musicList.get(positionVV).getCoverUrl()).into(coverImageExtended);
                    Glide.with(this).load(musicList.get(positionVV).getCoverUrl()).into(imageCloud);
                }
                songArtisttv.setVisibility(View.VISIBLE);
                songTitletv.setVisibility(View.VISIBLE);
                positionV =positionVV;
                checkMusicPrepare(positionV);

                }else {
                    Toast.makeText(this, "No song in queue..", Toast.LENGTH_SHORT).show();
                }

            }else if (playListToShow.equals("GosplayPlayLIST")){
                if (totalSongsVG!=positionVVG){
                    CreateNotification.createNotification(MainActivity.this, musicList.get(positionV+1), R.drawable.ic_pause, 1,musicList.size());
                mediaPlayer.reset();
                songduration.setText(milliSecondsToTimer(0));
                updateseekBar();
                playerSeekBar.setEnabled(false);
                playProgressing.setVisibility(View.VISIBLE);
                btnPlayPause.setVisibility(View.GONE);
                playProgressingMini.setVisibility(View.VISIBLE);
                btnPlayPauseMini.setVisibility(View.GONE);
                String thisSongURL = null;

                Log.d(TAG, ": next song "+positionVV + " newval "+positionVV + " playlist:" +playListToShow + "url:"+gospelplayList.get(positionVV).getSongUrl());
                thisSongURL = gospelplayList.get(positionVV).getSongUrl();
                mediaPlayer.setDataSource(thisSongURL);
                mediaPlayer.prepareAsync();

                songArtisttv.setText(gospelplayList.get(positionVV).getArtist());
                songTitletv.setText(gospelplayList.get(positionVV).getTitle());
                    songTitleMini.setText(gospelplayList.get(positionVV).getTitle());
                    songArtistMini.setText(gospelplayList.get(positionVV).getArtist());
                if (gospelplayList.get(positionV).getCoverUrl().equals("null")){
                    Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(coverImageMini);
                    Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(coverImageExtended);
                    Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(imageCloud);
                }else {
                    Glide.with(this).load(gospelplayList.get(positionVV).getCoverUrl()).into(coverImageMini);
                    Glide.with(this).load(gospelplayList.get(positionVV).getCoverUrl()).into(coverImageExtended);
                    Glide.with(this).load(gospelplayList.get(positionVV).getCoverUrl()).into(imageCloud);
                }
                songArtisttv.setVisibility(View.VISIBLE);
                songTitletv.setVisibility(View.VISIBLE);
                positionV =positionVV;
                checkMusicPrepare(positionV);
                }else {
                    Toast.makeText(this, "No song in queue..", Toast.LENGTH_SHORT).show();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void GetPreviousSong() {
        int dv = positionV;
        int dvg = positionVG;
        final int positionVV = dv-1;
        final int positionVVG = dvg-1;

        //getnext song
        try {
            if (playListToShow.equals("TopSongsPlayLIST")){
                if (positionVV!=-1){
                    CreateNotification.createNotification(MainActivity.this, musicList.get(positionV-1), R.drawable.ic_pause, 1,musicList.size());
                    mediaPlayer.reset();
                    songduration.setText(milliSecondsToTimer(0));
                    updateseekBar();
                    playerSeekBar.setEnabled(false);
                    playProgressing.setVisibility(View.VISIBLE);
                    btnPlayPause.setVisibility(View.GONE);
                    playProgressingMini.setVisibility(View.VISIBLE);
                    btnPlayPauseMini.setVisibility(View.GONE);
                    String thisSongURL = null;

                    thisSongURL = musicList.get(positionVV).getSongUrl();
                    Log.d(TAG, ": next song "+positionVV + " newval "+positionVV + " playlist:" +playListToShow + "url:"+musicList.get(positionVV).getSongUrl());
                    mediaPlayer.setDataSource(thisSongURL);
                    mediaPlayer.prepareAsync();

                    songArtisttv.setText(musicList.get(positionVV).getArtist());
                    songTitletv.setText(musicList.get(positionVV).getTitle());
                    songTitleMini.setText(musicList.get(positionVV).getTitle());
                    songArtistMini.setText(musicList.get(positionVV).getArtist());
                    if (musicList.get(positionV).getCoverUrl().equals("null")){
                        Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(coverImageMini);
                        Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(coverImageExtended);
                        Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(imageCloud);
                    }else {
                        Glide.with(this).load(musicList.get(positionVV).getCoverUrl()).into(coverImageMini);
                        Glide.with(this).load(musicList.get(positionVV).getCoverUrl()).into(coverImageExtended);
                        Glide.with(this).load(musicList.get(positionVV).getCoverUrl()).into(imageCloud);
                    }
                    songArtisttv.setVisibility(View.VISIBLE);
                    songTitletv.setVisibility(View.VISIBLE);
                    positionV =positionVV;
                    checkMusicPrepare(positionV);

                }else {
                    Toast.makeText(this, "No song in queue..", Toast.LENGTH_SHORT).show();
                }

            }else if (playListToShow.equals("GosplayPlayLIST")){
                if (positionVVG!=-1){
                    CreateNotification.createNotification(MainActivity.this, musicList.get(positionV-1), R.drawable.ic_pause, 1,musicList.size());
                    mediaPlayer.reset();
                    songduration.setText(milliSecondsToTimer(0));
                    updateseekBar();
                    playerSeekBar.setEnabled(false);
                    playProgressing.setVisibility(View.VISIBLE);
                    btnPlayPause.setVisibility(View.GONE);
                    playProgressingMini.setVisibility(View.VISIBLE);
                    btnPlayPauseMini.setVisibility(View.GONE);
                    String thisSongURL = null;

                    Log.d(TAG, ": next song "+positionVV + " newval "+positionVV + " playlist:" +playListToShow + "url:"+gospelplayList.get(positionVV).getSongUrl());
                    thisSongURL = gospelplayList.get(positionVV).getSongUrl();
                    mediaPlayer.setDataSource(thisSongURL);
                    mediaPlayer.prepareAsync();

                    songArtisttv.setText(gospelplayList.get(positionVV).getArtist());
                    songTitletv.setText(gospelplayList.get(positionVV).getTitle());
                    songTitleMini.setText(gospelplayList.get(positionVV).getTitle());
                    songArtistMini.setText(gospelplayList.get(positionVV).getArtist());
                    if (gospelplayList.get(positionV).getCoverUrl().equals("null")){
                        Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(coverImageMini);
                        Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(coverImageExtended);
                        Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(imageCloud);
                    }else {
                        Glide.with(this).load(gospelplayList.get(positionVV).getCoverUrl()).into(coverImageMini);
                        Glide.with(this).load(gospelplayList.get(positionVV).getCoverUrl()).into(coverImageExtended);
                        Glide.with(this).load(gospelplayList.get(positionVV).getCoverUrl()).into(imageCloud);
                    }
                    songArtisttv.setVisibility(View.VISIBLE);
                    songTitletv.setVisibility(View.VISIBLE);
                    positionV =positionVV;
                    checkMusicPrepare(positionV);
                }else {
                    Toast.makeText(this, "No song in queue..", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //extract songs
    private void extractMusics() {
        //topgospel songs
        String gospelURL = "http://rongmeimusic.pythonanywhere.com/api/genre/Gospel";
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonArrayRequest jArrayRequest = new JsonArrayRequest(Request.Method.GET, gospelURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                gospelplayList = new ArrayList<>();
                gospelplayList.clear();
                totalSongsVG = response.length();
                for (int i=0; i <response.length(); i++){
                    try {
                        JSONObject musicObject = response.getJSONObject(i);
                        Music music = new Music();
                        music.setId(i);
                        music.setSongId(musicObject.getString("id").toString());
                        music.setTitle(musicObject.getString("title").toString());
                        music.setArtist(musicObject.getString("artist"));
                        music.setGenre(musicObject.getString("genre").toString());
                        music.setCoverUrl(musicObject.getString("coverURL").toString());
                        music.setSongUrl(musicObject.getString("songURL").toString());
//                        Log.d("tag", ": for loop:"+musicObject.getString("coverURL").toString());
                        gospelplayList.add(music);
                    }catch (Exception e){

                    }
                }
                //get Gospel
                recyclerViewGospel.setLayoutManager(new GridLayoutManager(MainActivity.this, 1,GridLayoutManager.HORIZONTAL, false));
                musicsAdapter =new MusicsAdapter(gospelplayList, MainActivity.this , MainActivity.this );
                recyclerViewGospel.setAdapter(musicsAdapter);
                //playlist
                recyclerViewGospelPlayList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                musicsPlayListAdapter = new MusicsPlayListAdapter(gospelplayList, MainActivity.this, MainActivity.this);
                recyclerViewGospelPlayList.setAdapter(musicsPlayListAdapter);


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });
        rq.add(jArrayRequest);

        //getTop songs
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                musicList = new ArrayList<>();
                musicList.clear();
                totalSongsV = response.length();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject musicObject = response.getJSONObject(i);
                        Music music = new Music();
                        music.setId(i);
                        music.setSongId(musicObject.getString("id").toString());
                        music.setTitle(musicObject.getString("title").toString());
                        music.setArtist(musicObject.getString("artist"));
                        music.setGenre(musicObject.getString("genre").toString());
                        music.setCoverUrl(musicObject.getString("coverURL").toString());
                        music.setSongUrl(musicObject.getString("songURL").toString());
//                        Log.d("tag", ": for loop:"+musicObject.getString("coverURL").toString());
                        musicList.add(music);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("tag", "ontry case error: "+e.getMessage());
                    }


                    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2,GridLayoutManager.HORIZONTAL, false));
                    musicsAdapter =new MusicsAdapter(musicList, MainActivity.this , MainActivity.this );
                    recyclerView.setAdapter(musicsAdapter);
                    //playlist
                    recyclerViewPlayList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    musicsPlayListAdapter = new MusicsPlayListAdapter(musicList, MainActivity.this, MainActivity.this);
                    recyclerViewPlayList.setAdapter(musicsPlayListAdapter);

                    loadingLay.setVisibility(View.GONE);
                    topSongsLay.setVisibility(View.VISIBLE);

                    if (FreshStart){
                        loadFirstMusic();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: "+error.getMessage());
            }
        });
        queue.add(jsonArrayRequest);



    }

    private void loadFirstMusic() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(musicList.get(positionV).getSongUrl());
            mediaPlayer.prepareAsync();
            songArtisttv.setText(musicList.get(positionV).getArtist());
            songTitletv.setText(musicList.get(positionV).getTitle());
            songTitleMini.setText(musicList.get(positionV).getTitle());
            songArtistMini.setText(musicList.get(positionV).getArtist());
            if (musicList.get(positionV).getCoverUrl().equals("null")){
                Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(coverImageMini);
                Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(coverImageExtended);
                Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(imageCloud);
            }else {
                Glide.with(this).load(musicList.get(positionV).getCoverUrl()).into(coverImageMini);
                Glide.with(this).load(musicList.get(positionV).getCoverUrl()).into(coverImageExtended);
                Glide.with(this).load(musicList.get(positionV).getCoverUrl()).into(imageCloud);
            }
            songArtisttv.setVisibility(View.VISIBLE);
            songTitletv.setVisibility(View.VISIBLE);
            playerLayMini.setVisibility(View.VISIBLE);
            //setfreshstart value to fale

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //seekbar
    private void updateseekBar(){

        if (mediaPlayer.isPlaying()){
            playerSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration())*100));
            handler.postDelayed(updater, 1000);
        }
    }
    Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateseekBar();
            long currentDuartion = mediaPlayer.getCurrentPosition();
            songduration.setText(milliSecondsToTimer(currentDuartion));
        }
    };
    //milliseconds to timmer
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

    @Override
    public void onItemClick(int position,String cplaylist, int id, String songURL, String songTitle, String songArtist) {

        hideSoftKeyBoard();
        if (cplaylist.equals("GosplayPlayLIST")){
            playListToShow  = cplaylist;
        }else if (cplaylist.equals("TopSongsPlayLIST")){
            playListToShow = cplaylist;
        }
        Log.d(TAG, "Playlist "+playListToShow);
        playProgressing.setVisibility(View.VISIBLE);
        btnPlayPause.setVisibility(View.GONE);
        mediaPlayer.reset();
        playerSeekBar.setEnabled(false);
        songduration.setText(milliSecondsToTimer(0));
        updateseekBar();
        playProgressingMini.setVisibility(View.VISIBLE);
        btnPlayPauseMini.setVisibility(View.GONE);
        adView.setVisibility(View.VISIBLE);
        CreateNotification.createNotification(MainActivity.this, musicList.get(position), R.drawable.ic_play,1,musicList.size()-1);
        try {
            mediaPlayer.setDataSource(songURL);
            mediaPlayer.prepareAsync();
            songArtisttv.setText(songArtist);
            songTitletv.setText(songTitle);
            songTitleMini.setText(songTitle);
            songArtistMini.setText(songArtist);
            if (musicList.get(position).getCoverUrl().equals("null")){
                Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(coverImageMini);
                Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(coverImageExtended);
                Glide.with(this).load(R.drawable.rongmei_music_logo1000).into(imageCloud);
            }else {
                Glide.with(this).load(musicList.get(position).getCoverUrl()).into(coverImageMini);
                Glide.with(this).load(musicList.get(position).getCoverUrl()).into(coverImageExtended);
                Glide.with(this).load(musicList.get(position).getCoverUrl()).into(imageCloud);
            }

            songArtisttv.setVisibility(View.VISIBLE);
            songTitletv.setVisibility(View.VISIBLE);
            playerLayMini.setVisibility(View.VISIBLE);
            checkMusicPrepare(id);

            Log.d(TAG, "songid:->"+positionV+ " newsong id->"+position + " song name-> "
            +musicList.get(position).getTitle()+ " url->"+musicList.get(position).getSongUrl()+" ul"+songURL);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onTrackActions(String action) {
    }


    @Override
    public void onBackPressed() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Wanna exit the app?");

        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            notificationManager.cancelAll();
                        }
                        btnPlayPause.setImageResource(R.drawable.ic_play);
                        btnPlayPauseMini.setImageResource(R.drawable.ic_play);
                        mediaPlayer.pause();
                        notificationManager.cancelAll();
                        unregisterReceiver(broadcastReceiver);
                        moveTaskToBack(true);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        anonymousAuthenticationNuserInfo();
        Log.d(TAG, "Playlist "+playListToShow);
    }

}
