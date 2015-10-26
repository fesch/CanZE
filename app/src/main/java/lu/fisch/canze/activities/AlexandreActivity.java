package lu.fisch.canze.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.view.PagerTabStrip;


import java.util.ArrayList;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.fragments.AlexGeneralFragment;
import lu.fisch.canze.fragments.AlexPagerAdapter;
import lu.fisch.canze.fragments.SlidingTabLayout;
import lu.fisch.canze.interfaces.FieldListener;

/**
 * Created by jeroen on 10-10-15.
 */
public class AlexandreActivity  extends CanzeActivity implements FieldListener {


    public static final String SOC                     = "42e.0";

    private ArrayList<Field> subscribedFields;

    private static long[] valoresmemorizados;

    private static AlexandreActivity instance = null;

    /**
     * The android.support.v4.view.PagerAdapter that will provide fragments for
     * each of the sections. We use a
     * android.support.v4.app.FragmentPagerAdapter derivative, which will keep
     * every loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * android.support.v4.app.FragmentStatePagerAdapter.
     */
    AlexPagerAdapter mSectionsPagerAdapter;

    /**
     * The ViewPager that will host the section contents.
     */
    ViewPager mViewPager;

    SlidingTabLayout mSlidingTabLayout;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alexandre);

        subscribedFields = new ArrayList<>();

        valoresmemorizados = new long[100];

        addListener(SOC);

        instance = this;

        // Create the adapter that will return a fragment for each of the three
        // primary sections
        // of the app.
        mSectionsPagerAdapter = new AlexPagerAdapter(this, this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.FZpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);


        //choose the middle page
        mViewPager.setCurrentItem(1);



    }

    private void addListener(String sid) {
        Field field;
        field = MainActivity.fields.getBySID(sid);
        if (field != null) {
            field.addListener(this);
            MainActivity.device.addField(field);
            subscribedFields.add(field);
        } else {
            MainActivity.toast("sid " + sid + " does not exist in class Fields");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();



            // free up the listeners again
            for (Field field : subscribedFields) {
                field.removeListener(this);
            }
            subscribedFields.clear();


    }


    public void onFieldUpdateEvent(final Field field) {


        // the update has to be done in a separate thread
        // otherwise the UI will not be repainted
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String fieldId = field.getSID();
                long temp;

                switch (fieldId) {

                    case SOC: //já vem multiplicado por 100
                        valoresmemorizados[0]=(long) (field.getValue()*100.0);
                        actualizarpaginas(valoresmemorizados);
                        break;

                }

            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alex, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit_alex) {

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    //update fragments
    public void actualizarpaginas(long[] arrayd) {

        try {

            AlexGeneralFragment fragmentoy1 = (AlexGeneralFragment) mSectionsPagerAdapter.getRegisteredFragment(1);

            if (fragmentoy1 != null) {
                fragmentoy1.actpag1(arrayd);
            }

            /*
            AlexDrivingFrament fragmentoy2 = (AlexDrivingFrament) mSectionsPagerAdapter.getRegisteredFragment(2);

            if (fragmentoy2 != null) {
                fragmentoy2.actpag2(arrayd);
            }

            AlexBatteryFragment fragmentoy0 = (AlexBatteryFragment) mSectionsPagerAdapter.getRegisteredFragment(0);

            if (fragmentoy0 != null) {
                fragmentoy0.actpag0(arrayd);
            }
            */

        }
        catch (Exception e) {
            //ignore
        }

    }



}
