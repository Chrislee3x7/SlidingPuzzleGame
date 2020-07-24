package com.example.slidingpuzzlegame;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private View rootView;

    private Button resetRecordsButton;
    private Button backButton;

    private SharedPreferences sharedPreferences;


    public StatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_statistics.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
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
        rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        resetRecordsButton = rootView.findViewById(R.id.reset_records_button);
        resetRecordsButton.setOnClickListener(this);
        backButton = rootView.findViewById(R.id.statistics_page_back_button);
        backButton.setOnClickListener(this);

        sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);

        // Inflate the layout for this fragment
        return rootView;
    }

    public void resetRecords() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 2; i <= 6; i++) {
            editor.remove("Best Time" + i);
            editor.remove("Best Movecount" + i);
            editor.apply();
        }
    }

    public void close() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset_records_button:
                SoundPlayer.playLiquidDropClick(getContext());
                resetRecords();
                break;
            case R.id.statistics_page_back_button:
                SoundPlayer.playLiquidDropClick(getContext());
                close();
                break;
        }
    }
}