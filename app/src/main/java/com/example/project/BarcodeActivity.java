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

        // 화면 밝기 설정
        // TODO
        //   - 화면 밝기가 액티비티를 벗어나면 원복되는지 확인해야함.
        //   - 설정 변경 후 바로 반영되도록 수정해야함.
        if (selfCheck.isAutoBright) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            float brightness = params.screenBrightness;
            params.screenBrightness = 1f;
        }

        // 상단 View 설정
        TextView myId = (TextView) findViewById(R.id.my_id);
        TextView myName = (TextView) findViewById(R.id.my_name);
        ImageButton btnMenu = (ImageButton) findViewById(R.id.menu);
        btnMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = selfCheck.toIntent(SettingActivity.class);
                startActivity(intent);
            }
        });

        ImageView barcodeView = (ImageView) findViewById(R.id.barcode);

        barcodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode == 1) {
                    mode = 2;
                } else {
                    mode = 1;
                }

                Bitmap studentCode = selfCheck.getStudentCode(
                        selfCheck.studentID, mode, barcodeView.getWidth(), barcodeView.getHeight()
                );
                // TODO QR/바코드에 따라 크기 바뀌도록 수정
                barcodeView.setImageBitmap(studentCode);
            }
        });

        if (selfCheck.studentID.equals("")) {
            myId.setTextColor(Color.parseColor("#dddddd"));
        } else { myId.setText(selfCheck.studentID); }

        if (selfCheck.name.equals("")) {
            myName.setTextColor(Color.parseColor("#dddddd"));
        } else { myName.setText(selfCheck.name); }


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

        if (!selfCheck.studentID.equals("")) {
            Bitmap studentCode = selfCheck.getStudentCode(selfCheck.studentID, mode, );
            barcodeView.setImageBitmap(studentCode);
        } else {
            Toast.makeText(getApplicationContext(), "잘못된 접근입니다. 학번정보가 없습니다", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        selfCheck.loadData();
        mode = selfCheck.defaultBarcodeType;

        ImageView barcodeView = (ImageView) findViewById(R.id.barcode);
        Bitmap studentCode = selfCheck.getStudentCode(selfCheck.studentID, mode);
        barcodeView.setImageBitmap(studentCode);
    }
}