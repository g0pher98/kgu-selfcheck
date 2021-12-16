package com.example.project;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import org.json.JSONObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class SelfCheckInfo {
    static String saveFileName = null;

    // Basic info
    String studentID = "";
    String name = "";
    Date lastSubmitDate = new Date(0);
    Boolean simpleMode = false; // 빠른 문진 설정 여부
    Boolean isLastCheckClean = true; // 최근 문진이 모두 "예"를 선택했는지.
    Boolean isAutoBright = true; // 바코드 출력 시 자동 밝기조절 여부
    int defaultBarcodeType = 1;

    // Helpful variables
    String reqHost = null;
    Context context = null;
    Resources resources = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault());
    Calendar calendar = Calendar.getInstance();
    Date todayDate = calendar.getTime();
    RequestQueue requestQueue = null;

    public void init() {
        /**
         * Context를 받은 이후에 초기화 할 수 있는 변수들을 초기화하는 메소드
         */
        resources = context.getResources();
        reqHost = resources.getString(R.string.req_host);
        saveFileName = resources.getString(R.string.save_filename);
        requestQueue = Volley.newRequestQueue(context);
    }

    public void reset() {
        /**
         * 저장된 정보를 모두 초기화 하는 메소드
         */

        // save 파일 삭제
        File dir = context.getFilesDir();
        File file = new File(dir, saveFileName);
        boolean deleted = file.delete();

        // 기본정보 초기화
        studentID = "";
        name = "";
        lastSubmitDate = new Date(0);
        isLastCheckClean = true;
        simpleMode = false;
        isAutoBright = true;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("studentID"               , studentID);
            json.put("name"                    , name);
            json.put("lastSubmitDate"          , dateFormat.format(lastSubmitDate));
            json.put("simpleMode"              , simpleMode);
            json.put("isLastCheckClean"        , isLastCheckClean);
            json.put("isAutoBright"            , isAutoBright);
            json.put("defaultBarcodeType"      , defaultBarcodeType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    public void fromJSONString(String raw) {
        try {
            JSONObject json = new JSONObject(raw);

            studentID              = json.getString("studentID");
            name                   = json.getString("name");
            lastSubmitDate         = dateFormat.parse(json.getString("lastSubmitDate"));
            simpleMode             = json.getBoolean("simpleMode");
            isLastCheckClean       = json.getBoolean("isLastCheckClean");
            isAutoBright           = json.getBoolean("isAutoBright");
            defaultBarcodeType     = json.getInt("defaultBarcodeType");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        /**
         저장된 JSON 파일에서 기존 데이터를 불러오는 메소드
         */

        FileInputStream fis = null;
        DataInputStream dis = null;
        JSONObject data = null;
        String raw = null;

        try {
            fis = context.openFileInput(saveFileName);
            dis = new DataInputStream(fis);
            raw = dis.readUTF();

            dis.close();
            fis.close();
        } catch (FileNotFoundException e) {
            saveData();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        fromJSONString(raw);
    }

    public void saveData() {
        /**
         * 현재 데이터를 JSON 파일에 저장하는 메소드
         */
        JSONObject data = toJSON();

        // JSON 데이터를 파일에 저장
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(saveFileName, context.MODE_PRIVATE);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeUTF(data.toString());

            dos.flush();
            dos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Intent toIntent(Class c) {
        /**
         * 현재 데이터를 포함하는 intent를 생성하는 메소드
         */
        Intent intent = new Intent(context, c);
        intent.putExtra("studentID"             , studentID);
        intent.putExtra("name"                  , name);
        intent.putExtra("lastSubmitDate"        , lastSubmitDate);
        intent.putExtra("simpleMode"            , simpleMode);
        intent.putExtra("isLastCheckClean"      , isLastCheckClean);
        intent.putExtra("isAutoBright"          , isAutoBright);
        intent.putExtra("defaultBarcodeType"    , defaultBarcodeType);

        return intent;
    }

    public void fromIntent(Intent intent) {
        /**
         * intent로 전달된 데이터를 복원하는 메소드
         */
        studentID              = intent.getStringExtra("studentID");
        name                   = intent.getStringExtra("name");
        lastSubmitDate         = (Date) intent.getSerializableExtra("lastSubmitDate");
        simpleMode             = intent.getBooleanExtra("simpleMode", false);
        isLastCheckClean       = intent.getBooleanExtra("isLastCheckClean", true);
        isAutoBright           = intent.getBooleanExtra("isAutoBright", true);
        defaultBarcodeType     = intent.getIntExtra("defaultBarcodeType", 1);
    }

    public Boolean isCheckToday() {
        /**
         * 금인 문진 여부를 체크하는 메소드
         */
        return dateFormat.format(todayDate)
                .equals(dateFormat.format(lastSubmitDate));
    }

    public Bitmap getStudentCode(String studentID, Integer newFormat, Integer newWidth, Integer newHeight) {
        /**
         * 바코드 또는 QR코드 생성 메소드
         *     - mode 1 : barcode
         *     - mode 2 : QRcode
         */
        Integer width=500, height=500;

        if (newWidth != null)  width = newWidth;
        if (newHeight != null) height = newHeight;

        if (newFormat == null) {
            newFormat = defaultBarcodeType;
        }

        String data = studentID + "1";
        BarcodeFormat format = null;

        if (newFormat == 1) {
            format = BarcodeFormat.CODE_128;
        } else if (newFormat == 2) {
            format = BarcodeFormat.QR_CODE;

        } else {
            format = BarcodeFormat.CODE_128; // 기본적으로 QR코드 출력
        }

        // QR코드 생성부
        BarcodeEncoder bEncoder = new BarcodeEncoder();
        Bitmap bitmap = null;

        try {
            bitmap = bEncoder.encodeBitmap(data, format, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}