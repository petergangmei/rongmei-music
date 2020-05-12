package com.petergangmei.rongmeimusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.petergangmei.rongmeimusic.model.User;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "myTag";
    private static  final String  noUserRecord = "There is no user record corresponding to this identifier. The user may have been deleted.";
    private static  final String  incorrectPassword = "The password is invalid or the user does not have a password.";
    DatabaseReference ref;
    Handler handler;
    private FirebaseAuth firebaseAuth;
    //login
    LinearLayout loginLay;
    private EditText emailField, passwordField,nameField;
    private Button btnContinue;
    ProgressDialog progressDialog;
    private TextView errorMsg;
    Toolbar toolBar;

    //intro vidoe
    RelativeLayout introVideoLay;
    Button continueApp;
    ImageView musicNote;
    private VideoView videoView;
    String video_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firebaseAuth = FirebaseAuth.getInstance();

        ref = FirebaseDatabase.getInstance().getReference();

        //videoView
        videoView = findViewById(R.id.VideoView);
        introVideoLay = findViewById(R.id.introVideoLay);
        continueApp = findViewById(R.id.continueApp);
        musicNote = findViewById(R.id.musicNote);
        showIntroVideo();


        //logininitialize
        toolBar = findViewById(R.id.tool);
        loginLay = findViewById(R.id.loginLay);
        btnContinue = findViewById(R.id.btnContinue);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        nameField = findViewById(R.id.nameField);
        errorMsg = findViewById(R.id.errorMsg);
        anonymousAuthentication();
    }

    public void enableSound(int sound, MediaPlayer mp){
        Float f = Float.valueOf(sound);
        Log.e("checkingsounds","&&&&&   "+f);
        mp.setVolume(f,f);
        mp.setLooping(true);
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); //Max Volume 15
        audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);  //this will return current volume.

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sound, AudioManager.FLAG_PLAY_SOUND);   //here you can set custom volume.
    }
    private void showIntroVideo() {
         video_url = "android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.rm_intro_vid;
        Uri videoUri = Uri.parse(video_url);
        MediaController mediaController= new MediaController(getApplicationContext());
        mediaController.setAnchorView(videoView);
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                Float f = Float.valueOf(8);
                Log.e("checkingsounds","&&&&&   "+f);
                mp.setVolume(f,f);
                mp.setLooping(false);
                AudioManager audioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); //Max Volume 15
                audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);  //this will return current volume.
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 8, AudioManager.FLAG_PLAY_SOUND);

            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.pause();
                continueApp.setVisibility(View.VISIBLE);
                musicNote.setVisibility(View.VISIBLE);

            }
        });
        continueApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.stopPlayback();
                introVideoLay.setVisibility(View.GONE);
                continueApp.setVisibility(View.GONE);
                loginLay.setVisibility(View.VISIBLE);
                toolBar.setVisibility(View.VISIBLE);
            }
        });
    }


    private void anonymousAuthentication() {
        //timeanddate
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        String currentTime =currentTimeFormat.format(calForTime.getTime());
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
        String currentDate = currentDateFormat.format(calForDate.getTime());
        final String currentTimeNdate = currentTime +"--"+ currentDate;
        if (FirebaseAuth.getInstance().getCurrentUser()==null){

            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnContinue.getTag().toString().equals("login")){
                        if (!emailField.getText().toString().isEmpty()){
                            emailField.setVisibility(View.GONE);
                            passwordField.setVisibility(View.VISIBLE);
                            passwordField.requestFocus();
                            if (!passwordField.getText().toString().isEmpty()){
                                if (passwordField.getText().length()>5){
                                    Log.d(TAG, "anonymousAuthentication: email and password: "+emailField.getText().toString() + passwordField.getText().toString());
                                    progressDialog = new ProgressDialog(LoginActivity.this);
                                    progressDialog.setMessage("Progress..");
                                    progressDialog.show();
                                    firebaseAuth.signInWithEmailAndPassword(emailField.getText().toString(), passwordField.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                @Override
                                                public void onSuccess(AuthResult authResult) {
                                                    Log.d(TAG, "onSuccess: "+authResult.getUser());
                                                    progressDialog.dismiss();
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull final Exception e) {

                                            Log.d(TAG, "onFailure: "+e.getLocalizedMessage());
                                            if (e.getMessage().equals(noUserRecord)){
                                                progressDialog.dismiss();
                                                firebaseAuth.createUserWithEmailAndPassword(emailField.getText().toString()
                                                        ,passwordField.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                    @Override
                                                    public void onSuccess(AuthResult authResult) {
                                                        progressDialog.dismiss();
                                                        emailField.setVisibility(View.GONE);
                                                        passwordField.setVisibility(View.GONE);
                                                        nameField.setVisibility(View.VISIBLE);
                                                        nameField.requestFocus();
                                                        btnContinue.setTag("updateProfile");

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        Log.d(TAG, "onFailure: "+e.getMessage());
                                                    }
                                                });
                                            }else if (e.getMessage().equals(incorrectPassword)){
                                                progressDialog.dismiss();
                                                errorMsg.setText("Invalid PIN!");
                                                errorMsg.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });


                                }else {
                                    Toast.makeText(LoginActivity.this, "PIN should be 6 Digit", Toast.LENGTH_SHORT).show();
                                }

                            }else {
                                Toast.makeText(getApplicationContext(), "Enter your PIN", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "Enter your email.", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        if (!nameField.getText().toString().isEmpty()){
                            Log.d(TAG, "onClick: start updating profile"+nameField.getText().toString());
                            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            User user = new User(userid,nameField.getText().toString(),emailField.getText().toString(),currentTimeNdate,0,0,System.currentTimeMillis());
                            ref.child("Users").child(userid).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(LoginActivity.this, "Profile create!", Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    i.putExtra("from", "loginActivity");
                                    startActivity(i);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: "+e.getMessage());
                                }
                            });
                        }else {
                            Toast.makeText(LoginActivity.this, "Enter your name", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });

        }else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            },1500);
        }
    }
}
