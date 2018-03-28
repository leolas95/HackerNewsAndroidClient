package com.leonardo.hackernewsclient;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    private final int PAGE_COUNT = 2;
    private String[] titles = new String[]{"Story", "Comments"};
    private Context context;
    private Uri[] uris;

    PagerAdapter(FragmentManager fm, Context context, Uri[] uris) {
        super(fm);
        this.context = context;
        this.uris = uris;
    }

    @Override
    public Fragment getItem(int position) {
        return StoryDetailsFragment.newInstance(uris[position]);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
