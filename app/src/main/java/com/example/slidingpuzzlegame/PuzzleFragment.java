package com.example.slidingpuzzlegame;

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
import android.widget.GridLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Locale;

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
        paused = running;
        running = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (paused) {
            running = true;
        }
    }

    public void runStopwatch()
    {
        // Creates a new Handler
        final Handler handler = new Handler();

        // Call the post() method, passing in a new Runnable. The post() method processes
        // code without a delay, so the code in the Runnable will run almost immediately.
        handler.post(new Runnable() {
            @Override

            public void run()
            {
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
        running = false;
    }

    public void startStopwatch() {
        running = true;
        runStopwatch();
    }

    public void countOneMove() {
        if (numberOfMoves == 0) {
            movecount.setVisibility(View.VISIBLE);
        }
        numberOfMoves++;
        movecount.setText(String.valueOf(numberOfMoves));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        puzzleMatrix.scramblePuzzle();
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
//        scrambleButton = rootView.findViewById(R.id.scramble_button);
//        scrambleButton.setOnClickListener(this);
        // Return the View for the fragment's UI.
        return rootView;
    }

    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//
//        }
    }
}