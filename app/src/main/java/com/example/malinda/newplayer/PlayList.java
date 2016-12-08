package com.example.malinda.newplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PlayList extends AppCompatActivity {

    private ListView playList;
    private ImageButton btnBack;
    public static  ArrayList<String> fileNames;
    String INTERNAL_PATH = Environment.getExternalStorageDirectory().getPath()+"/";
    String EXTERNAL_PATH = "storage/extSdCard/";
    static String AUDIO_PATH = "";
    static HashSet<String> myFiles = new HashSet<String>();
    static HashMap<String,String> fileDetails = new HashMap<String,String>();
    String[] DIRECTORIES = {INTERNAL_PATH,EXTERNAL_PATH};
    public static int songNumber ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        playList = (ListView)findViewById(R.id.listView);
        btnBack = (ImageButton)findViewById(R.id.imageButton);

        fileNames = new ArrayList<String>(getFiles());
        playList.setAdapter(new ArrayAdapter<String>(this, R.layout.song_items, fileNames));

        setListeners();

    }

    void setListeners(){

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        playList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getApplicationContext(), getFilePath(fileNames.get(position).toString()), Toast.LENGTH_LONG).show();
                openPlayer(getFilePath(fileNames.get(position).toString()));
                songNumber = position;
            }
        });

    }


    public HashSet<String> getFiles(){

        for(int d = 0 ; d < DIRECTORIES.length ; d++){

            File f = new File(DIRECTORIES[d]);
            f.mkdirs();
            File[] files = f.listFiles();

            if(files != null && files.length > 0){
                for(File file : files){
                    if(file.isDirectory()){
                        scanDirectory(file);
                    }else{
                        addSong(file);
                    }
                }
            }else{ return  myFiles;}
        }

        return myFiles;

    }

    public void scanDirectory(File directory){
        if(directory != null){
            File[] listFiles = directory.listFiles();
            for (File file:listFiles){
                if(file.isDirectory()){
                    scanDirectory(file);
                }else{
                    addSong(file);
                }
            }

        }
    }

    public void addSong(File song){
        if(song.getName().endsWith(".mp3")){
            myFiles.add(song.getName());
            fileDetails.put(song.getName(),song.getAbsolutePath());
        }
    }

    public String getFilePath(String key){
        String songPath = "";
        if(fileDetails.get(key) != null) {
            songPath = fileDetails.get(key);
        }
        return songPath;
    }


    public void openPlayer(String path){
        Intent intent = new Intent();
        intent.putExtra(AUDIO_PATH, path);
        setResult(52, intent);
        finish();

    }





}
