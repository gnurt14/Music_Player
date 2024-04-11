package com.example.musicplayer;

import static android.content.Context.MODE_PRIVATE;
import static com.example.musicplayer.MainActivity.PATH_TO_FRAG;
import static com.example.musicplayer.MainActivity.SHOW_MINI_PLAYER;
import static com.example.musicplayer.MainActivity.SONG_NAME;
import static com.example.musicplayer.MainActivity.SONG_TO_FRAG;
import static com.example.musicplayer.MainActivity.musicFiles;
import static com.example.musicplayer.MusicService.MUSIC_FILE;
import static com.example.musicplayer.MusicService.MUSIC_LAST_PLAYED;
import static com.example.musicplayer.MusicService.actionPlaying;
import static com.example.musicplayer.PlayerActivity.uri;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class NowPlayingFragment extends Fragment implements ServiceConnection {
    ImageView nextBtn, playPauseBtn;
    TextView songName;
    View view;
    MusicService musicService;
    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public NowPlayingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        nextBtn = view.findViewById(R.id.homeNextButton);
        playPauseBtn = view.findViewById(R.id.homePlayPause);
        songName = view.findViewById(R.id.homeNameSong);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicService != null){
                    musicService.nextBtnClicked();
                    if(getActivity() != null){
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                                .edit();
                        editor.putString(MUSIC_FILE, musicService.musicFiles.get(musicService.position).getPath());
                        editor.putString(SONG_NAME, musicService.musicFiles.get(musicService.position).getTitle());
                        editor.apply();
                        SharedPreferences preferences = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED,
                                MODE_PRIVATE);
                        String path = preferences.getString(MUSIC_FILE, null);
                        String song_name = preferences.getString(SONG_NAME, null);
                        if(path != null){
                            SHOW_MINI_PLAYER = true;
                            PATH_TO_FRAG = path;
                            SONG_TO_FRAG = song_name;
                        }else{
                            SHOW_MINI_PLAYER = false;
                            PATH_TO_FRAG = null;
                            SONG_TO_FRAG = null;
                        }
                        if(SHOW_MINI_PLAYER){
                            if(PATH_TO_FRAG != null){
                                songName.setText(SONG_TO_FRAG);
                            }
                        }
                    }
                }
            }
        });
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicService != null){
                    musicService.playPauseBtnClicked();
                    if(musicService.isPlaying()){
                        playPauseBtn.setImageResource(R.drawable.ic_pause_home);
                    }
                    else{
                        playPauseBtn.setImageResource(R.drawable.ic_play_home);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SHOW_MINI_PLAYER){
            if(PATH_TO_FRAG != null){
                songName.setText(SONG_TO_FRAG);
                Intent intent = new Intent(getContext(), MusicService.class);
                if(getContext() != null){
                    getContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
                }
                if(actionPlaying != null){
                    playPauseBtn.setImageResource(R.drawable.ic_pause_home);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getContext() != null){
            getContext().unbindService(this);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }
}