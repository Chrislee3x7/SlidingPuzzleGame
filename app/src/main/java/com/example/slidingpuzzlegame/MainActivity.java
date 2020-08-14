package com.example.slidingpuzzlegame;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int NUMBER_OF_DIFFICULTIES = 4;
    public static final int BASE_DIFFICULTY = 3;

    private GridLayout puzzleDisplay;
    private ImageButton homeSettingsButton;
    private ImageButton specialStatsButton;
    private TextView title;
    private PuzzlePreStartFragment puzzlePreStartFragment;
    private FragmentPagerAdapter tipsPagerAdapter;

    //pager stuff
    private LinearLayout tipsPanel;
    private ViewPager tipsViewPager;
    private LinearLayout pagerDots;
    private View[] pagerDotsArray;
    private TextView[] pagerViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        puzzleDisplay = (GridLayout) findViewById(R.id.home_screen_puzzle_display);
        specialStatsButton = findViewById(R.id.special_stats_button);
        specialStatsButton.setOnClickListener(this);
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
        pagerViews = new TextView[4];
        pagerViews[0] = createPagerTextView("Welcome to Sliding Puzzle game! Hope you enjoy!" +
                " Swipe to see some tips!");
        pagerViews[1] = createPagerTextView("Try tapping a tile to move " +
                "it to the open location!");
        pagerViews[2] = createPagerTextView("You can also move multiple pieces at once!");
        pagerViews[3] = createPagerTextView("By starting in the blank spot, you can swipe through multiple " +
                "pieces to solve quickly!");
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

        tipsViewPager.setAdapter(new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup collection, int position) {
                TextView textViewToAdd = pagerViews[position];
                textViewToAdd.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textViewToAdd.setGravity(Gravity.CENTER);
                collection.addView(textViewToAdd);
                return textViewToAdd;
            }

            @Override
            public int getCount() {
                return pagerViews.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup collection, int position, Object view) {
                collection.removeView((View) view);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "hello";
            }
        });

        setupPagerIndicatorDots();

        pagerDotsArray[0].setBackgroundResource(R.drawable.selected_dot);

        tipsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                SoundPlayer.playWhoosh(getApplicationContext());
                for (int i = 0; i < pagerDotsArray.length; i++) {
                    pagerDotsArray[i].setBackgroundResource(R.drawable.unselected_dot);
                }
                pagerDotsArray[position].setBackgroundResource(R.drawable.selected_dot);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupPagerIndicatorDots() {
        //number of tips or banners to display
        pagerDotsArray = new View[pagerViews.length];
        for (int i = 0; i < pagerDotsArray.length; i++) {
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