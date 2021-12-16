package com.example.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }
    @Override
    public void onBackPressed() {
        SelfCheck selfCheck = new SelfCheck(getApplicationContext());
        if (selfCheck.studentID.equals("")) {
            // 기존 데이터 없을 경우
            Intent intent = selfCheck.toIntent(StudentIdActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (selfCheck.isCheckToday()) {
                // 금일 문진이 기진행된 상황. 바코드 출력
                Intent intent = selfCheck.toIntent(BarcodeActivity.class);
                startActivity(intent);
                finish();
            } else {
                // 금일 문진이 미진행된 상황. 전자문진 시작
                Intent intent = selfCheck.toIntent(CheckingActivity.class);
                startActivity(intent);
                finish();
            }
        }
        super.onBackPressed();
    }
}