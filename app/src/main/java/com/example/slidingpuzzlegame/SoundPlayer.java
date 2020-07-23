package com.example.slidingpuzzlegame;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer {

    public static void playSheenSound(Context context) {

        MediaPlayer sound = MediaPlayer.create(context, R.raw.sheen_sound);
        sound.start();
    }

    public static void playHollowShimmerSound(Context context) {

        MediaPlayer sound = MediaPlayer.create(context, R.raw.hollow_shimmer);
        sound.start();
    }

    public static void playHardRefuseClick(Context context) {

        MediaPlayer sound = MediaPlayer.create(context, R.raw.hard_refuse_click);
        sound.start();
    }

    public static void playLiquidDropClick(Context context) {

        MediaPlayer sound = MediaPlayer.create(context, R.raw.water_drop);
        sound.start();
    }

    public static void playOptionsClick(Context context) {

        MediaPlayer sound = MediaPlayer.create(context, R.raw.options_click);
        sound.start();
    }

    public static void playBacktrackClick(Context context) {

        MediaPlayer sound = MediaPlayer.create(context, R.raw.backtrack_click);
        sound.start();
    }

    public static void playGlassClick(Context context) {

        MediaPlayer sound = MediaPlayer.create(context, R.raw.glass_click);
        sound.start();
    }
}
