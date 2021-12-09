package com.example.project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CheckingActivity extends AppCompatActivity {
    SelfCheck selfCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        selfCheck = new SelfCheck(getApplicationContext(), getIntent());

        Button submitButton = (Button) findViewById(R.id.submit_selfcheck);
        RadioGroup on1 = (RadioGroup) findViewById(R.id.on1);
        RadioGroup on2 = (RadioGroup) findViewById(R.id.on2);
        RadioGroup on3 = (RadioGroup) findViewById(R.id.on3);

        ImageButton b = (ImageButton) findViewById(R.id.menu);
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = selfCheck.toIntent(SettingActivity.class);
                startActivity(intent);
            }
        });

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
        runSimpleMode(selfCheck.simpleMode);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean[] chkResult = getCheckedResult();
                boolean isCleanResult = true;

                if (chkResult == null) return;
                if (chkResult[0] || chkResult[1] || chkResult[2]) isCleanResult = false;

                Boolean finalIsCleanResult = isCleanResult;
                selfCheck.requestSubmitCheckResult(chkResult, new FunctionAfterRequest() {
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
    @Override
    public void onResume() {
        super.onResume();
        selfCheck.loadData();
        runSimpleMode(selfCheck.simpleMode);
    }

    public boolean[] getCheckedResult() {
        boolean[] result = {false, false, false};

        RadioGroup on1 = (RadioGroup) findViewById(R.id.on1);
        RadioGroup on2 = (RadioGroup) findViewById(R.id.on2);
        RadioGroup on3 = (RadioGroup) findViewById(R.id.on3);

        int chkRadioId1 = on1.getCheckedRadioButtonId();
        int chkRadioId2 = on2.getCheckedRadioButtonId();
        int chkRadioId3 = on3.getCheckedRadioButtonId();

        if (chkRadioId1 == -1 || chkRadioId2 == -1 || chkRadioId3 == -1) {
            Toast.makeText(getApplicationContext(), "모든 항목을 선택 후 제출해주세요.", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (chkRadioId1 == R.id.on11) { result[0] = true; }
        if (chkRadioId2 == R.id.on21) { result[1] = true; }
        if (chkRadioId3 == R.id.on31) { result[2] = true; }

        return result;
    }

    public void runSimpleMode(boolean isOn) {
        RadioButton btn1 = (RadioButton) findViewById(R.id.on12);
        RadioButton btn2 = (RadioButton) findViewById(R.id.on22);
        RadioButton btn3 = (RadioButton) findViewById(R.id.on32);
        btn1.setChecked(isOn);
        btn2.setChecked(isOn);
        btn3.setChecked(isOn);
    }
}

