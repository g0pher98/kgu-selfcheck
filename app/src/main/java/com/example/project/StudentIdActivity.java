package com.example.project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StudentIdActivity extends AppCompatActivity {
    SelfCheck selfCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_id);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        selfCheck = new SelfCheck(getApplicationContext(), getIntent());

        setTopBar();

        TextView b1 = (TextView) findViewById(R.id.find);
        Button b2 = (Button) findViewById(R.id.next);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://kutis.kyonggi.ac.kr/webkutis/view/indexWeb.jsp"));
                startActivity(intent);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = (TextView) findViewById(R.id.studentID);
                String id = textView.getText().toString();


                selfCheck.requestCheckID(id, new FunctionAfterRequest() {
                    @Override
                    public void afterRequest(String response) {
                        // 요청에 문제가 있는 경우
                        if (response.contains("비정상 접근입니다")) {
                            Toast.makeText(getApplicationContext(), "조회할 수 없습니다. 관리자에게 문의하세요.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // 학번조회 실패여부 확인
                        if (response.contains("KUTIS 학번(or 사번)을(를) 조회 할 수 없습니다")) {
                            Toast.makeText(getApplicationContext(), "조회되지 않는 학번(or 사번)입니다.", Toast.LENGTH_LONG).show();
                            textView.requestFocus();
                            return;
                        }

                        // parse name
                        String name = null;
                        try {
                            Pattern pattern = Pattern.compile("<input type=\"text\" name=\"userName\" id=\"userName\" class=\"user\" value=\"(.*)\" size=\"20\" readonly>");
                            Matcher matcher = pattern.matcher(response);
                            matcher.find();
                            name = matcher.group(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (name == null) {
                            Toast.makeText(getApplicationContext(), "학번(or 사번)과 관련된 정보를 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
                            textView.requestFocus();
                            return;
                        }

                        AlertDialog.Builder myAlertBuilder =
                                new AlertDialog.Builder(StudentIdActivity.this);
                        // alert의 title과 Messege 세팅
                        myAlertBuilder.setTitle("학번 확인");
                        myAlertBuilder.setMessage(name+" 님이 맞으십니까?");
                        // 버튼 추가 (Ok 버튼과 Cancle 버튼 )
                        String finalName = name;
                        myAlertBuilder.setPositiveButton("일치",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                // OK 버튼을 눌렸을 경우
                                selfCheck.studentID = id;
                                selfCheck.name = finalName;
                                selfCheck.saveData();

                                Intent intent = selfCheck.toIntent(CheckingActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        myAlertBuilder.setNegativeButton("불일치", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancle 버튼을 눌렸을 경우
                                textView.setText("");
                                textView.requestFocus();
                            }
                        });
                        AlertDialog dialog = myAlertBuilder.create();
                        dialog.show();
                    }
                });
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        selfCheck.loadData();
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
            }
        });

        if (selfCheck.studentID.equals("")) {
            myId.setTextColor(Color.parseColor("#dddddd"));
        } else { myId.setText(selfCheck.studentID); }

        if (selfCheck.name.equals("")) {
            myName.setTextColor(Color.parseColor("#dddddd"));
        } else { myName.setText(selfCheck.name); }
    }
}