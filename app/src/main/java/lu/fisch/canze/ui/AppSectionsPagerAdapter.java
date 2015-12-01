package lu.fisch.canze.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import lu.fisch.canze.fragments.ExperimentalFragment;
import lu.fisch.canze.fragments.MainFragment;
import lu.fisch.canze.fragments.TechnicalFragment;

/**
 * Created by robertfisch on 01.12.2015.
 */
public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();

    public AppSectionsPagerAdapter(FragmentManager fm)
    {
        super(fm);

        fragments.add(new MainFragment());
        fragments.add(new TechnicalFragment());
        fragments.add(new ExperimentalFragment());
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}