package com.example.slidingpuzzlegame;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer {

    public static void playSheenSound(Context context) {

        MediaPlayer sheenSound = MediaPlayer.create(context, R.raw.sheen_sound);
        sheenSound.start();
    }

    public static void playHollowShimmerSound(Context context) {

        MediaPlayer hollowShimmer = MediaPlayer.create(context, R.raw.hollow_shimmer);
        hollowShimmer.start();
    }
}
