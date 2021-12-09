package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SettingsFragment extends PreferenceFragment {

    SharedPreferences prefs;
    SelfCheck selfCheck;

    Preference resetPreference;
    Preference mainPreference;
    SwitchPreference simplePreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        resetPreference = (Preference) findPreference("reset");
        mainPreference = (Preference) findPreference("main");
        simplePreference = (SwitchPreference) findPreference("simple");

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        selfCheck = new SelfCheck(getActivity());

        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }
    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if(s.equals("simple")){
                boolean check = prefs.getBoolean("simple",false);
                selfCheck.simpleMode = check;
                selfCheck.saveData();
            }
            if(s.equals("bright")){
                Toast.makeText(getActivity(), Boolean.toString(prefs.getBoolean("bright",false)), Toast.LENGTH_LONG).show();
            }
        }
    };
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference){
        String s = preference.getKey();
        if(s.equals("reset")){
            SelfCheck selfCheck = new SelfCheck(getActivity());
            selfCheck.reset();
            Intent intent = selfCheck.toIntent(MainActivity.class);
            startActivity(intent);
        }
        return super.onPreferenceTreeClick(preferenceScreen,preference);
    }
}
