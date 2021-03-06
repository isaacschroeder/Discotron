package isaacjschroeder.discotron;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

//will work kind of like ObjectBox (as a global singleton) but for shared preferences
public class SharedPreferencesManager {

    private static SharedPreferences sp;

    //Keywords:
    public static final String GAME_IN_PROGRESS_ID = "GAME_IN_PROGRESS_ID";
    //public static final String GAME_IN_PROGRESS_CURRENT_HOLE = "GAME_IN_PROGRESS_CURRENT_HOLE";
    //could have player id of which player is the app owner

    public SharedPreferencesManager() {}

    public static void init(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        }
    }

    //Read and writes for different primitive types:
    //fyi default value is what function will return if no matching value found

    public static String read(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }
    public static boolean read(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }
    public static long read(String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = sp.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }
    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = sp.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }
    public static void write(String key, long value) {
        SharedPreferences.Editor prefsEditor = sp.edit();
        prefsEditor.putLong(key, value);
        prefsEditor.commit();
    }

    public static void clearData() {
        SharedPreferences.Editor prefsEditor = sp.edit();
        prefsEditor.clear();
        prefsEditor.commit();
    }
}
