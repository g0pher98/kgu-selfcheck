package com.example.project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BarcodeActivity extends AppCompatActivity {
    SelfCheck selfCheck;
    int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * 전자문진을 완료한 사용자에게 바코드를 출력.
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        selfCheck = new SelfCheck(getApplicationContext(), getIntent());
        mode = selfCheck.defaultBarcodeType;

        setTopBar();

        // 화면 밝기 설정
        if (selfCheck.isAutoBright) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            float brightness = params.screenBrightness;
            params.screenBrightness = 1f;
        }

        ImageView barcodeView = (ImageView) findViewById(R.id.barcode);
        barcodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBarcode(true);
            }
        });

        // 금일 문진 여부에 따른 출력 메세지 변경
        TextView msg    = (TextView) findViewById(R.id.barcode_msg);
        TextView submsg = (TextView) findViewById(R.id.barcode_submsg);

        if (!selfCheck.isCheckToday()) {
            msg.setText("전자문진을 아직 진행하지 않았습니다. 금일 문진을 진행해주세요.");
            submsg.setText("");
            msg.setTextColor(Color.parseColor("#FFAF1B1B"));
        } else if (!selfCheck.isLastCheckClean) {
            msg.setText("현재 귀하의 건강상태는 확인이 필요한 경우로, 금일 학교 방문을 삼가주시기 바랍니다.");
            msg.setTextColor(Color.parseColor("#FFAF1B1B"));
        } else {
            submsg.setText("QR 또는 바코드를 터치해서 모드를 전환하실 수 있습니다.");
        }

        // 최종 바코드 출력부
        if (!selfCheck.studentID.equals("")) {
            showBarcode(false);
        } else {
            Toast.makeText(getApplicationContext(), "잘못된 접근입니다. 학번정보가 없습니다", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        selfCheck.loadData();
        mode = selfCheck.defaultBarcodeType;
        showBarcode(false);
    }

    public void setTopBar() {
        // 상단 View 설정
        TextView myId = (TextView) findViewById(R.id.my_id);
        TextView myName = (TextView) findViewById(R.id.my_name);
        ImageButton btnMenu = (ImageButton) findViewById(R.id.menu);
        btnMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = selfCheck.toIntent(SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (selfCheck.studentID.equals("")) {
            myId.setTextColor(Color.parseColor("#dddddd"));
        } else { myId.setText(selfCheck.studentID); }

        if (selfCheck.name.equals("")) {
            myName.setTextColor(Color.parseColor("#dddddd"));
        } else { myName.setText(selfCheck.name); }
    }

    public void showBarcode(Boolean isChange) {
        ImageView barcode = (ImageView) findViewById(R.id.barcode);

        // mode 변경
        if (isChange != null && isChange)
            mode = (mode != 1)? 1:2;

        // mode에 따른 View 크기 변경
        int width = 500, height = 500;
        if (mode == 1) {
            width = 800;
            height = 300;
        }
        barcode.getLayoutParams().width  = width;
        barcode.getLayoutParams().height = height;
        barcode.requestLayout();

        // 바코드 생성 및 적용
        Bitmap studentCode = selfCheck.getStudentCode(
                selfCheck.studentID, mode,
                width, height
        );
        barcode.setImageBitmap(studentCode);
    }
}