package com.example.slidingpuzzlegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button playButton;
    private PuzzlePreStartFragment puzzlePreStartFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton = (Button) findViewById(R.id.start_button);
    }

    public void openPreStartFragment(View view) {
        playButton.setVisibility(View.GONE);
        //playButton.setEnabled(false);
        // Create a new Fragment to be placed in the activity layout
        puzzlePreStartFragment = new PuzzlePreStartFragment();
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        puzzlePreStartFragment.setArguments(getIntent().getExtras());
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main, puzzlePreStartFragment).commit();
    }

//    public void closePreStartFragment(View view) {
//        playButton.setVisibility(View.GONE);
//        //playButton.setEnabled(false);
//        getSupportFragmentManager().beginTransaction().remove(puzzlePreStartFragment).commit();
//    }
}