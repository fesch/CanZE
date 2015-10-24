package lu.fisch.canze.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.ArrayList;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.fragments.FZPage0;
import lu.fisch.canze.fragments.FZPage1;
import lu.fisch.canze.fragments.FZPage2;
import lu.fisch.canze.fragments.FZPagerAdapter;
import lu.fisch.canze.interfaces.FieldListener;

/**
 * Created by jeroen on 10-10-15.
 */
public class AlexandreActivity  extends CanzeActivity implements FieldListener,ActionBar.TabListener {


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
    FZPagerAdapter mSectionsPagerAdapter;

    /**
     * The ViewPager that will host the section contents.
     */
    ViewPager mViewPager;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alexandre);

        subscribedFields = new ArrayList<>();

        valoresmemorizados = new long[100];

        addListener(SOC);

        instance = this;

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections
        // of the app.
        mSectionsPagerAdapter = new FZPagerAdapter(this, this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.FZpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.



            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));


        }

        //By default choose the second page (index 1)
        actionBar.setSelectedNavigationItem(1);




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


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    //actualizar páginas
    public void actualizarpaginas(long[] arrayd) {

        try {

            FZPage1 fragmentoy1 = (FZPage1) mSectionsPagerAdapter.getRegisteredFragment(1);

            if (fragmentoy1 != null) {
                fragmentoy1.actpag1(arrayd);
            }

            /*
            FZPage2 fragmentoy2 = (FZPage2) mSectionsPagerAdapter.getRegisteredFragment(2);

            if (fragmentoy2 != null) {
                fragmentoy2.actpag2(arrayd);
            }

            FZPage0 fragmentoy0 = (FZPage0) mSectionsPagerAdapter.getRegisteredFragment(0);

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
