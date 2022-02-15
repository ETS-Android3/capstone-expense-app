package com.gsbatra.expensedeck.ui.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.gsbatra.expensedeck.view.fragments.Goal;
import com.gsbatra.expensedeck.view.fragments.Subscription;
import com.gsbatra.expensedeck.view.fragments.Summary;
import com.gsbatra.expensedeck.view.fragments.Expense;
import com.gsbatra.expensedeck.view.fragments.Income;
import com.gsbatra.expensedeck.R;
import com.gsbatra.expensedeck.view.fragments.All;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.allTab, R.string.incomeTab, R.string.expenseTab, R.string.summaryTab,
            R.string.subscriptionTab, R.string.goalTab};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Fragment fragment = null;
        switch(position){
            case 0:
                fragment = new All();
                break;
            case 1:
                fragment = new Income();
                break;
            case 2:
                fragment = new Expense();
                break;
            case 3:
                fragment = new Summary();
                break;
            case 4:
                fragment = new Subscription();
                break;
            case 5:
                fragment = new Goal();
                break;
        }
        assert fragment != null;
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 6 total pages.
        return 6;
    }
}