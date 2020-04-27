package com.bruce.raeasy.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ImagesUrlPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mFragments;

    public ImagesUrlPagerAdapter(
            @NonNull FragmentManager fm, int behavior, ArrayList<Fragment> fragments) {
        super(fm, behavior);
        mFragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
