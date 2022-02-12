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

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import lu.fisch.canze.BuildConfig;
import lu.fisch.canze.R;
import lu.fisch.canze.actors.Utils;
import lu.fisch.canze.classes.Activity;
import lu.fisch.canze.classes.ActivityRegistry;
import lu.fisch.canze.classes.Crashlytics;
import lu.fisch.canze.database.CanzeDataSource;

public class SettingsActivity extends AppCompatActivity {

    // Settings keys
    // -> Device
    public static final String SETTING_DEVICE_CHOICE = "btDeviceChoice";
    public static final String SETTING_DEVICE_NAME = "btDeviceName";
    public static final String SETTING_DEVICE_TYPE = "btDeviceType";
    public static final String SETTING_DEVICE_ADDRESS = "btDeviceAddress";
    public static final String SETTING_DEVICE_HTTP_GATEWAY = "btHttpGateway";
    public static final String SETTING_DEVICE_USE_ISOTP_FIELDS = "btUseIsotp";
    public static final String SETTING_DEVICE_USE_BACKGROUND_MODE = "btUseBackgroundMode";
    // -> Car
    public static final String SETTING_CAR_MODEL = "carModel";
    public static final String SETTING_CAR_USE_MILES = "carUseMiles";
    // -> Security
    public static final String SETTING_SECURITY_SAFE_MODE = "securitySafeMode";
    // -> Display
    public static final String SETTING_DISPLAY_THEME = "displayTheme";
    public static final String SETTING_DISPLAY_TOAST_LEVEL = "displayToastLevel";
    public static final String SETTING_DISPLAY_STARTUP_ACTIVITY = "displayStartupActivity";
    public static final String SETTING_DISPLAY_STARTUP_MENU = "displayStartupMenu";
    // -> Logging
    public static final String SETTING_LOGGING_DEBUG_LOG = "loggingDebugLog";
    public static final String SETTING_LOGGING_FIELDS_LOG = "loggingFieldLog";
    public static final String SETTING_LOGGING_USE_SD_CARD = "loggingUseSdCard";
    // -> Info
    public static final String INFO_APP_VERSION_LABEL = "lblVersionInfo";
    // -> App
    public static final String SETTING_APP_OLD_SETTINGS_MIGRATED = "appOldSettingsMigrated";
    public static final String SETTING_APP_OLD_SETTINGS_VALUES = "appOldSettingsValues";
    public static final String SETTING_APP_DISCLAIMER_SEEN = "appDisclaimerSeen";
    public static final String SETTING_APP_VERSION = "appVersion";
    // -> Actions & buttons
    public static final String BTN_LOGGING_SETTINGS = "btnLoggingSettings";
    public static final String BTN_CAN_SEE_SETTINGS = "btnCanSeeSettings";
    public static final String BTN_CLEAR_CACHE = "btnClearCache";
    public static final String BTN_CUSTOM_FRAGMENT = "btnCustomFragment";

    // Bluetooth devices types
    public static final String DEVICE_TYPE_HTTP_GATEWAY = "Http";
    public static final String DEVICE_TYPE_ELM_327 = "ELM327";
    public static final String DEVICE_TYPE_CAN_SEE = "CanSee";
    public static final String DEVICE_TYPE_ELM_HTTP = "ELM327Http";
    public static final String DEVICE_TYPE_BOB_DUE = "Bob Due";

    // Bluetooth devices names used to compare settings values
    public static final String DEVICE_NAME_HTTP_GATEWAY = "http gateway";
    public static final String DEVICE_NAME_CAN_SEE = "cansee";

    // Car models
    public static final CharSequence[] CAR_MODELS_LABELS = {
            "ZOE Q210",
            "ZOE R240",
            "ZOE Q90",
            "ZOE R90/110",
            "ZOE ZE40/ZE50",
            "DACIA SPRING",
            "TWINGO 3 Ph2 (unsupported)",
            "TWIZY (unsupported)"
    };
    public static final CharSequence[] CAR_MODELS_VALUES = {
            String.valueOf(MainActivity.CAR_ZOE_Q210),
            String.valueOf(MainActivity.CAR_ZOE_R240),
            String.valueOf(MainActivity.CAR_ZOE_Q90),
            String.valueOf(MainActivity.CAR_ZOE_R90),
            String.valueOf(MainActivity.CAR_X10PH2),
            String.valueOf(MainActivity.CAR_SPRING),
            String.valueOf(MainActivity.CAR_TWINGO),
            String.valueOf(MainActivity.CAR_TWIZY)
    };

    // Display contrast modes
    public static final CharSequence[] DISPLAY_THEMES_VALUES = {
            String.valueOf(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
            String.valueOf(AppCompatDelegate.MODE_NIGHT_YES),
            String.valueOf(AppCompatDelegate.MODE_NIGHT_NO),
    };

    // Display toast levels
    public static final CharSequence[] DISPLAY_TOAST_LEVELS_VALUES = {
            String.valueOf(MainActivity.TOAST_NONE),
            String.valueOf(MainActivity.TOAST_ELM),
            String.valueOf(MainActivity.TOAST_ELMCAR),
    };

    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.settingsFragment = new SettingsFragment();
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_list_main, settingsFragment)
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        settingsFragment.saveSettings();
        MainActivity.getInstance().handleDarkMode();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            settingsFragment.saveSettings();
            MainActivity.getInstance().handleDarkMode();
            finish();
            return true;
        } else if (id == R.id.action_cancel) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.REQUEST_ENABLE_BT) {
            settingsFragment.fillDeviceList();
        }

        MainActivity.debug("Code = " + requestCode);
    }

    public static void convertOldSettings(SharedPreferences settings) {
        // Check if old settings have already been migrated
        if (settings.getBoolean(SETTING_APP_OLD_SETTINGS_MIGRATED, false)) {
            return;
        }

        MainActivity.debug("Migrating old settings...");

        // Get the old settings for future use
        SharedPreferences.Editor editor = settings.edit();
        Map<String, ?> oldSettings = settings.getAll();
        MainActivity.debug("Settings before migration: " + oldSettings.toString());

        // Iterate over all the settings
        boolean removeSetting;
        for (Map.Entry<String, ?> entry : oldSettings.entrySet()) {
            String oldValue = entry.getValue().toString();
            removeSetting = false;
            switch (entry.getKey()) {
                // Map the old setting to the new one
                case "deviceName":
                    editor.putString(SETTING_DEVICE_NAME, oldValue);
                    removeSetting = true;
                    break;
                case "gatewayUrl":
                    editor.putString(SETTING_DEVICE_HTTP_GATEWAY, oldValue);
                    removeSetting = true;
                    break;
                case "startActivity":
                    editor.putInt(SETTING_DISPLAY_STARTUP_ACTIVITY, StrToIntDefault(oldValue, 0));
                    removeSetting = true;
                    break;
                case "deviceAddress":
                    editor.putString(SETTING_DEVICE_ADDRESS, oldValue);
                    removeSetting = true;
                    break;
                case "device":
                    editor.putString(SETTING_DEVICE_TYPE, oldValue);
                    removeSetting = true;
                    break;
                case "car":
                    short car = MainActivity.CAR_NONE;
                    switch (oldValue) {
                        case "None":
                            car = MainActivity.CAR_NONE;
                            break;
                        case "Zoé":
                        case "ZOE":
                        case "ZOE Q210":
                            car = MainActivity.CAR_ZOE_Q210;
                            break;
                        case "ZOE R240":
                            car = MainActivity.CAR_ZOE_R240;
                            break;
                        case "ZOE Q90":
                            car = MainActivity.CAR_ZOE_Q90;
                            break;
                        case "ZOE R90":
                        case "ZOE R90/110":
                            car = MainActivity.CAR_ZOE_R90;
                            break;
                        case "ZOE ZE50":
                            car = MainActivity.CAR_X10PH2;
                            break;
                        case "DACIA SPRING":
                            car = MainActivity.CAR_SPRING;
                            break;
                    }
                    editor.putInt(SETTING_CAR_MODEL, car);
                    removeSetting = true;
                    break;
                case "optDebugLog":
                    editor.putBoolean(SETTING_LOGGING_DEBUG_LOG, Boolean.parseBoolean(oldValue));
                    removeSetting = true;
                    break;
                case "optToast":
                    editor.putInt(SETTING_DISPLAY_TOAST_LEVEL, StrToIntDefault(oldValue, MainActivity.TOAST_ELMCAR));
                    removeSetting = true;
                    break;
                case "optBTBackground":
                    editor.putBoolean(SETTING_DEVICE_USE_BACKGROUND_MODE, Boolean.parseBoolean(oldValue));
                    removeSetting = true;
                    break;
                case "optSafe":
                    editor.putBoolean(SETTING_SECURITY_SAFE_MODE, Boolean.parseBoolean(oldValue));
                    removeSetting = true;
                    break;
                case "optMiles":
                    editor.putBoolean(SETTING_CAR_USE_MILES, Boolean.parseBoolean(oldValue));
                    removeSetting = true;
                    break;
                case "optAltFields":
                    editor.putBoolean(SETTING_DEVICE_USE_ISOTP_FIELDS, Boolean.parseBoolean(oldValue));
                    removeSetting = true;
                    break;
                case "optFieldLog":
                    editor.putBoolean(SETTING_LOGGING_FIELDS_LOG, Boolean.parseBoolean(oldValue));
                    removeSetting = true;
                    break;
                case "optDataExport":
                    editor.putBoolean(SETTING_LOGGING_USE_SD_CARD, Boolean.parseBoolean(oldValue));
                    removeSetting = true;
                    break;
                case "optDark":
                    editor.putInt(SETTING_DISPLAY_THEME, StrToIntDefault (oldValue, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM));
                    removeSetting = true;
                    break;
                case "disclaimer":
                    editor.putBoolean(SETTING_APP_DISCLAIMER_SEEN, Boolean.parseBoolean(oldValue));
                    removeSetting = true;
                    break;
                case "startMenu":
                    editor.putInt(SETTING_DISPLAY_STARTUP_MENU, StrToIntDefault (oldValue, 0));
                    removeSetting = true;
                    break;
            }

            // Remove the old setting if needed
            if (removeSetting) {
                editor.remove(entry.getKey());
            }
        }

        // Mark old settings as converted
        editor.putBoolean(SETTING_APP_OLD_SETTINGS_MIGRATED, true);

        // Save settings
        editor.apply();

        MainActivity.debug("Settings after migration: " + settings.getAll().toString());
    }

    private static int StrToIntDefault (String s, int defaultValue) {
        try{
            return Integer.parseInt(s);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        // Settings object
        SharedPreferences settings;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Load preferences file
            PreferenceManager preferenceManager = getPreferenceManager();
            preferenceManager.setSharedPreferencesName(MainActivity.PREFERENCES_FILE);
            preferenceManager.setSharedPreferencesMode(0);
            this.settings = preferenceManager.getSharedPreferences();
            MainActivity.debug(this.settings.toString());

            // Check for non-migrated settings (from old settings version)
            if (!this.settings.getBoolean(SettingsActivity.SETTING_APP_OLD_SETTINGS_MIGRATED, false)) {
                convertOldSettings(settings);
            }

            // Load views from settings fragment
            setPreferencesFromResource(R.xml.fragment_settings_main, rootKey);

            MainActivity.debug("ALL SETTINGS " + this.settings.getAll().toString());

            // Bind settings fields events
            bindSettingsEvents();

            // Bind buttons and actions
            bindActionButtons();

            // Load settings
            loadDeviceSettings();
            loadCarSettings();
            loadSecuritySettings();
            loadDisplaySettings();
            loadInfo();
        }
/*
        @Override
        public void onDetach() {
            super.onDetach();
            // saveSettings();
        }
*/
        private void bindSettingsEvents() {
            // On BT device choice
            findPreference(SETTING_DEVICE_CHOICE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    ListPreference devicesList = (ListPreference) preference;
                    String choiceValue = newValue.toString();
                    String choiceLabel = devicesList.getEntries()[devicesList.findIndexOfValue(choiceValue)].toString();

                    applyDeviceSettings(choiceLabel, choiceValue);

                    return true;
                }
            });

            // On safe mode check
            findPreference(SETTING_SECURITY_SAFE_MODE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    MainActivity.debug("SAFE MODE VALUE CHANGED" + newValue);
                    return true;
                }
            });
            findPreference(SETTING_SECURITY_SAFE_MODE).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    if (!((SwitchPreference)preference).isChecked()) {
                        // set dialog message
                        String yes = MainActivity.getStringSingle(R.string.prompt_YesIKnow);
                        String no = MainActivity.getStringSingle(R.string.prompt_NoSecureWay);

                        Display display = getActivity().getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        float width = size.x;
                        //int height = size.y;
                        width = width / getResources().getDisplayMetrics().density * getResources().getDisplayMetrics().scaledDensity;
                        if (width <= 480) {
                            yes = MainActivity.getStringSingle(R.string.default_Yes);
                            no = MainActivity.getStringSingle(R.string.default_No);
                        }

                        final Context context = getActivity();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        // set title
                        alertDialogBuilder.setTitle(R.string.prompt_Attention);

                        // set dialog message
                        alertDialogBuilder
                                .setMessage(MainActivity.getStringSingle(R.string.prompt_WarningDriving))
                                .setCancelable(true)
                                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        setSwitchPreferenceStatus(((SwitchPreference) preference), SETTING_SECURITY_SAFE_MODE, false);
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton(no,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // if this button is clicked, just close
                                                // the dialog box and do nothing
                                                setSwitchPreferenceStatus(((SwitchPreference) preference), SETTING_SECURITY_SAFE_MODE, true);
                                                dialog.cancel();
                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();

                        // Block the click by default as the actual result is handled by the dialog
                        return false;
                    }

                    return true;
                }
            });

            // On BT background mode check
            findPreference(SETTING_DEVICE_USE_BACKGROUND_MODE).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    if (((SwitchPreference)preference).isChecked()) {
                        // set dialog message
                        String yes = MainActivity.getStringSingle(R.string.prompt_YesIKnow);
                        String no = MainActivity.getStringSingle(R.string.prompt_NoThanks);

                        Display display = getActivity().getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        float width = size.x;
                        //int height = size.y;
                        width = width / getResources().getDisplayMetrics().scaledDensity;
                        if (width <= 480) {
                            yes = MainActivity.getStringSingle(R.string.default_Yes);
                            no = MainActivity.getStringSingle(R.string.default_No);
                        }

                        final Context context = getActivity();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        // set title
                        alertDialogBuilder.setTitle("ATTENTION");

                        // set dialog message
                        alertDialogBuilder
                                .setMessage(Html.fromHtml(MainActivity.getStringSingle(R.string.prompt_BluetoothOn)))
                                .setCancelable(true)
                                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        setSwitchPreferenceStatus(((SwitchPreference) preference), SETTING_DEVICE_USE_BACKGROUND_MODE, true);
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton(no,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // if this button is clicked, just close
                                                // the dialog box and do nothing
                                                setSwitchPreferenceStatus(((SwitchPreference) preference), SETTING_DEVICE_USE_BACKGROUND_MODE, false);
                                                dialog.cancel();
                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();

                        // Block the click by default as the actual result is handled by the dialog
                        return false;
                    }

                    return true;
                }
            });

            findPreference(SETTING_LOGGING_USE_SD_CARD).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    // add code here to check external SDcard is avail, writeable and has sufficient space
                    if (!MainActivity.getInstance().isExternalStorageWritable()) {
                        final Context context = getActivity();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        // set title
                        alertDialogBuilder.setTitle(R.string.prompt_Sorry);

                        // set dialog message
                        alertDialogBuilder
                                .setMessage(Html.fromHtml(MainActivity.getStringSingle(R.string.prompt_NoSd)))
                                .setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        setSwitchPreferenceStatus(((SwitchPreference) preference), SETTING_LOGGING_USE_SD_CARD, false);
                                        dialog.cancel();
                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                        MainActivity.dataExportMode = false; // due to SDcard not writeable

                        return false;
                    }

                    return true;
                }
            });

            // On debug log check
            findPreference(SETTING_LOGGING_DEBUG_LOG).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    // add code here to check external SDcard is avail, writeable and has sufficient space
                    final boolean sdcardCheck = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()); // check for space later
                    if (!sdcardCheck) {
                        final Context context = getActivity();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        // set title
                        alertDialogBuilder.setTitle(R.string.prompt_Sorry);

                        // set dialog message
                        alertDialogBuilder
                                .setMessage(Html.fromHtml(MainActivity.getStringSingle(R.string.prompt_NoSd)))
                                .setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        setSwitchPreferenceStatus(((SwitchPreference) preference), SETTING_LOGGING_DEBUG_LOG, false);
                                        dialog.cancel();
                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();

                        return false;
                    }

                    return true;
                }
            });

            // On debug fields check
            findPreference(SETTING_LOGGING_FIELDS_LOG).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    // add code here to check external SDcard is avail, writeable and has sufficient space
                    final boolean sdcardCheck = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()); // check for space later
                    if (!sdcardCheck) {
                        final Context context = getActivity();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        // set title
                        alertDialogBuilder.setTitle(R.string.prompt_Sorry);

                        // set dialog message
                        alertDialogBuilder
                                .setMessage(Html.fromHtml(MainActivity.getStringSingle(R.string.prompt_NoSd)))
                                .setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        setSwitchPreferenceStatus(((SwitchPreference) preference), SETTING_LOGGING_FIELDS_LOG, false);
                                        dialog.cancel();
                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();

                        return false;
                    }

                    return true;
                }
            });
        }

        private void loadDeviceSettings() {
            // Device address
            EditTextPreference deviceAddress = (EditTextPreference) findPreference(SETTING_DEVICE_ADDRESS);
            deviceAddress.setText(settings.getString(SETTING_DEVICE_ADDRESS, ""));

            // Device type
            ListPreference deviceType = (ListPreference) findPreference(SETTING_DEVICE_TYPE);
            deviceType.setValue(settings.getString(SETTING_DEVICE_TYPE, DEVICE_TYPE_ELM_327));

            // Use ISOTP
            SwitchPreference btUseIsotp = (SwitchPreference) findPreference(SETTING_DEVICE_USE_ISOTP_FIELDS);
            btUseIsotp.setChecked(settings.getBoolean(SETTING_DEVICE_USE_ISOTP_FIELDS, false));

            // Use background mode
            SwitchPreference btUseBackground = (SwitchPreference) findPreference(SETTING_DEVICE_USE_BACKGROUND_MODE);
            btUseBackground.setChecked(settings.getBoolean(SETTING_DEVICE_USE_BACKGROUND_MODE, false));

            // Device choice: load devices list
            ListPreference devicesList = findPreference(SETTING_DEVICE_CHOICE);
            tryTofillDeviceList();
            String deviceAddressValue = settings.getString(SETTING_DEVICE_ADDRESS, "");
            String deviceNameValue = settings.getString(SETTING_DEVICE_NAME, "");
            if (deviceNameValue.toLowerCase().startsWith(DEVICE_NAME_HTTP_GATEWAY)) {
                devicesList.setValue(DEVICE_TYPE_HTTP_GATEWAY);
                //deviceAddressValue = settings.getString(SETTING_DEVICE_HTTP_GATEWAY, "");
            } else if (deviceAddressValue.isEmpty()) {
                devicesList.setValueIndex(0);
            } else {
                devicesList.setValue(deviceAddressValue);
            }

            // Propagate device settings to other fields
            applyDeviceSettings(
                    settings.getString(SETTING_DEVICE_NAME, ""),
                    deviceAddressValue
            );
        }

        private void loadCarSettings() {
            // Car model
            ListPreference carModel = (ListPreference) findPreference(SETTING_CAR_MODEL);
            carModel.setEntryValues(CAR_MODELS_VALUES);
            carModel.setEntries(CAR_MODELS_LABELS);
            carModel.setValue(String.valueOf(settings.getInt(SETTING_CAR_MODEL, MainActivity.CAR_ZOE_Q210)));
        }

        private void loadSecuritySettings() {
            // Nothing for now
        }

        private void loadDisplaySettings() {
            // Display theme
            ListPreference theme = (ListPreference) findPreference(SETTING_DISPLAY_THEME);
            theme.setEntryValues(DISPLAY_THEMES_VALUES);
            theme.setValue(String.valueOf(settings.getInt(SETTING_DISPLAY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)));

            // Display toast level
            ListPreference toastLevel = (ListPreference) findPreference(SETTING_DISPLAY_TOAST_LEVEL);
            toastLevel.setEntryValues(DISPLAY_TOAST_LEVELS_VALUES);
            toastLevel.setValue(String.valueOf(settings.getInt(SETTING_DISPLAY_TOAST_LEVEL, MainActivity.TOAST_NONE)));

            // Startup activity
            fillStartupActivityList();
        }

        private void loadLoggingSettings() {
            // Nothing for now
        }

        private void loadInfo() {
            // Display build version
            EditTextPreference versionInfo = findPreference(INFO_APP_VERSION_LABEL);

            try {
                Date buildDate = new Date(BuildConfig.TIMESTAMP);
                SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.getStringSingle(R.string.format_YMDHM), Locale.getDefault());
                String version = MainActivity.getStringSingle(R.string.version) + BuildConfig.VERSION_NAME + " (" + BuildConfig.BUILD_TYPE + "-" + BuildConfig.BRANCH + ") " + MainActivity.getStringSingle(R.string.build) + sdf.format(buildDate);
                versionInfo.setTitle(version);
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }

        private void saveCarSettings(SharedPreferences.Editor editor) {
            // Car model
            editor.putInt(SETTING_CAR_MODEL, Integer.parseInt(((ListPreference) findPreference(SETTING_CAR_MODEL)).getValue()));
        }

        private void saveDisplaySettings(SharedPreferences.Editor editor) {
            // Display theme
            editor.putInt(SETTING_DISPLAY_THEME, Integer.parseInt(((ListPreference) findPreference(SETTING_DISPLAY_THEME)).getValue()));

            // Display toast level
            editor.putInt(SETTING_DISPLAY_TOAST_LEVEL, Integer.parseInt(((ListPreference) findPreference(SETTING_DISPLAY_TOAST_LEVEL)).getValue()));

            // Startup activity or menu
            ListPreference activitiesList = (ListPreference) findPreference(SETTING_DISPLAY_STARTUP_ACTIVITY);
            String startupChoice = activitiesList.getValue();
            if (startupChoice.startsWith("m")) {
                editor.remove(SETTING_DISPLAY_STARTUP_ACTIVITY);
                editor.putInt(SETTING_DISPLAY_STARTUP_MENU, Integer.parseInt(startupChoice.substring(1)));
            } else {
                // Find the menu corresponding to the selected activity to add it as parent
                boolean menuFound = false;
                for (String menu : new String[]{"MAIN", "TECHNICAL", "EXPERIMENTAL", "CUSTOM"}) {
                    int[] activityCategory;
                    try {
                        activityCategory = (int[]) ActivityRegistry.class.getField("ACTIVITIES_" + menu).get(null);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        // If the activity group is not found, go to the next
                        continue;
                    }
                    for (int activity : activityCategory) {
                        if (activity == Integer.parseInt(startupChoice)) {
                            menuFound = true;
                            try {
                                editor.putInt(SETTING_DISPLAY_STARTUP_MENU, (Integer) MainActivity.class.getField("MENU_" + menu).get(null));
                            } catch (IllegalAccessException | NoSuchFieldException e) {
                                // Fail silently
                            }
                        }
                    }
                }

                if (!menuFound) {
                    editor.remove(SETTING_DISPLAY_STARTUP_MENU);
                }
                editor.putInt(SETTING_DISPLAY_STARTUP_ACTIVITY, Integer.parseInt(startupChoice));
            }
        }

        private void bindActionButtons() {
            // CanSee settings
            Preference btnCanSeeSettings = findPreference(BTN_CAN_SEE_SETTINGS);
            btnCanSeeSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), CanSeeActivity.class);
                    startActivityForResult(intent, 4);

                    return true;
                }
            });

            // Log settings
            Preference btnLoggingSettings = findPreference(BTN_LOGGING_SETTINGS);
            btnLoggingSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), LoggingActivity.class);
                    startActivityForResult(intent, 4);

                    return true;
                }
            });

            // Clear cached data
            Preference btnClearCache = findPreference(BTN_CLEAR_CACHE);
            btnClearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // Clear settings
                    SharedPreferences.Editor editor = settings.edit();
                    editor.clear();
                    editor.apply();

                    // Clear data file
                    PreferenceManager preferenceManager = getPreferenceManager();
                    preferenceManager.setSharedPreferencesName(MainActivity.DATA_FILE);
                    preferenceManager.setSharedPreferencesMode(0);
                    SharedPreferences dataSettings = preferenceManager.getSharedPreferences();
                    dataSettings.edit().clear().apply();

                    // Clear database
                    CanzeDataSource cds = CanzeDataSource.getInstance();
                    if (cds != null) CanzeDataSource.getInstance().clear();

                    MainActivity.fields.clearAllFields();
                    MainActivity.toast(MainActivity.TOAST_NONE, R.string.toast_CacheCleared);

                    return true;
                }
            });

            // Configure custom fragment
            Preference btnCustomFragment = findPreference(BTN_CUSTOM_FRAGMENT);
            btnCustomFragment.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(getActivity(), SettingsCustomActivity.class);
                    startActivity(i);

                    return true;
                }
            });
        }

        private void applyDeviceSettings(String choiceLabel, String choiceValue) {
            EditTextPreference deviceAddress = (EditTextPreference) findPreference(SETTING_DEVICE_ADDRESS);
            ListPreference deviceType = (ListPreference) findPreference(SETTING_DEVICE_TYPE);
            SwitchPreference useIsotp = (SwitchPreference) findPreference(SETTING_DEVICE_USE_ISOTP_FIELDS);

            // Set fields depending on the detected dongle type
            if (choiceLabel.toLowerCase().startsWith(DEVICE_NAME_HTTP_GATEWAY)) {
                // Enable manual address selection
                deviceAddress.setEnabled(true);
                // Set gateway address
                deviceAddress.setText(settings.getString(SETTING_DEVICE_HTTP_GATEWAY, ""));
                // Set device type and disable field
                deviceType.setValue(DEVICE_TYPE_HTTP_GATEWAY);
                deviceType.setEnabled(false);
            } else if (choiceLabel.toLowerCase().startsWith(DEVICE_NAME_CAN_SEE)) {
                // Set device address
                deviceAddress.setText(choiceValue);
                // Disable manual address selection
                deviceAddress.setEnabled(false);
                // Set device type and disable field
                deviceType.setValue(DEVICE_TYPE_CAN_SEE);
                deviceType.setEnabled(false);
                // Set ISOTP to false
                useIsotp.setChecked(false);
            } else {
                // Set device address
                deviceAddress.setText(choiceValue);
                // Disable manual address selection
                deviceAddress.setEnabled(false);
                // Set device type to ELM by default and disable field
                deviceType.setValue(DEVICE_TYPE_ELM_327);
                deviceType.setEnabled(false);
                // Set ISOTP to true
                useIsotp.setChecked(true);
            }
        }

        private void tryTofillDeviceList() {
            // get the bluetooth adapter
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                MainActivity.toast(MainActivity.TOAST_NONE, R.string.toast_NoBluetooth);
            } else {
                if (!bluetoothAdapter.isEnabled()) {
                    // launch the system activity
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, MainActivity.REQUEST_ENABLE_BT);
                }
            }
            fillDeviceList(); // if no BT, still allow the http devices
        }

        private void fillDeviceList() {
            // Get current device info
            String deviceAddress = settings.getString(SETTING_DEVICE_ADDRESS, null);
            String deviceName = settings.getString(SETTING_DEVICE_NAME, "");
            MainActivity.debug("SELECT: deviceAddress = " + deviceAddress);
            MainActivity.debug("SELECT: deviceName = " + deviceName);

            // Create labels and values arrays
            List<CharSequence> listLabels = new ArrayList<>();
            List<CharSequence> listValues = new ArrayList<>();

            int selectedIndex = -1;
            //int i = 0; // activateing the selection is now done in loadDeviceSettings

            // Get the bluetooth adapter
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null) {
                // get the devices
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                // if there are paired devices
                if (pairedDevices.size() > 0) {
                    // loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                        // add the name and address to an array adapter to show in a ListView
                        // see https://stackoverflow.com/questions/20658142/getting-the-renamed-name-of-an-android-bluetoothdevice
                        String deviceAlias = device.getName();
                        try {
                            // getAliasName is preferred as it returns the user naming. This simplifies
                            // identification if having several dongles, so you can name them "KONNWEI", "blue"
                            // etcetera in Android's Bluetooth settings.
                            // this will lint warning as getAliasName has @hide set.
                            Method method = device.getClass().getMethod("getAliasName");
                            // getMethod is supposed never to return null, but raise an exception instead
                            //if (method != null) {
                            deviceAlias = (String) method.invoke(device);
                            //}
                        } catch (Exception e) {
                            // do nothing. Trapping here is no problem, as we already have the name
                        }

                        listLabels.add(deviceAlias + "\n" + device.getAddress());
                        listValues.add(device.getAddress());

                        // Set selected index if the device equals the one in the settings
                        if (deviceAlias != null && deviceAlias.equals(deviceName)) {
                            //selectedIndex = i; // plus one as HTTP is always first in list
                            //MainActivity.debug("SELECT: found = "+i+" ("+deviceAlias+")");
                        }
                        //i++;
                    }

                }
            }

            // Add a static entry for the HTTP Gateway
            listLabels.add("HTTP Gateway\n-");
            listValues.add(DEVICE_TYPE_HTTP_GATEWAY);

            //if ("HTTP Gateway".equals(deviceName))
            //    selectedIndex = i;

            // Map the labels/values to the list
            ListPreference devicesList = (ListPreference) findPreference(SETTING_DEVICE_CHOICE);
            devicesList.setEntries(listLabels.toArray(new CharSequence[listLabels.size()]));
            devicesList.setEntryValues(listValues.toArray(new CharSequence[listValues.size()]));

            // Select the actual device
            //devicesList.setValueIndex(selectedIndex == -1 ? i : selectedIndex);
        }

        public void fillStartupActivityList() {
            // Get the ListPreference
            ListPreference startupActivities = (ListPreference) findPreference(SETTING_DISPLAY_STARTUP_ACTIVITY);

            // Create labels and values arrays
            List<CharSequence> listLabels = new ArrayList<>();
            List<CharSequence> listValues = new ArrayList<>();

            // Iterate over the different activity categories and add the list items
            for (String menu : new String[]{"MAIN", "TECHNICAL", "EXPERIMENTAL", "CUSTOM"}) {
                // Add the menu item
                try {
                    int resId = Utils.getResId("menu_" + Utils.capitalizeString(menu), R.string.class);
                    listValues.add("m" + MainActivity.class.getField("MENU_" + menu).get(null));
                    listLabels.add("-- " + getString(resId) + " --");
                } catch (NoSuchFieldException | SecurityException | IllegalAccessException | Resources.NotFoundException e) {
                    // Fail silently as we won't add the menu if the field does not exist
                }

                // Add activity items from the menu
                int[] activityCategory;
                try {
                    activityCategory = (int[]) ActivityRegistry.class.getField("ACTIVITIES_" + menu).get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // If the activity group is not found, go to the next
                    continue;
                }

                for (int i : activityCategory) {
                    Activity activity = ActivityRegistry.getInstance().getById(i);
                    listLabels.add(activity.getTitle());
                    listValues.add(String.valueOf(i));
                }
            }

            // Load values
            startupActivities.setEntries(listLabels.toArray(new CharSequence[listLabels.size()]));
            startupActivities.setEntryValues(listValues.toArray(new CharSequence[listValues.size()]));

            // Set selected value
            int selectedStartupActivity = settings.getInt(SETTING_DISPLAY_STARTUP_ACTIVITY, -1);
            int selectedStartupMenu = settings.getInt(SETTING_DISPLAY_STARTUP_MENU, -1);
            if (selectedStartupMenu > -1) {
                startupActivities.setValue("m" + selectedStartupMenu);
            } else if (selectedStartupActivity > -1) {
                startupActivities.setValue(String.valueOf(selectedStartupActivity));
            } else {
                startupActivities.setValueIndex(0);
            }
        }

        public void saveSettings() {
            // double check if settings is set. We've seen crashes on that
            if (settings == null) return;

            // Using the fragment, all visible settings are automatically saved, we need to save
            // the "meta" (without View) settings
            SharedPreferences.Editor editor = settings.edit();
            ListPreference devicesList = (ListPreference) findPreference(SETTING_DEVICE_CHOICE);
            if (devicesList != null && devicesList.getEntry() != null) {
                String device = devicesList.getEntry().toString();
                editor.putString(SETTING_DEVICE_NAME, device);
                if (device.toLowerCase().startsWith(DEVICE_NAME_HTTP_GATEWAY)) {
                    EditTextPreference url = findPreference(SETTING_DEVICE_ADDRESS);
                    editor.putString(SETTING_DEVICE_HTTP_GATEWAY, url.getText().replace ("http:", "https:"));
                }
            }

            // As we use ListPreference that only accept string, we have to manually save some
            // non-string settings

            saveCarSettings(editor);
            saveDisplaySettings(editor);

            // Remove the visible "meta" fields from the settings as they are not needed there
            editor.remove(SETTING_DEVICE_CHOICE);

            // Save to file
            editor.apply();
        }

        private void setSwitchPreferenceStatus(SwitchPreference preference, String setting,
                                               boolean status)
        {
            SharedPreferences.Editor editor = settings.edit();
            preference.setChecked(status);
            editor.putBoolean(setting, status).apply();
        }
    }
}
