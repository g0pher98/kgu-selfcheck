package com.example.project;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {
    SelfCheck selfCheck;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        selfCheck = new SelfCheck(getApplicationContext());

        // 초기 로딩화면 2초동안 보여주도록 handler 설정.
        Handler hdl = new Handler();
        hdl.postDelayed(new Runnable() {
            @Override
            public void run() {
                route();
            }
        }, 2000);
    }
    @Override
    public void onResume() {
        super.onResume();
        selfCheck.loadData();
    }

    public void route() {
        /**
         * 기존 데이터 존재유무에 따라 필요한 Activity로 routing 해주는 메소드
         *     - 데이터 없음(초기 실행) -> studentID (학번조회 및 등록)
         *     - 데이터 있음(기존 유저) -> checking (전자문진 진행)
         *     - 금일 최신화(문진 완료) -> barcode (바코드 출력)
         */
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
    }
}