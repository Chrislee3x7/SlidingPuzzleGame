package com.example.slidingpuzzlegame;

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
            TextView cardBestTime = ((TextView) card.findViewById(R.id.card_best_time));
            TextView cardBestMovecount = ((TextView) card.findViewById(R.id.card_best_movecount));

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

            cardBestTime.setText(sharedPreferences
                    .getString(getString(R.string.shared_preference_best_time) + i, "––:––:––"));
            String bestMoveCountToSet = String.valueOf(sharedPreferences
                    .getInt(getString(R.string.shared_preference_best_movecount) + i, 0));

            if (bestMoveCountToSet.equals("0")) {
                bestMoveCountToSet = "–";
            }
            cardBestMovecount.setText(bestMoveCountToSet);
        }
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
            editor.apply();
        }
        updateCards();
    }



    public void confirmResetRecord(final int difficulty) {
        new AlertDialog.Builder(getContext())
                .setTitle("Reset " + difficulty + " x " + difficulty + "?")
                .setMessage("Do you really want to reset the records for this difficulty? Tapping OK will " +
                        "confirm your choice to clear your current " + difficulty + " x " + difficulty + " standings!")
                .setIcon(null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        SoundPlayer.playLiquidDropClick(getContext());
                        resetRecord(difficulty);Toast.makeText(getActivity().getApplicationContext(),
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
        editor.apply();
        updateCards();
    }

    public void close() {
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

    public void broadcastIntent(View view){
//        Toast.makeText(getContext(), "broadcasted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setAction("com.example.slidingpuzzlegame");
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