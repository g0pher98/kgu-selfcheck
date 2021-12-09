package com.example.project;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO
        //   - 설정 값 각 기능이 실제로도 적용되도록 수정 (간편문진, 자동밝기)
        //   - 설정 페이지 기능 추가. (개발자 소개, 교내 코로나대책본부 전화 등)
        //   - QR코드에서 바코드로 변경 시 좌우로 길게 크기 변경되도록 수정
        //   - 설정 아이콘 크기조절
        //   - 뒤로가기 눌렀을 때, 이전 페이지가 아니라 아예 꺼지도록 수정(액티비티 넘길 때 기존 것은 죽여야할듯)
        //   -


        /**
         * 기존 데이터 존재 여부에 따라 보여지는 Activity가 달라지도록 구성
         *     - 데이터 없음(초기 실행) -> studentID (학번조회 및 등록)
         *     - 데이터 있음(기존 유저) -> checking (전자문진 진행)
         *     - 금일 최신화(문진 완료) -> barcode (바코드 출력)
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        SelfCheck selfCheck = new SelfCheck(getApplicationContext());

        if (selfCheck.studentID.equals("")) {
            // 기존 데이터 없을 경우
            Intent intent = selfCheck.toIntent(StudentIdActivity.class);
            startActivity(intent);
        } else {
            if (selfCheck.isCheckToday()) {
                // 금일 문진이 기진행된 상황. 바코드 출력
                Intent intent = selfCheck.toIntent(BarcodeActivity.class);
                startActivity(intent);
            } else {
                // 금일 문진이 미진행된 상황. 전자문진 시작
                Intent intent = selfCheck.toIntent(CheckingActivity.class);
                startActivity(intent);
            }
        }
    }
}