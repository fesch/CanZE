package lu.fisch.canze.classes;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class Crashlytics {
    public static void logException (Exception e) {
        FirebaseCrashlytics.getInstance().recordException(e);
    }

    public static void logString (String e) {
        FirebaseCrashlytics.getInstance().log(e);
    }
}
