package com.chrislee3x7.slidingpuzzlegame;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    private View statsLayout;
    private Button resetRecordsButton;
    private ImageButton backButton;


    //record display stuff
    private LinearLayout recordsLinearLayout;
    private LinearLayout[] recordCards = new LinearLayout[MainActivity.NUMBER_OF_DIFFICULTIES];
    private ObjectAnimator cardEnterAnimation;

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
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
        setExitTransition(inflater.inflateTransition(R.transition.slide_right));
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        statsLayout = rootView.findViewById(R.id.stats_layout);
        statsLayout.setOnClickListener(this);

        recordsLinearLayout = rootView.findViewById(R.id.records_linear_layout);
        resetRecordsButton = rootView.findViewById(R.id.reset_records_button);
        resetRecordsButton.setOnClickListener(this);
        backButton = rootView.findViewById(R.id.statistics_page_back_button);
        backButton.setOnClickListener(this);

        sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
        inflateCards(inflater);


        return rootView;
    }

    public void updateCards() {
        recordsLinearLayout.removeAllViews();
        inflateCards(getLayoutInflater());
    }

    public void inflateCards(LayoutInflater inflater) {
        for (int i = MainActivity.BASE_DIFFICULTY; i < MainActivity.NUMBER_OF_DIFFICULTIES + MainActivity.BASE_DIFFICULTY; i++) {
            LinearLayout card = (LinearLayout) inflater.inflate(R.layout.stats_record_card, null);
            recordCards[i - MainActivity.BASE_DIFFICULTY] = card;
            recordsLinearLayout.addView(card);
            card.setOnClickListener(this);
            card.findViewById(R.id.individual_reset_button).setOnClickListener(this);

            LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            final float scale = getContext().getResources().getDisplayMetrics().density;
            int pixels = (int) (10 * scale + 0.5f);
            cardLayoutParams.setMargins(0, 0, 0, pixels);
            card.setLayoutParams(cardLayoutParams);

            TextView cardDifficultyTitle = ((TextView) card.findViewById(R.id.card_difficulty));
            TextView cardSolveCountDisplay = card.findViewById(R.id.solve_count_display);
            TextView cardBestTime = ((TextView) card.findViewById(R.id.card_best_time));
            TextView cardBestMovecount = ((TextView) card.findViewById(R.id.card_best_movecount));
            TextView cardAverageTime = card.findViewById(R.id.card_average_time);
            TextView cardAverageMovecount = card.findViewById(R.id.card_average_movecount);

            switch (i) {
                case 3:
                    cardDifficultyTitle.setText(getString(R.string.difficulty3));
                    break;
                case 4:
                    cardDifficultyTitle.setText(getString(R.string.difficulty4));
                    break;
                case 5:
                    cardDifficultyTitle.setText(getString(R.string.difficulty5));
                    break;
                case 6:
                    cardDifficultyTitle.setText(getString(R.string.difficulty6));
                    break;
            }

            int solveCount = sharedPreferences.getInt(getString(R.string.shared_preference_solve_count) + i, 0);

            float bestTimeToSet = sharedPreferences
                    .getFloat(getString(R.string.shared_preference_best_time) + i,
                            Float.parseFloat(getString(R.string.default_time_value)));
            int bestMoveCountToSet = sharedPreferences
                    .getInt(getString(R.string.shared_preference_best_movecount) + i,
                            Integer.parseInt(getString(R.string.default_movecount_value)));

            float averageTimeToSet = (sharedPreferences.getFloat(getString(R.string.shared_preference_time_sum) + i,
                    Float.parseFloat(getString(R.string.default_time_value)))
                    / sharedPreferences.getInt(getString(R.string.shared_preference_solve_count) + i, 1));
            float averageMovecountToSet = ((float) sharedPreferences.getInt(getString(R.string.shared_preference_movecount_sum) + i,
                    Integer.parseInt(getString(R.string.default_time_value))))
                    / sharedPreferences.getInt(getString(R.string.shared_preference_solve_count) + i, 1);

            cardSolveCountDisplay.setText(solveCount < 0 ? "–" : "( " + solveCount + " solves )");
            cardBestTime.setText(bestTimeToSet < 0 ? "–" : formatTime(bestTimeToSet));
            cardAverageTime.setText(averageTimeToSet < 0 ? "–" : formatTime(averageTimeToSet));
            cardBestMovecount.setText(bestMoveCountToSet < 0 ? "–" : (bestMoveCountToSet) + " moves");
            cardAverageMovecount.setText(averageMovecountToSet < 0 ? "–" : (String.format("%.1f", averageMovecountToSet)) + " moves");
//            Log.d("StatisticsFragment", formatTime(30.7f));
//            Log.d("StatisticsFragment", formatTime(30.7421f));
//            Log.d("StatisticsFragment", formatTime(60f));
//            Log.d("StatisticsFragment", formatTime(61f));
//            Log.d("StatisticsFragment", formatTime(3600f));
//            Log.d("StatisticsFragment", formatTime(3601f));
//            Log.d("StatisticsFragment", formatTime(3660f));
//            Log.d("StatisticsFragment", formatTime(Integer.MAX_VALUE));
        }
    }

    //changes seconds into the format of time i want 1h 2m 4s or 40.5s
    public String formatTime(float seconds) {
        int hour = (int) (seconds / 3600);
        int min = (int) (seconds % 3600 / 60);
        int secondsLeft = (int) (seconds % 60); // casting to int decimal will be gone

        StringBuilder displayTime = new StringBuilder();
        if (hour > 0) {
            displayTime.append(hour + "h ");
        }
        if (hour > 0 || min > 0) {
            displayTime.append(min + "m ");
        }
        if (seconds < 60) {
            displayTime.append(String.format("%.1f", seconds) + "s");
        }
        else {
            displayTime.append(secondsLeft + "s");
        }
        return displayTime.toString();
    }

    public void confirmResetRecords() {
        new AlertDialog.Builder(getContext())
                .setTitle("Reset All?")
                .setMessage("Do you really want to reset all records? Tapping OK will " +
                        "confirm your choice to clear all of your current standings!")
                .setIcon(null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        SoundPlayer.playLiquidDropClick(getContext());
                        resetAllRecords();
                        Toast.makeText(getActivity().getApplicationContext(),
                                "All records have been reset", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SoundPlayer.playLiquidDropClick(getContext());
                    }
                }).show();
    }

    public void resetAllRecords() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 2; i <= 6; i++) {
            editor.remove(getString(R.string.shared_preference_best_time) + i);
            editor.remove(getString(R.string.shared_preference_best_movecount) + i);
            editor.remove(getString(R.string.shared_preference_time_sum) + i);
            editor.remove(getString(R.string.shared_preference_movecount_sum) + i);
            editor.remove(getString(R.string.shared_preference_solve_count) + i);
            editor.apply();
        }
        updateCards();
    }


    public void confirmResetRecord(final int difficulty) {
        new AlertDialog.Builder(getContext())
                .setTitle("Reset " + difficulty + " x " + difficulty + "?")
                .setMessage("Do you really want to reset the records for this difficulty? Tapping OK will " +
                        "confirm your choice to clear your " + difficulty + " x " + difficulty + " standings!")
                .setIcon(null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        SoundPlayer.playLiquidDropClick(getContext());
                        resetRecord(difficulty);
                        Toast.makeText(getActivity().getApplicationContext(),
                                difficulty + " x " + difficulty + " records have been reset", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SoundPlayer.playLiquidDropClick(getContext());
                    }
                }).show();
    }

    public void resetRecord(int difficulty) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getString(R.string.shared_preference_best_time) + difficulty);
        editor.remove(getString(R.string.shared_preference_best_movecount) + difficulty);
        //for average
        editor.remove(getString(R.string.shared_preference_time_sum) + difficulty);
        editor.remove(getString(R.string.shared_preference_movecount_sum) + difficulty);
        editor.remove(getString(R.string.shared_preference_time_sum) + difficulty);
        editor.remove(getString(R.string.shared_preference_movecount_sum) + difficulty);
        editor.remove(getString(R.string.shared_preference_solve_count) + difficulty);
        editor.apply();
        updateCards();
    }

    public void close() {
        ((MainActivity) getActivity()).setClickable(true);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset_records_button:
                SoundPlayer.playLiquidDropClick(getContext());
                broadcastIntent(view);
                confirmResetRecords();
                break;
            case R.id.statistics_page_back_button:
                SoundPlayer.playLiquidDropClick(getContext());
                close();
                break;
            case R.id.record_card:
//                SoundPlayer.playLiquidDropClick(getContext());
//                LinearLayout card = ((LinearLayout) view);
//                deselectAllOtherCards(card);
//                toggleCardSelected(card);
                break;
            case R.id.individual_reset_button:
                SoundPlayer.playLiquidDropClick(getContext());
                //getting hte card which has a linear layout (where the pressed button is) within another ll
                //which is the one we want
                LinearLayout card = (LinearLayout) view.getParent().getParent();
                broadcastIntent(view);
                confirmResetRecord(getCardDifficulty(card));
                break;
            default:
//                deselectAllCards();
        }
    }

    public void broadcastIntent(View view) {
//        Toast.makeText(getContext(), "broadcasted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setAction("com.chrislee3x7.slidingpuzzlegame");
        getActivity().sendBroadcast(intent);
    }

    public int getCardDifficulty(LinearLayout card) {
        for (int i = 0; i < recordCards.length; i++) {
            if (recordCards[i] == card) {
                return i + MainActivity.BASE_DIFFICULTY;
            }
        }
        return 0;
    }

    public void toggleCardSelected(LinearLayout card) {
        int numberOfChildren = card.getChildCount();
        View resetButton = card.getChildAt(numberOfChildren - 1);
        if (resetButton.getVisibility() == View.VISIBLE) {
            resetButton.setVisibility(View.GONE);
            return;
        }
        resetButton.setVisibility(View.VISIBLE);
    }

    public void deselectAllCards() {
        final int resetButtonIndex = 4;
        for (int i = 0; i < recordsLinearLayout.getChildCount(); i++) {
            ((LinearLayout) recordsLinearLayout.getChildAt(i)).getChildAt(resetButtonIndex).setVisibility(View.GONE);
        }
    }

    public void deselectAllOtherCards(LinearLayout exceptionCard) {
        final int resetButtonIndex = 4;
        for (int i = 0; i < recordsLinearLayout.getChildCount(); i++) {
            LinearLayout currentCard = ((LinearLayout) recordsLinearLayout.getChildAt(i));
            if (currentCard == exceptionCard) {
                continue;
            }
            //hide button
            currentCard.getChildAt(resetButtonIndex).setVisibility(View.GONE);
        }
    }

}