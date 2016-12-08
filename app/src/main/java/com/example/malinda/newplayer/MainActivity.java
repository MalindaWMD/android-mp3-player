package com.example.malinda.newplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageButton btnList,btnPlay,btnStop,btnForward,btnPrev;
    private MediaPlayer mp;
    private TextView txtTotal,txtCurrent,txtTitle;
    private String MEDIA_PATH = "storage/extSdCard/Music/";
    private SeekBar soundSeek;
    private ImageView imgCover;
    int currentPosition = 0;
    int totalDuration = 0;
    int value  =0;
    boolean Playing = false;
    String audioFile = "";
    int SONG_NUMBER = 0;
    int CURRENT_NUMBER= 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnList = (ImageButton)findViewById(R.id.imbList);
        btnPlay = (ImageButton) findViewById(R.id.imageButton3);
        btnStop = (ImageButton) findViewById(R.id.imageButton4);
        btnForward = (ImageButton) findViewById(R.id.imageButton5);
        btnPrev = (ImageButton) findViewById(R.id.imageButton2);
        txtTitle = (TextView) findViewById(R.id.textView3);
        txtCurrent = (TextView) findViewById(R.id.textView2);
        txtTotal = (TextView) findViewById(R.id.textView);
        soundSeek = (SeekBar) findViewById(R.id.seekBar);
        imgCover = (ImageView) findViewById(R.id.imageView2);
        mp = new MediaPlayer();

        setListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 52){
            audioFile = data.getStringExtra(PlayList.AUDIO_PATH);
            SONG_NUMBER = PlayList.songNumber;
            playMedia(audioFile);
            getImage(audioFile);

        }
    }

    private void setListeners() {

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent listIntent = new Intent(v.getContext(),PlayList.class);
                        startActivityForResult(listIntent, 52);
                    }
                });
            }
        });

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(final MediaPlayer mp) {
                mp.start();
                mRunnable.run();
            }
        });

        soundSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mp.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                Playing = false;
                mHandler.removeCallbacks(mRunnable);

                currentPosition = 0;
                totalDuration = 0;
                txtTitle.setText("No media selected");
                txtTotal.setText(getFormatTime(0));
                txtCurrent.setText(getFormatTime(0));
                soundSeek.setProgress(0);

                btnPlay.setImageResource(R.drawable.play_selector);

                imgCover.setImageResource(R.drawable.default_cover); //any default cover
                imgCover.setAdjustViewBounds(true);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Playing){
                    Intent listIntent = new Intent(v.getContext(),PlayList.class);
                    startActivityForResult(listIntent, 52);
                }else{
                    if(value == 0){
                        mp.pause();
                        value = 1;
                        btnPlay.setImageResource(R.drawable.play_selector);
                    }else if(value == 1){
                        mp.start();
                        value = 0;
                        btnPlay.setImageResource(R.drawable.pause_selector);
                    }
                }
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Playing) {
                    Intent listIntent = new Intent(v.getContext(), PlayList.class);
                    startActivityForResult(listIntent, 52);
                }else{
                    if(SONG_NUMBER == (PlayList.fileNames.size()-1)){
                        playMedia(PlayList.fileDetails.get(PlayList.fileNames.get(0)));
                        CURRENT_NUMBER = 0;
                    }else{
                        playMedia(PlayList.fileDetails.get(PlayList.fileNames.get(SONG_NUMBER+1)));
                        CURRENT_NUMBER = SONG_NUMBER + 1;
                    }
                    getImage(PlayList.fileDetails.get(PlayList.fileNames.get(SONG_NUMBER+1)));
                    SONG_NUMBER = CURRENT_NUMBER;
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Playing) {
                    Intent listIntent = new Intent(v.getContext(), PlayList.class);
                    startActivityForResult(listIntent, 52);
                }else{
                    if(SONG_NUMBER == 0){
                        playMedia(PlayList.fileDetails.get(PlayList.fileNames.size() - 1));
                        CURRENT_NUMBER = PlayList.fileNames.size() - 1 ;
                    }else{
                        playMedia(PlayList.fileDetails.get(PlayList.fileNames.get(SONG_NUMBER - 1)));
                        CURRENT_NUMBER = SONG_NUMBER - 1;
                    }
                    getImage(PlayList.fileDetails.get(PlayList.fileNames.get(CURRENT_NUMBER)));
                    SONG_NUMBER = CURRENT_NUMBER;
                }

            }
        });

    }

    void setTitle(String directory){
        if(directory != null){
            File ftitle = new File(directory);
            txtTitle.setText(ftitle.getName().toString());
        }
    }

    public void playMedia(String path){

        if(path != null){
            try{
                setTitle(path);
                Uri myUri = Uri.parse(path); // initialize Uri here
                mp.stop();
                mp.reset();
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setDataSource(getApplicationContext(), myUri);
                mp.prepare();
                btnPlay.setImageResource(R.drawable.pause_selector);
                value = 0;
                Playing = true;
            }catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(mp != null) {
                currentPosition = 0;
                totalDuration = mp.getDuration();
                currentPosition = mp.getCurrentPosition();
                soundSeek.setMax(totalDuration);
                soundSeek.setProgress(currentPosition);
                txtCurrent.setText(getFormatTime(currentPosition));
                txtTotal.setText(getFormatTime(totalDuration));
            }
            mHandler.postDelayed(this, 10);
        }
    };

    private String getFormatTime(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf     .append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    public void getImage(String path){
        if(path != null){
            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            byte [] data = mmr.getEmbeddedPicture();

            if(data != null)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                imgCover.setImageBitmap(bitmap); 
                imgCover.setAdjustViewBounds(true);
            }else
            {
                imgCover.setImageResource(R.drawable.default_cover); //default cover
                imgCover.setAdjustViewBounds(true);
            }
        }
    }

}
