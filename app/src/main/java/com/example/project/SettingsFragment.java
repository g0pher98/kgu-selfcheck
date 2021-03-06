package com.example.project;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class SettingsFragment extends PreferenceFragment {
    Activity activity;
    SharedPreferences prefs;
    SelfCheck selfCheck;

    Preference resetPreference;
    SwitchPreference brightPreference;
    SwitchPreference simplePreference;
    ListPreference listPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

        addPreferencesFromResource(R.xml.settings);
        resetPreference = (Preference) findPreference("reset");
        brightPreference = (SwitchPreference) findPreference("bright");
        simplePreference = (SwitchPreference) findPreference("simple");
        listPreference = (ListPreference) findPreference("barcodeType");

        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        selfCheck = new SelfCheck(activity);

        brightPreference.setChecked(selfCheck.isAutoBright);
        simplePreference.setChecked(selfCheck.simpleMode);
        listPreference.setValueIndex(selfCheck.defaultBarcodeType-1);

        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }
    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            String msg = "";
            if (s.equals("simple")){
                boolean check = prefs.getBoolean("simple",false);
                selfCheck.simpleMode = check;
                selfCheck.saveData();

                if (check) {
                    msg = "??????????????? ?????????????????????.";
                } else {
                    msg = "??????????????? ?????????????????????.";
                }
            } else if (s.equals("bright")){
                boolean bright = prefs.getBoolean("bright",false);
                selfCheck.isAutoBright = bright;
                selfCheck.saveData();

                if (bright) {
                    msg = "????????? ?????? ??? ?????? ????????? ???????????????.";
                } else {
                    msg = "?????? ?????? ????????? ???????????? ????????????.";
                }
            } else if (s.equals("barcodeType")) {
                String barcodeType = prefs.getString("barcodeType","????????? ?????????");
                int type = 1;
                if (barcodeType.equals("QR??????")) {
                    type = 2;
                }

                selfCheck.defaultBarcodeType = type;
                selfCheck.saveData();

                if (type == 1) {
                    msg = "????????? ???????????? ???????????? ?????????????????????.";
                } else {
                    msg = "QR????????? ???????????? ?????????????????????.";
                }
            }
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference){
        String s = preference.getKey();
        if(s.equals("reset")){
            AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(activity);
            // alert??? title??? Messege ??????
            myAlertBuilder.setTitle("????????? ?????????");
            myAlertBuilder.setMessage("?????? ????????????????????????????");
            // ?????? ?????? (Ok ????????? Cancle ?????? )
            myAlertBuilder.setPositiveButton("?????????",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    // OK ????????? ????????? ??????
                    selfCheck.reset();
                    Intent intent = selfCheck.toIntent(MainActivity.class);
                    startActivity(intent);
                    activity.finish();
                }
            });
            myAlertBuilder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Cancle ????????? ????????? ??????
                }
            });
            AlertDialog dialog = myAlertBuilder.create();
            dialog.show();



        }
        return super.onPreferenceTreeClick(preferenceScreen,preference);
    }
}
