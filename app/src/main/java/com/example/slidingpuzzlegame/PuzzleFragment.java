package com.example.slidingpuzzlegame;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        final View rootView =
                inflater.inflate(R.layout.puzzle_fragment, container, false);
        this.rootView = rootView;


        difficulty = getArguments().getInt("Difficulty");
        final GridLayout puzzleBoard = (GridLayout) rootView.findViewById(R.id.puzzle_board);
        ViewTreeObserver viewTreeObserver = puzzleBoard.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    puzzleBoard.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    frameWidth = puzzleBoard.getWidth();
                    frameHeight = puzzleBoard.getHeight();
                }
            });
        }
//rootView.getWidth() - puzzleBoard.getPaddingRight() - puzzleBoard.getPaddingLeft() -
        frameWidth = puzzleBoard.getMeasuredWidthAndState();
        frameHeight = rootView.getHeight();
        puzzleMatrix = new PuzzleMatrix(difficulty, getResources(), this.getContext(), puzzleBoard, frameWidth, frameHeight);
        // Return the View for the fragment's UI.
        return rootView;
    }

    public void testMoveClicked() {
    }



    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.test_piece:
//                //testMoveClicked();
//                break;
//        }
    }
}