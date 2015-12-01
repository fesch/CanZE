package lu.fisch.canze.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import lu.fisch.canze.fragments.ExperimentalFragment;
import lu.fisch.canze.fragments.MainFragment;
import lu.fisch.canze.fragments.TechnicalFragment;

/**
 * Created by robertfisch on 01.12.2015.
 */
public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

    public AppSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }



    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new MainFragment();
            case 1:
                return new TechnicalFragment();
            case 2:
                return new ExperimentalFragment();

            default:
                return new MainFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int i) {
        switch (i) {
            case 0:
                return "Main";
            case 1:
                return "Technical";
            case 2:
                return "Experimental";

            default:
                return "?";
        }
    }
}