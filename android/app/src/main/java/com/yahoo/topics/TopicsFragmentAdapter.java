package com.yahoo.topics;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TopicsFragmentAdapter extends FragmentPagerAdapter {
    public TopicsFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show all feeds
                return TopicsFragment.feeds("feeds");
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return TopicsFragment.feeds("Page # 2");
            default:
                return null;
        }
    }

    @Override public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "discover";
            case 1:
                return "messages";
        }
        return "topics";
    }
}
