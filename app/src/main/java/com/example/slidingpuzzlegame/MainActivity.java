package com.example.slidingpuzzlegame;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int NUMBER_OF_DIFFICULTIES = 4;
    public static final int BASE_DIFFICULTY = 3;

    private GridLayout puzzleDisplay;
    private ImageButton homeSettingsButton;
    private ImageButton specialStatsButton;
    private TextView title;
    private PuzzlePreStartFragment puzzlePreStartFragment;
    private LoopingPagerAdapter loopingPagerAdapter;

    //pager stuff
    private LinearLayout tipsPanel;
    private ViewPager tipsViewPager;
    private LinearLayout pagerDots;
    private View[] pagerDotsArray;
    private TextView[] pagerViews;

    private AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
    private AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        puzzleDisplay = (GridLayout) findViewById(R.id.home_screen_puzzle_display);
        specialStatsButton = findViewById(R.id.special_stats_button);
        specialStatsButton.setOnClickListener(this);

        fadeIn.setDuration(500);
        fadeOut.setDuration(500);
        //fade.setRepeatMode(Animation.REVERSE);

//        homeSettingsButton = (ImageButton) findViewById(R.id.home_screen_settings_button);
        puzzleDisplay.setOnClickListener(this);
        title = (TextView) findViewById(R.id.game_title);

        tipsPanel = (LinearLayout) findViewById(R.id.tips_panel);
        setPagerViews();
        setUpViewPager();
    }

    public PuzzlePreStartFragment getPreStartFragment() {
        return puzzlePreStartFragment;
    }

    public void openPreStartFragment() {
        puzzleDisplay.startAnimation(fadeOut);
        tipsPanel.startAnimation(fadeOut);
        title.startAnimation(fadeOut);

        puzzleDisplay.setVisibility(View.GONE);
//        homeSettingsButton.setVisibility(View.GONE);
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
        puzzleDisplay.startAnimation(fadeIn);
        tipsPanel.startAnimation(fadeIn);
        title.startAnimation(fadeIn);

        puzzleDisplay.setVisibility(View.VISIBLE);
        tipsPanel.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);


//        homeSettingsButton.setVisibility(View.VISIBLE);
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
                break;
            case R.id.special_stats_button:
                SoundPlayer.playLiquidDropClick(this);
                openStatisticsFragment();
                break;
        }
    }

    public void openStatisticsFragment() {
        // Create a new Fragment to be placed in the activity layout
        final StatisticsFragment statisticsFragment = new StatisticsFragment();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        //statisticsFragment.setArguments(getActivity().getIntent().getExtras());
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main, statisticsFragment).commit();
    }

    public void setPagerViews() {
        pagerViews = new TextView[8];
        int i = 0;
        pagerViews[i++] = createPagerTextView("Want a fresh start? You can reset your current standings in the" +
                " Statistics page!");
        pagerViews[i++] = createPagerTextView("Welcome to Sliding Puzzle Game! Hope you enjoy!" +
                " Swipe to see some tips!");
        pagerViews[i++] = createPagerTextView("You can tap a tile to move " +
                "it to the open location!");
        pagerViews[i++] = createPagerTextView("Try moving multiple pieces at once!");
        pagerViews[i++] = createPagerTextView("By starting in the blank spot, you can swipe through multiple " +
                "pieces to solve quickly!");
        pagerViews[i++] = createPagerTextView("Check out your records for each puzzle in the Statistics page!");
        pagerViews[i++] = createPagerTextView("Want a fresh start? You can reset your current standings in the" +
                " Statistics page!");
        pagerViews[i++] = createPagerTextView("Welcome to Sliding Puzzle Game! Hope you enjoy!" +
                " Swipe to see some tips!");
    }

    public TextView createPagerTextView(String text) {
        TextView textView = new TextView(getApplicationContext());
        textView.setBackgroundResource(R.drawable.play_button_background);
        textView.setPadding(30, 0, 30, 0);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setHeight(150);
        textView.setTextSize(18);
        textView.setText(text);
        return textView;
    }

    public void setUpViewPager() {
        tipsViewPager = findViewById(R.id.tips_display);
        tipsViewPager.setPageMargin(80);
        pagerDots = findViewById(R.id.pager_dots);

        loopingPagerAdapter = new LoopingPagerAdapter(pagerViews, getApplicationContext());

        tipsViewPager.setAdapter(loopingPagerAdapter);

        setupPagerIndicatorDots();

        pagerDotsArray[1].setBackgroundResource(R.drawable.selected_dot);

        tipsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int truePosition;
            private int previousPosition = -1;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                Toast.makeText(getApplicationContext(), "page selected", Toast.LENGTH_SHORT).show();
                truePosition = position;


//                Toast.makeText(getApplicationContext(), position, Toast.LENGTH_SHORT).show();
                if (position == 0) {
                    previousPosition = position;
                    position = 6;
                }
                if (position == pagerViews.length - 1) {
                    previousPosition = position;
                    position = 1;
                }
                //alterdpostion is when 0 -> 6 or 7 -> 1
                if (previousPosition != position) {
                    SoundPlayer.playWhoosh(getApplicationContext());
                }

                for (int i = 0; i < pagerDotsArray.length; i++) {
                    if (i == 0 || i == pagerViews.length - 1) {
                        continue;
                    }
                    pagerDotsArray[i].setBackgroundResource(R.drawable.unselected_dot);
                }
                if (position != 0 && position != pagerViews.length - 1) {
                    pagerDotsArray[position].setBackgroundResource(R.drawable.selected_dot);
                    //SoundPlayer.playWhoosh(getApplicationContext());
                }
                previousPosition = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Toast.makeText(getApplicationContext(), "scroll state changed", Toast.LENGTH_SHORT).show();
                if ((truePosition == 0) && state == ViewPager.SCROLL_STATE_IDLE) {
                    tipsViewPager.setCurrentItem(pagerViews.length - 2, false);
//                    SoundPlayer.playWhoosh(getApplicationContext());
                } else if ((truePosition == pagerViews.length - 1) && state == ViewPager.SCROLL_STATE_IDLE) {
                    tipsViewPager.setCurrentItem(1, false);
//                    SoundPlayer.playWhoosh(getApplicationContext());
                }
            }
        });
        tipsViewPager.setCurrentItem(1, false);
    }

    private void setupPagerIndicatorDots() {
        //number of tips or banners to display
        pagerDotsArray = new View[pagerViews.length];
        for (int i = 0; i < pagerDotsArray.length; i++) {
            if (i == 0 || i == pagerViews.length - 1) {
                continue;
            }
            pagerDotsArray[i] = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 0, 5, 0);
            params.height = 24;
            params.width = 24;
            pagerDotsArray[i].setLayoutParams(params);
            pagerDotsArray[i].setBackgroundResource(R.drawable.unselected_dot);
            //ivArrayDotsPager[i].setAlpha(0.4f);
            pagerDotsArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setAlpha(1);
                }
            });
            pagerDots.addView(pagerDotsArray[i]);
            pagerDots.bringToFront();
        }
    }
}