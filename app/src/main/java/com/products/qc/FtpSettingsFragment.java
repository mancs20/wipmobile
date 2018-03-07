package com.products.qc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FtpSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
//public class FtpSettingsFragment extends PreferenceFragment {
    public static final String KEY_FTP_PREF_ADDRESS = "ftp_pref_address";
    public static final String KEY_FTP_PREF_PORT = "ftp_pref_port";
    public static final String KEY_FTP_PREF_FOLDER = "ftp_pref_folder";
    public static final String KEY_FTP_PREF_USER = "ftp_pref_user";
    public static final String KEY_FTP_PREF_PASSWORD = "ftp_pref_password";
    private static final String FPT_START = "ftp://";
    public static final String PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.ftp_settings);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String[] prefToShow = {KEY_FTP_PREF_ADDRESS, KEY_FTP_PREF_FOLDER, KEY_FTP_PREF_PORT, KEY_FTP_PREF_USER};

        for (String key: prefToShow) {
            Preference addrPref = (Preference) findPreference(key);//preferences key
            addrPref.setSummary(prefs.getString(key,""));
        }

        SharedPreferences.OnSharedPreferenceChangeListener listener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                    }
                };
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference connectionPref = findPreference(key);
        connectionPref.setDefaultValue(sharedPreferences.getString(key, ""));
        if (!key.equals(KEY_FTP_PREF_PASSWORD)) {
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }
        if (key.equals(KEY_FTP_PREF_PORT)){
            boolean validate = true;
            try {
                int portNumber = Integer.parseInt(sharedPreferences.getString(key, ""));
                if (!(portNumber > 0 && portNumber < 65536)){
                    validate = false;
                }
            } catch(NumberFormatException nfe) {
                validate = false;
            }
            if (!validate){
                Toast.makeText(getActivity(), R.string.toast_preference_ftp_port_validation, Toast.LENGTH_SHORT).show();
                saveSharedPreference(sharedPreferences, key);
            }
        }else if (key.equals(KEY_FTP_PREF_ADDRESS)){
            String ftpAdr = sharedPreferences.getString(key, "");
            boolean validate = false;
            if (ftpAdr.startsWith(FPT_START)){
                int id = ftpAdr.indexOf("//");
                String shortFtpAdr = ftpAdr.substring(id+2);
                if ((validateIsIP(shortFtpAdr) || validateIsURL(shortFtpAdr))){
                    validate = true;
                }
            }
            if(!validate){
                Toast.makeText(getActivity(), R.string.toast_preference_ftp_port_validation, Toast.LENGTH_LONG).show();
                saveSharedPreference(sharedPreferences, key);
            }
        }
    }

    public static boolean validateIsIP(final String ip){
        Pattern pattern = Patterns.IP_ADDRESS;
        //Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    public static boolean validateIsURL(final String ip){
        Pattern pattern = Patterns.WEB_URL;
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    public static void saveSharedPreference(SharedPreferences sharedPrefs, String key){
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(key, "");
        editor.commit();
    }
}
