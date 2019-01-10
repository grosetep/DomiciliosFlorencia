package com.delivery.estrategiamovilmx.domiciliosflorencia.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

/**
 * Created by administrator on 10/07/2017.
 */
public class ApplicationPreferences {

    private static SharedPreferences mPrefs;
    public ApplicationPreferences(){}
    public static void saveLocalPreference(Context context,String propertyName, String propertyValue) {
        if (mPrefs==null)
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(propertyName, propertyValue);
        editor.commit();
    }

    public static String getLocalStringPreference(Context context,String namePreference){
        if (mPrefs == null)
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return mPrefs.getString(namePreference,"");
    }
    public static Set<String> getLocalSetPreference(Context context,String namePreference){
        if (mPrefs == null)
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        return mPrefs.getStringSet(namePreference,null);
    }

    public static void saveLocalSetPreference(Context context, String propertyName, Set<String> propertyValue) {
        if (mPrefs==null)
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putStringSet(propertyName, propertyValue);
        editor.commit();
    }
}
