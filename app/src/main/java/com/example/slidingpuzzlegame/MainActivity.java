package com.example.slidingpuzzlegame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button playButton;
    private PuzzlePreStartFragment puzzlePreStartFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton = (Button) findViewById(R.id.start_button);
        playButton.setOnClickListener(this);
    }

    public PuzzlePreStartFragment getPreStartFragment() {
        return puzzlePreStartFragment;
    }

    public void openPreStartFragment() {
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

    public void showMainMenu() {
        playButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //playButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_button:
                SoundPlayer.playLiquidDropClick(this);
                openPreStartFragment();
        }
    }

    //    public void closePreStartFragment(View view) {
//        playButton.setVisibility(View.GONE);
//        //playButton.setEnabled(false);
//        getSupportFragmentManager().beginTransaction().remove(puzzlePreStartFragment).commit();
//    }
}