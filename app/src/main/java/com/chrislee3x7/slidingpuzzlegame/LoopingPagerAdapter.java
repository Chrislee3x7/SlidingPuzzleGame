package com.chrislee3x7.slidingpuzzlegame;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

public class LoopingPagerAdapter extends PagerAdapter {

    private TextView[] pagerViews;
    private Context context;

    public LoopingPagerAdapter(TextView[] pagerViews, Context context) {
        this.pagerViews = pagerViews;
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
//        int displayPosition = mapPagerPositionToDisplayPosition(position);
//        collection.addView(textViewToAdd);

//        int modelPosition = mapPagerPositionToDisplayPosition(position);
        TextView textViewToAdd = pagerViews[position];
        textViewToAdd.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textViewToAdd.setGravity(Gravity.CENTER);
        if (textViewToAdd.getParent() != null) {
            collection.removeView(textViewToAdd);
        }
        collection.addView(textViewToAdd);


        return textViewToAdd;
    }

    @Override
    public int getCount() {
//        return pagerViews.length == 0 ? 0 : pagerViews.length + 2;
        return pagerViews.length;
    }


    public int getRealCount() {
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
}
