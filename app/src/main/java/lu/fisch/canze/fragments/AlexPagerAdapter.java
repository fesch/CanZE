/*
    CanZE
    Take a closer look at your ZE car

    Copyright (C) 2015 - The CanZE Team
    http://canze.fisch.lu

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package lu.fisch.canze.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import lu.fisch.canze.R;
import lu.fisch.canze.activities.AlexandreActivity;

/**
 * A FragmentPagerAdapter that returns a fragment corresponding to one of
 * the primary sections of the app.
 */
public class AlexPagerAdapter extends FragmentPagerAdapter {

    private AlexandreActivity myPagerAdapter;
    SparseArray <Fragment> registeredFragments = new SparseArray<Fragment>();

    Context c;

    public AlexPagerAdapter(AlexandreActivity MyPagerAdapter, Context c, FragmentManager fm) {
        super(fm);
        myPagerAdapter = MyPagerAdapter;
        this.c = c;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        if (i == 0) {
            fragment = new AlexBatteryFragment();
        }
        if (i == 1) {
            fragment = new AlexGeneralFragment();
        }
        if (i == 2) {
            fragment = new AlexDrivingFrament();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return myPagerAdapter.getString(R.string.title_FZ_section0).toUpperCase();
            case 1:
                return myPagerAdapter.getString(R.string.title_FZ_section1).toUpperCase();
            case 2:
                return myPagerAdapter.getString(R.string.title_FZ_section2).toUpperCase();
        }
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }


}
