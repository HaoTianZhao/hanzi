package com.example.hanzi.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.example.hanzi.R;

/**
 * Created by 赵 on 2018/4/21.
 * .
 */

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private MusicBinder binder;

    public MusicService() {
    }

    public class MusicBinder extends Binder {
        public void startMusic() {
            if (!mediaPlayer.isPlaying())
                mediaPlayer.start();
        }

        public void pauseMusic() {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
        }

        public void changeVolume(float volume) {
            mediaPlayer.setVolume(volume, volume);
        }

        public void changeStatus(){
            if(mediaPlayer.isPlaying())
                mediaPlayer.pause();
            else
                mediaPlayer.start();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (binder == null)
            binder = new MusicBinder();
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(MusicService.this, R.raw.we_dont_talk_anymore);
        mediaPlayer.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        super.onDestroy();
    }
}
