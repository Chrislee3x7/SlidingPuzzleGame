package com.example.slidingpuzzlegame;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PuzzleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PuzzleFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private PuzzleMatrix puzzleMatrix;
    private View rootView;
    private int frameHeight;
    private int frameWidth;
    private Bitmap rootBitmap;
    private int difficulty;
    private Uri selectedImage;


    //movecount stuff
    private TextView movecount;
    private int numberOfMoves = 0;

    //timer stuff
    private TextView stopwatch;
    private int seconds = 0;
    private boolean running = false;
    private boolean paused = false;

    //end screen stuff
    private LinearLayout endScreen;
    private TextView endScreenTitle;
    private TextView endScreenDifficultyDisplay;
    private TextView endScreenTime;
    private TextView endScreenMovecount;
    private TextView endScreenTimeBest;
    private TextView endScreenMovecountBest;
    private ImageButton endScreenStatsButton;
    private ImageButton endScreenHomeButton;
    private ImageButton endScreenRetryButton;


    private String bestTime;
    private String bestMoveCount;

    //pause stuff
    private LinearLayout pauseMenu;
    private ImageButton pauseButton;
    private Button pauseMenuRetryButton;
    private Button pauseMenuGiveUpButton;
    private Button pauseMenuStatsButton;
    private Button pauseMenuOptionsButton;
    private View darkenBackground;

    private SharedPreferences sharedPreferences;


    public PuzzleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SimpleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PuzzleFragment newInstance(String param1, String param2) {
        return new PuzzleFragment();
    }

    public void runStopwatch() {
        // Creates a new Handler
        final Handler handler = new Handler();

        // Call the post() method, passing in a new Runnable. The post() method processes
        // code without a delay, so the code in the Runnable will run almost immediately.
        handler.post(new Runnable() {
            @Override

            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                // Format the seconds into hours, minutes,
                // and seconds.
                String time = String.format(Locale.getDefault(),
                        "%d:%02d:%02d", hours, minutes, secs);
                // Set the text view text.
                stopwatch.setText(time);
                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++;
                }
                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000);
            }
        });
    }

    public boolean stopwatchIsRunning() {
        return running;
    }

    public void pauseStopwatch() {
        //only set to paused = true if was running before onPause was called
        paused = running;
        running = false;
    }

    public void startStopwatch() {
        running = true;
        runStopwatch();
    }

    public void resumeStopwatch() {
        if (paused) {
            running = true;
        }
    }

    public void countOneMove() {
        if (numberOfMoves == 0) {
            movecount.setVisibility(View.VISIBLE);
        }
        numberOfMoves++;
        movecount.setText(String.valueOf(numberOfMoves));
    }

    public void puzzleEnd(boolean successful) {
        stopwatch.setVisibility(View.INVISIBLE);
        movecount.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        darkenBackground.setClickable(false);
        String currentTime = stopwatch.getText().toString();
        String currentMovecount = movecount.getText().toString();
        if (!successful) {
            endScreenTitle.setText("Try Again");
        } else {
            endScreenTime.setText(currentTime);
            endScreenMovecount.setText(String.valueOf(currentMovecount));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (isNewBestTime(currentTime)) {
                bestTime = currentTime;
                endScreenTimeBest.setText(currentTime);
                editor.putString("Best Time" + difficulty, stopwatch.getText().toString());
                editor.apply();
            }
            if (isNewBestMovecount(currentMovecount)) {
                bestMoveCount = currentMovecount;
                editor.putInt("Best Movecount" + difficulty, Integer.parseInt(movecount.getText().toString()));
                editor.apply();
            }
        }
        endScreenTimeBest.setText(bestTime);
        endScreenMovecountBest.setText(bestMoveCount);
        darkenBackground.setVisibility(View.VISIBLE);
        endScreen.setVisibility(View.VISIBLE);


    }

    private String getBestTime() {
        return sharedPreferences.getString("Best Time" + difficulty,
                "9:99:99");

    }

    private String getBestMovecount() {
        if (!sharedPreferences.contains("Best Movecount" + difficulty)) {
            return String.valueOf(Integer.MAX_VALUE);
        }
        return Integer.toString(sharedPreferences.getInt("Best Movecount" + difficulty, 0));
    }

    private boolean isNewBestTime(String curentTime) {
        if (bestMoveCount.equals(getString(R.string.default_time_value))) {
            return true;
        }
        if (curentTime.substring(0, 1).equals(bestTime.substring(0, 1))) {
            if (curentTime.substring(2, 4).equals(bestTime.substring(2, 4))) {
                if (curentTime.substring(5, 7).equals(bestTime.substring(5, 7))) {
                    return false;
                } else {
                    return Integer.parseInt(curentTime.substring(5, 7)) < Integer.parseInt(bestTime.substring(5, 7));
                }
            } else {
                return Integer.parseInt(curentTime.substring(2, 4)) < Integer.parseInt(bestTime.substring(2, 4));
            }
        } else {
            return Integer.parseInt(curentTime.substring(0, 1)) < Integer.parseInt(bestTime.substring(0, 1));
        }
    }

    public boolean isNewBestMovecount(String currentMovecount) {
        if (bestMoveCount.equals(getString(R.string.default_movecount_value))) {
            return true;
        }
        return Integer.parseInt(currentMovecount) < Integer.parseInt(bestMoveCount);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        puzzleMatrix.scramblePuzzle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        //only set to paused = true if was running before onPause was called
        pauseStopwatch();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (paused) {
            running = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        final View rootView =
                inflater.inflate(R.layout.puzzle_fragment, container, false);
        this.rootView = rootView;
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        frameWidth = display.getWidth();
        frameHeight = display.getHeight();
        difficulty = getArguments().getInt("Difficulty");
        Bitmap bitmap = null;
        if (getArguments().getSerializable("Selected Image") == null) {
            selectedImage = null;
        } else {
            selectedImage = Uri.parse(getArguments().getSerializable("Selected Image").toString());
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
            } catch (IOException e) {
                //input null
            }
        }

        final GridLayout puzzleBoard = (GridLayout) rootView.findViewById(R.id.puzzle_board);
        puzzleMatrix = new PuzzleMatrix(difficulty, getResources(), this.getContext(), puzzleBoard,
                rootView, frameWidth, frameHeight, bitmap, this);

        stopwatch = (TextView) rootView.findViewById(R.id.stopwatch_display);
        movecount = (TextView) rootView.findViewById(R.id.movecount_display);

        //end screen stuff
        endScreen = (LinearLayout) rootView.findViewById(R.id.end_screen);
        endScreenTitle = (TextView) rootView.findViewById(R.id.end_screen_title);
        endScreenDifficultyDisplay = (TextView) rootView.findViewById(R.id.end_screen_difficulty_display);
        endScreenDifficultyDisplay.setText(difficulty + " x " + difficulty);
        endScreenTime = (TextView) rootView.findViewById(R.id.end_screen_time);
        endScreenMovecount = (TextView) rootView.findViewById(R.id.end_screen_movecount);
        endScreenTimeBest = (TextView) rootView.findViewById(R.id.end_screen_time_best);
        endScreenMovecountBest = (TextView) rootView.findViewById(R.id.end_screen_movecount_best);
        endScreenHomeButton = (ImageButton) rootView.findViewById(R.id.home_button);
        endScreenHomeButton.setOnClickListener(this);
        endScreenRetryButton = (ImageButton) rootView.findViewById(R.id.retry_button);
        endScreenRetryButton.setOnClickListener(this);
        endScreenStatsButton = (ImageButton) rootView.findViewById(R.id.stats_button);
        endScreenStatsButton.setOnClickListener(this);
        sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);

        darkenBackground = rootView.findViewById(R.id.darken_background);
        bestTime = getBestTime();
        bestMoveCount = getBestMovecount();

        //pause menu stuff
        pauseMenu = (LinearLayout) rootView.findViewById(R.id.pause_menu);
        pauseButton = (ImageButton) rootView.findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(this);
        pauseMenuRetryButton = (Button) rootView.findViewById(R.id.pause_menu_retry_button);
        pauseMenuRetryButton.setOnClickListener(this);
        pauseMenuGiveUpButton = (Button) rootView.findViewById(R.id.pause_menu_give_up_button);
        pauseMenuGiveUpButton.setOnClickListener(this);
        pauseMenuStatsButton = (Button) rootView.findViewById(R.id.pause_menu_stats_button);
        pauseMenuStatsButton.setOnClickListener(this);
        pauseMenuOptionsButton = (Button) rootView.findViewById(R.id.pause_menu_options_button);
        pauseMenuOptionsButton.setOnClickListener(this);
        darkenBackground = rootView.findViewById(R.id.darken_background);
        darkenBackground.setOnClickListener(this);

        //        scrambleButton = rootView.findViewById(R.id.scramble_button);
//        scrambleButton.setOnClickListener(this);
        // Return the View for the fragment's UI.
        return rootView;
    }


    public void close() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stats_button:
                SoundPlayer.playLiquidDropClick(getContext());
                openStatisticsFragment();
                break;
            case R.id.home_button:
                //close this fragment and return to start screen
                SoundPlayer.playLiquidDropClick(getContext());
                ((MainActivity) getActivity()).showMainMenu();
                close();
                break;
            case R.id.retry_button:
                SoundPlayer.playLiquidDropClick(getContext());
                //reload the same puzzle with another scramble
                //close this fragment
                MainActivity mainActivity = ((MainActivity) getActivity());
                mainActivity.openPreStartFragment();
//                PuzzlePreStartFragment puzzlePreStartFragment = mainActivity.getPreStartFragment();
//                puzzlePreStartFragment.openPuzzleFragment(difficulty);
                close();
                break;


            case R.id.pause_button:
                SoundPlayer.playLiquidDropClick(getContext());
                pauseStopwatch();
                darkenBackground.setVisibility(View.VISIBLE);
                puzzleMatrix.setMovementLocked(true);
                pauseMenu.setVisibility(View.VISIBLE);
                break;
            case R.id.pause_menu_give_up_button:
                SoundPlayer.playLiquidDropClick(getContext());
                darkenBackground.setVisibility(View.GONE);
                pauseMenu.setVisibility(View.GONE);
                puzzleEnd(false);
                break;
            case R.id.pause_menu_retry_button:
                SoundPlayer.playLiquidDropClick(getContext());
                darkenBackground.setVisibility(View.INVISIBLE);
                pauseMenu.setVisibility(View.GONE);
                //implement retry
                break;
            case R.id.pause_menu_stats_button:
                SoundPlayer.playLiquidDropClick(getContext());
                openStatisticsFragment();
                //dont need to close the pausemenu
                break;
            case R.id.pause_menu_options_button:
                SoundPlayer.playLiquidDropClick(getContext());
                //also do not need to close the pause menue also do not need to darken background
                break;
            case R.id.darken_background:
                SoundPlayer.playLiquidDropClick(getContext());
                darkenBackground.setVisibility(View.INVISIBLE);
                puzzleMatrix.setMovementLocked(false);
                pauseMenu.setVisibility(View.GONE);
                resumeStopwatch();

        }
    }

    public void openStatisticsFragment() {
        // Create a new Fragment to be placed in the activity layout
        final StatisticsFragment statisticsFragment = new StatisticsFragment();
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        //statisticsFragment.setArguments(getActivity().getIntent().getExtras());
        // Add the fragment to the 'fragment_container' FrameLayout
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main, statisticsFragment).commit();
    }
}