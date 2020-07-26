package com.example.slidingpuzzlegame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GridLayout puzzleDisplay;
    private Button tipsPanel;
    private TextView title;
    private PuzzlePreStartFragment puzzlePreStartFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        puzzleDisplay = (GridLayout) findViewById(R.id.home_screen_puzzle_display);
        puzzleDisplay.setOnClickListener(this);
        tipsPanel = (Button) findViewById(R.id.tips_panel);
        title = (TextView) findViewById(R.id.game_title);
    }

    public PuzzlePreStartFragment getPreStartFragment() {
        return puzzlePreStartFragment;
    }

    public void openPreStartFragment() {
        puzzleDisplay.setVisibility(View.GONE);
        tipsPanel.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
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
        puzzleDisplay.setVisibility(View.VISIBLE);
        tipsPanel.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //playButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_screen_puzzle_display:
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