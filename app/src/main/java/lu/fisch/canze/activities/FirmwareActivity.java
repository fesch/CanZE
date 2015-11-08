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

package lu.fisch.canze.activities;


import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.interfaces.FieldListener;


// Jeroen

public class FirmwareActivity extends CanzeActivity implements FieldListener {

    // There are hardware dependencies here (i.e. different CLIMA ECU's with different software loads) that we do not yet fully understand.

    //                                             EVC     TCU     LBC     PEB     AIBAG   USM     CLUSTER EPS     ABS     UBP     BCM     CLIM    UPA     BCB     LBC2    LINSCH
//  private static final int [] zoeVersions     = {0x0203, 0x0767, 0x0751, 0x0a06, 0x0470, 0xc630, 0x0507, 0x014a, 0x1178, 0x1523, 0x140e, 0x0702, 0x0017, 0x0210, 0x0751,      0};
//  private static final int [] fluenceVersions = {0x0202, 0x0172, 0x7505, 0x02ba, 0x0305, 0x0907, 0x0026, 0x014a, 0x8160,      0, 0x140e, 0x0007, 0x0024, 0xd300, 0x5c0a,      0};
//  private static final int [] kangooVersions  = {0x0201, 0x1011, 0x7505, 0x0205,      1,      1, 0x003d,      1,      1,      1, 0x0003,      1,      1, 0xd300, 0x5c0a,      1};
//  private static final int [] x10Versions     = zoeVersions;
    private static final int [] zoeVersions     = {0x0680, 0x0336, 0x0000, 0x0200, 0x0470, 0x2420, 0x0504, 0x0800, 0x0e43, 0x0c04, 0x0000, 0x0515, 0x0000, 0x0000, 0x0000, 0x0000};
    private static final int [] fluenceVersions = {0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000};
    private static final int [] kangooVersions  = {0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000};
    private static final int [] x10Versions     = zoeVersions;

    private static int versions [] = null;


    private ArrayList<Field> subscribedFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware);

        subscribedFields = new ArrayList<>();

        switch (Fields.getInstance().getCar()) {
            case Fields.CAR_FLUENCE:
                versions = fluenceVersions;
                break;
            case Fields.CAR_KANGOO:
                versions = kangooVersions;
                break;
            case Fields.CAR_X10:
                versions = x10Versions;
                break;
            default:
                versions = zoeVersions;
                break;
        }

        TextView textView = (TextView) findViewById(R.id.link);
        textView.setText(Html.fromHtml("Learn more about the car's computers <a href='http://canze.fisch.lu/computers/'>here</a>."));
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        initListeners();

     }

    private void addListener(String sid) {
        Field field;
        field = MainActivity.fields.getBySID(sid);
        if (field != null) {
            field.addListener(this);
            if(MainActivity.device!=null)
                MainActivity.device.addActivityField(field);
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

    @Override
    protected void onResume() {
        super.onResume();

        // initialise the widgets
        initListeners();
    }

    private void initListeners() {

        subscribedFields = new ArrayList<>();

        addListener("793.6180.144"); // BCB put here as it seems to stop reponding very easily
        addListener("7ec.6180.144"); // SCH
        addListener("7da.6180.144"); // TCU
        addListener("7bb.6180.144"); // LBC
        addListener("77e.6180.144"); // PEB
        addListener("772.6180.144"); // AIRBAG
        addListener("76d.6180.144"); // USM not for Fluence
        addListener("763.6180.144"); // CLUSTER
        addListener("762.6180.144"); // EPS
        addListener("760.6180.144"); // ABS
        addListener("7bc.6180.144"); // UBP not for Fluence
        addListener("765.6180.144"); // BCM
        addListener("764.6180.144"); // CLIM
        addListener("76e.6180.144"); // UPA not for Zoe
        addListener("7b6.6180.144"); // LBC2
        //addListener("722.6180.144"); // LINSCH not for FLuence or Zoe
    }

    // This is the event fired as soon as this the registered fields are
    // getting updated by the corresponding reader class.
    @Override
    public void onFieldUpdateEvent(final Field field) {
        // the update has to be done in a separate thread
        // otherwise the UI will not be repainted
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String fieldId = field.getSID();
                TextView tv = null;
                int refVersion = 0;

                // get the text field
                switch (fieldId) {

                    case "7ec.6180.144":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        refVersion = versions [0];
                        break;
                    case "7da.6180.144":
                        tv = (TextView) findViewById(R.id.ecu7da);
                        refVersion = versions [1];
                        break;
                    case "7bb.6180.144":
                        tv = (TextView) findViewById(R.id.ecu7bb);
                        refVersion = versions [2];
                        break;
                    case "77e.6180.144":
                        tv = (TextView) findViewById(R.id.ecu77e);
                        refVersion = versions [3];
                        break;
                    case "772.6180.144":
                        tv = (TextView) findViewById(R.id.ecu772);
                        refVersion = versions [4];
                        break;
                    case "76d.6180.144":
                        tv = (TextView) findViewById(R.id.ecu76d);
                        refVersion = versions [5];
                        break;
                    case "763.6180.144":
                        tv = (TextView) findViewById(R.id.ecu763);
                        refVersion = versions [6];
                        break;
                    case "762.6180.144":
                        tv = (TextView) findViewById(R.id.ecu762);
                        refVersion = versions [7];
                        break;
                    case "760.6180.144":
                        tv = (TextView) findViewById(R.id.ecu760);
                        refVersion = versions [8];
                        break;
                    case "7bc.6180.144":
                        tv = (TextView) findViewById(R.id.ecu7bc);
                        refVersion = versions [9];
                        break;
                    case "765.6180.144":
                        tv = (TextView) findViewById(R.id.ecu765);
                        refVersion = versions [10];
                        break;
                    case "764.6180.144":
                        tv = (TextView) findViewById(R.id.ecu764);
                        refVersion = versions [11];
                        break;
                    case "76e.6180.144":
                        tv = (TextView) findViewById(R.id.ecu76e);
                        refVersion = versions [12];
                        break;
                    case "793.6180.144":
                        tv = (TextView) findViewById(R.id.ecu793);
                        refVersion = versions [13];
                        break;
                    case "7b6.6180.144":
                        tv = (TextView) findViewById(R.id.ecu7b6);
                        refVersion = versions [14];
                        break;
//                  case "722.6180.144":
//                      tv = (TextView) findViewById(R.id.ecu722);
//                      refVersion = versions [15];
//                      break;
                }

                // set regular new content, all exeptions handled above
                if (tv != null) {
                    int curVersion = (int) field.getValue();
                    if (curVersion != 0) {
                        String hexCurVersion = Integer.toHexString(curVersion);
                        hexCurVersion = ("0000" + hexCurVersion).substring(hexCurVersion.length());
                        if (refVersion != 0) {
                            String hexRefVersion = Integer.toHexString(refVersion);
                            hexRefVersion = ("0000" + hexRefVersion).substring(hexRefVersion.length());
                            if (curVersion > refVersion) {
                                hexCurVersion += " > " + hexRefVersion;
                            } else if (curVersion < refVersion) {
                                hexCurVersion += " < " + hexRefVersion;
                            }
                        }
                        tv.setText(hexCurVersion);
                    }
                }

                tv = (TextView) findViewById(R.id.textDebug);
                tv.setText(fieldId);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
