package com.example.project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CheckingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Button submitButton = (Button) findViewById(R.id.submit_selfcheck);
        RadioGroup on1 = (RadioGroup) findViewById(R.id.on1);
        RadioGroup on2 = (RadioGroup) findViewById(R.id.on2);
        RadioGroup on3 = (RadioGroup) findViewById(R.id.on3);

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
        
        // 간편 문진 모드가 켜져있는 경우 초기 설정
        if (selfCheck.simpleMode) {
            RadioButton button1 = (RadioButton) findViewById(R.id.on11);
            RadioButton button2 = (RadioButton) findViewById(R.id.on21);
            RadioButton button3 = (RadioButton) findViewById(R.id.on31);
            button1.setChecked(true);
            button2.setChecked(true);
            button3.setChecked(true);
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int chkRadioId1 = on1.getCheckedRadioButtonId();
                int chkRadioId2 = on2.getCheckedRadioButtonId();
                int chkRadioId3 = on3.getCheckedRadioButtonId();

                if (chkRadioId1 == -1 || chkRadioId2 == -1 || chkRadioId3 == -1) {
                    Toast.makeText(getApplicationContext(), "모든 항목을 선택 후 제출해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Boolean chk1 = true, chk2 = true, chk3 = true;
                Boolean isCleanResult = true;

                if (chkRadioId1 != R.id.on11) { chk1 = false; isCleanResult = false; }
                if (chkRadioId2 != R.id.on21) { chk2 = false; isCleanResult = false; }
                if (chkRadioId3 != R.id.on31) { chk3 = false; isCleanResult = false; }

                Boolean finalIsCleanResult = isCleanResult;
                selfCheck.requestSubmitCheckResult(chk1, chk2, chk3, new FunctionAfterRequest() {
                    @Override
                    public void afterRequest(String response) {
                        Log.e(this.toString(), response);

                        selfCheck.lastSubmitDate = selfCheck.todayDate;
                        selfCheck.isLastCheckClean = finalIsCleanResult;

                        selfCheck.saveData();

                        Intent intent = selfCheck.toIntent(BarcodeActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

    }
}

