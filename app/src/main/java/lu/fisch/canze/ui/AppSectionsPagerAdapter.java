package lu.fisch.canze.ui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

import lu.fisch.canze.fragments.ExperimentalFragment;
import lu.fisch.canze.fragments.MainFragment;
import lu.fisch.canze.fragments.TechnicalFragment;

/**
 * Created by robertfisch on 01.12.2015.
 */
public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();

    public AppSectionsPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments.add(new MainFragment());          // 0
        fragments.add(new TechnicalFragment());     // 1
        fragments.add(new ExperimentalFragment());  // 2
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