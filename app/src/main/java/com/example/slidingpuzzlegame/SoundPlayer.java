package com.example.slidingpuzzlegame;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer {

    public static void playSheenSound(Context context) {

        final MediaPlayer sound = MediaPlayer.create(context, R.raw.sheen_sound);
        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                sound.release();
            }
        });
        sound.start();
    }

    public static void playHollowShimmerSound(Context context) {

        final MediaPlayer sound = MediaPlayer.create(context, R.raw.hollow_shimmer);
        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                sound.release();
            }
        });
        sound.start();
    }

    public static void playHardRefuseClick(Context context) {

        final MediaPlayer sound = MediaPlayer.create(context, R.raw.sparky_click);
        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                sound.release();
            }
        });
        sound.start();
    }

    public static void playLiquidDropClick(Context context) {

        final MediaPlayer sound = MediaPlayer.create(context, R.raw.water_drop);
        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                sound.release();
            }
        });
        sound.start();
    }

    public static void playOptionsClick(Context context) {

        final MediaPlayer sound = MediaPlayer.create(context, R.raw.options_click);
        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                sound.release();
            }
        });
        sound.start();
    }

    public static void playBacktrackClick(Context context) {

        final MediaPlayer sound = MediaPlayer.create(context, R.raw.backtrack_click);
        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                sound.release();
            }
        });
        sound.start();
    }

    public static void playGlassClick(Context context) {

        final MediaPlayer sound = MediaPlayer.create(context, R.raw.glass_click);
        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                sound.release();
            }
        });
        sound.start();
    }
}
