package com.example.slidingpuzzlegame;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PuzzlePreStartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PuzzlePreStartFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    private View rootView;
    private Button cancelButton;
    private Button confirmButton;
    private Spinner difficultyDropdown;

    public PuzzlePreStartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PuzzlePreStartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PuzzlePreStartFragment newInstance(String param1, String param2) {
        PuzzlePreStartFragment fragment = new PuzzlePreStartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_puzzle_pre_start, container, false);
        cancelButton = (Button) rootView.findViewById(R.id.cancel_button);
        confirmButton = (Button) rootView.findViewById(R.id.confirm_button);
        difficultyDropdown = (Spinner) rootView.findViewById(R.id.difficulty_dropdown);
        cancelButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        return rootView;
    }

    public void close() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    public void openPuzzleFragment(int puzzleDifficulty) {
        PuzzleFragment puzzleFragment = new PuzzleFragment();
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        Bundle bundle = new Bundle();
        bundle.putInt("Difficulty", puzzleDifficulty);
        puzzleFragment.setArguments(bundle);
        // Add the fragment to the 'fragment_container' FrameLayout
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main, puzzleFragment).commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_button:
                //close this fragment and go back to main menu/home
                close();
                break;
            case R.id.confirm_button:
                //close this fragment and go to the puzzle of the specified info
                int difficulty = Integer.parseInt((Character.toString(difficultyDropdown.getSelectedItem().toString().charAt(0))));
                openPuzzleFragment(difficulty);
                close();
                break;

        }
    }

}