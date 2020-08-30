package com.example.slidingpuzzlegame;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.transition.TransitionInflater;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;

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
    private DecimalFormat df = new DecimalFormat("0.0");
    private TextView stopwatch;
    private double seconds = 0;
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
    private ImageView bestIconTime;
    private ImageView bestIconMovecount;


    private float bestTime;
    private int bestMoveCount;

    //pause stuff
    private LinearLayout pauseMenu;
    private ImageButton pauseButton;
    private Button pauseMenuRetryButton;
    private Button pauseMenuGiveUpButton;
    private Button pauseMenuStatsButton;
    private Button pauseMenuOptionsButton;
    private View darkenBackground;

    private SharedPreferences sharedPreferences;

    private MyReceiver broadcastReceiver;

    private AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
    private AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);


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
//                int hours = seconds / 3600;
//                int minutes = (seconds % 3600) / 60;
//                int secs = seconds % 60;

                // Format the seconds into hours, minutes,
                // and seconds.
//                String time = String.format(Locale.getDefault(),
//                        "%d:%02d:%02d", hours, minutes, secs);


                String time = df.format(seconds);
                // Set the text view text.
                stopwatch.setText(time);
                // If running is true, increment the
                // seconds variable.
                if (running) {
                    if (stopwatch.getVisibility() != View.VISIBLE) {
                        stopwatch.setVisibility(View.VISIBLE);
                    }
                    seconds += 0.1;
                }
                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 100);
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
        float currentTime = Float.parseFloat(stopwatch.getText().toString());
        int currentMovecount = Integer.parseInt(movecount.getText().toString());
        if (!successful) {
            endScreenTitle.setText("Try Again");
            SoundPlayer.playAwwDisappointment(getContext());
        } else {
            SoundPlayer.playApplause(getContext());

            incrementSolveCount();
            addToTimeSum(currentTime);
            addToMovecountSum(currentMovecount);

            boolean isNewBestTime = isNewBestTime(currentTime);
            boolean isNewBestMovecount = isNewBestMovecount(currentMovecount);

            if (isNewBestTime) {
                setNewBestTime(currentTime);
            }

            if (isNewBestMovecount) {
                setNewBestMovecount(currentMovecount);
            }

            if (isNewBestTime || isNewBestMovecount) {
                SoundPlayer.playWowSparkle(getContext());
            }
        }
        endScreenTime.setText(!successful ? "–" : String.format(Float.toString(currentTime), df));
        endScreenMovecount.setText(!successful ? "–" : Integer.toString(currentMovecount));
        endScreenTimeBest.setText(bestTime < 0 ? "–" : String.valueOf(bestTime));
        endScreenMovecountBest.setText(bestMoveCount < 0 ? "–" : String.valueOf(bestMoveCount));
        darkenBackground.setAnimation(fadeIn);
        endScreen.setAnimation(fadeIn);
        darkenBackground.setVisibility(View.VISIBLE);
        endScreen.setVisibility(View.VISIBLE);


    }

    private float getTimeSum() {
        return sharedPreferences.getFloat(getString(R.string.shared_preference_time_sum) + difficulty, 0);
    }

    private int getMovecountSum() {
        return sharedPreferences.getInt(getString(R.string.shared_preference_movecount_sum) + difficulty, 0);
    }

    private int getSolveCount() {
        return sharedPreferences.getInt(getString(R.string.shared_preference_solve_count) + difficulty, 0);
    }

    private void incrementSolveCount() {
        sharedPreferences.edit().putInt(getString(R.string.shared_preference_solve_count) + difficulty,
                getSolveCount() + 1).apply();
    }

    private void addToTimeSum(float timeToAdd) {
        sharedPreferences.edit().putFloat(getString(R.string.shared_preference_time_sum) + difficulty,
                getTimeSum() + timeToAdd).apply();
    }

    private void addToMovecountSum(int movecountToAdd) {
        sharedPreferences.edit().putInt(getString(R.string.shared_preference_movecount_sum) + difficulty,
                getMovecountSum() + movecountToAdd).apply();
    }

    private float getBestTime() {
        return sharedPreferences.getFloat(getString(R.string.shared_preference_best_time)
                        + difficulty,
                Float.parseFloat(getString(R.string.default_time_value)));
    }

    private void setNewBestTime(float currentTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        bestIconTime.setVisibility(View.VISIBLE);
        bestTime = currentTime;
        endScreenTimeBest.setText(currentTime < 0 ? "–" : String.valueOf(currentTime));
        editor.putFloat("Best Time" + difficulty, Float.parseFloat(stopwatch.getText().toString()));
        editor.apply();
    }

    private int getBestMovecount() {
        return sharedPreferences.getInt(getString(R.string.shared_preference_best_movecount) + difficulty,
                Integer.parseInt(getString(R.string.default_movecount_value)));
    }

    public void setNewBestMovecount(int currentMovecount) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        bestIconMovecount.setVisibility(View.VISIBLE);
        bestMoveCount = currentMovecount;
        endScreenTimeBest.setText(currentMovecount < 0 ? "–" :String.valueOf(currentMovecount));
        editor.putInt("Best Movecount" + difficulty, Integer.parseInt(movecount.getText().toString()));
        editor.apply();
    }

    private boolean isNewBestTime(double curentTime) {

        if (bestTime == Double.parseDouble(getString(R.string.default_time_value))) {
            return true;
        }
        return curentTime < bestTime;
//        if (curentTime.substring(0, 1).equals(bestTime.substring(0, 1))) {
//            if (curentTime.substring(2, 4).equals(bestTime.substring(2, 4))) {
//                if (curentTime.substring(5, 7).equals(bestTime.substring(5, 7))) {
//                    return false;
//                } else {
//                    return Integer.parseInt(curentTime.substring(5, 7)) < Integer.parseInt(bestTime.substring(5, 7));
//                }
//            } else {
//                return Integer.parseInt(curentTime.substring(2, 4)) < Integer.parseInt(bestTime.substring(2, 4));
//            }
//        } else {
//            return Integer.parseInt(curentTime.substring(0, 1)) < Integer.parseInt(bestTime.substring(0, 1));
//        }
    }

    public boolean isNewBestMovecount(int currentMovecount) {
        if (bestMoveCount == Integer.parseInt(getString(R.string.default_movecount_value))) {
            return true;
        }
        return currentMovecount < bestMoveCount;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        puzzleMatrix.scramblePuzzle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.fade));
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        fadeIn.setDuration(500);
        fadeOut.setDuration(500);
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
            String path = selectedImage.getPath();
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (exif != null) {
                Toast.makeText(getContext(), "exif is not null", Toast.LENGTH_SHORT).show();
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                bitmap = rotateBitmap(bitmap ,orientation);
            }
            else {
                Toast.makeText(getContext(), "exif IS null", Toast.LENGTH_SHORT).show();
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
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.apply();
        bestIconTime = rootView.findViewById(R.id.best_icon_time);
        bestIconMovecount = rootView.findViewById(R.id.best_icon_movecount);

        darkenBackground = rootView.findViewById(R.id.darken_background);
        updateRecords();

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
//        pauseMenuOptionsButton = (Button) rootView.findViewById(R.id.pause_menu_options_button);
//        pauseMenuOptionsButton.setOnClickListener(this);
        darkenBackground = rootView.findViewById(R.id.darken_background);
        darkenBackground.setOnClickListener(this);

        broadcastReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        //getActivity().registerReceiver(broadcastReceiver, filter);


        //        scrambleButton = rootView.findViewById(R.id.scramble_button);
//        scrambleButton.setOnClickListener(this);
        // Return the View for the fragment's UI.
        return rootView;
    }

//    //convert the image URI to the direct file system path of the image file
//    public String getRealPathFromURI(Uri contentUri) {
//
//        // can post image
//        String[] proj = {MediaStore.Images.Media.DATA};
//        Cursor cursor = getActivity().managedQuery(contentUri,
//                proj, // Which columns to return
//                null,       // WHERE clause; which rows to return (all rows)
//                null,       // WHERE clause selection arguments (none)
//                null); // Order-by clause (ascending by name)
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//
//        return cursor.getString(column_index);
//    }



    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }



    public void updateRecords() {
        bestTime = getBestTime();
        bestMoveCount = getBestMovecount();
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
//                mainActivity.openPreStartFragment();
                PuzzleFragment newPuzzle = new PuzzleFragment();
                newPuzzle.setArguments(getArguments());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.activity_main, newPuzzle).commit();
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
                //reload the same puzzle with another scramble
                //close this fragment
//                mainActivity.openPreStartFragment();
                PuzzleFragment newPuzzleFromPause = new PuzzleFragment();
                newPuzzleFromPause.setArguments(getArguments());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.activity_main, newPuzzleFromPause).commit();
                close();
                break;
            case R.id.pause_menu_stats_button:
                SoundPlayer.playLiquidDropClick(getContext());
                openStatisticsFragment();
                //dont need to close the pausemenu
                break;
//            case R.id.pause_menu_options_button:
//                SoundPlayer.playLiquidDropClick(getContext());
//                //also do not need to close the pause menu also do not need to darken background
//                break;
            case R.id.darken_background:
                SoundPlayer.playLiquidDropClick(getContext());
                darkenBackground.setVisibility(View.INVISIBLE);
                puzzleMatrix.setMovementLocked(false);
                pauseMenu.setVisibility(View.GONE);
                updateRecords();
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