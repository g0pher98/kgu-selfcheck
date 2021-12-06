package com.example.project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class BarcodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * 전자문진을 완료한 사용자에게 바코드를 출력.
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 화면 밝기 설정
        WindowManager.LayoutParams params = getWindow().getAttributes();
        float brightness = params.screenBrightness;
        params.screenBrightness = 1f;

        ImageView barcodeView = (ImageView) findViewById(R.id.barcode);

        SelfCheck selfCheck = new SelfCheck(getApplicationContext(), getIntent());

        // 내 정보 출력
        TextView myId = (TextView) findViewById(R.id.my_id);
        TextView myName = (TextView) findViewById(R.id.my_name);

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
            submsg.setText("");
        }

        if (!selfCheck.studentID.equals("")) {
            Bitmap studentCode = selfCheck.getStudentCode(selfCheck.studentID, 1);
            barcodeView.setImageBitmap(studentCode);
        } else {
            Toast.makeText(getApplicationContext(), "잘못된 접근입니다. 학번정보가 없습니다", Toast.LENGTH_LONG).show();
        }
    }
}