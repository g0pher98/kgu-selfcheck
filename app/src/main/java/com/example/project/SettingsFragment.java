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
                    msg = "간편모드가 설정되었습니다.";
                } else {
                    msg = "기존모드로 복구되었습니다.";
                }
            } else if (s.equals("bright")){
                boolean bright = prefs.getBoolean("bright",false);
                selfCheck.isAutoBright = bright;
                selfCheck.saveData();

                if (bright) {
                    msg = "바코드 출력 시 화면 밝기가 조정됩니다.";
                } else {
                    msg = "이제 화면 밝기를 제어하지 않습니다.";
                }
            } else if (s.equals("barcodeType")) {
                String barcodeType = prefs.getString("barcodeType","바코드 기본형");
                int type = 1;
                if (barcodeType.equals("QR코드")) {
                    type = 2;
                }

                selfCheck.defaultBarcodeType = type;
                selfCheck.saveData();

                if (type == 1) {
                    msg = "바코드 기본형이 기본으로 설정되었습니다.";
                } else {
                    msg = "QR코드가 기본으로 설정되었습니다.";
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
            // alert의 title과 Messege 세팅
            myAlertBuilder.setTitle("데이터 초기화");
            myAlertBuilder.setMessage("정말 초기화하시겠습니까?");
            // 버튼 추가 (Ok 버튼과 Cancle 버튼 )
            myAlertBuilder.setPositiveButton("초기화",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    // OK 버튼을 눌렸을 경우
                    selfCheck.reset();
                    Intent intent = selfCheck.toIntent(MainActivity.class);
                    startActivity(intent);
                    activity.finish();
                }
            });
            myAlertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Cancle 버튼을 눌렸을 경우
                }
            });
            AlertDialog dialog = myAlertBuilder.create();
            dialog.show();



        }
        return super.onPreferenceTreeClick(preferenceScreen,preference);
    }
}
