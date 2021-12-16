package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

interface FunctionAfterRequest {
    /**
     * 웹 요청 이후 동작을 정의하는 interface.
     *
     * Activity에서 응답 수신 이후 로직을 제어할 필요가 있음.
     */
    void afterRequest(String response);
}


public class SelfCheck extends SelfCheckInfo {
    public SelfCheck(Context newContext) {
        context = newContext;
        init();

        loadData();
    }

    public SelfCheck(Context newContext, Intent intent) {
        context = newContext;
        init();
        fromIntent(intent);
    }

    public void requestCheckID(String newStudentID, FunctionAfterRequest funcAfterReq) {
        /**
         * 학번 조회 메소드
         *
         * 존재하지 않는 학번일 경우 Toast, 존재할 경우 이름 확인 후 저장.
         */
        String url = reqHost + "/webkutis/visitors/chkCert.jsp";

        Map<String, String> params = new HashMap<String, String>();
        params.put("cert_no", "");
        params.put("visit_date", dateFormat.format(todayDate));
        params.put("cert_gb", "1");
        params.put("id", newStudentID);

        requestFunction(url, params, funcAfterReq);
    }

    public void requestSubmitCheckResult(boolean[] result, FunctionAfterRequest funcAfterReq) {
        /**
         * 전자문진 결과를 전송하는 메소드
         */
        String url = reqHost + "/webkutis/visitors/selfChkSave.jsp";

        Map<String, String> params = new HashMap<String, String>();
        params.put("cert_gb", "1");
        params.put("visit_date", dateFormat.format(todayDate));
        params.put("id", studentID);
        params.put("check_gb", "2");
        params.put("answer010" + (result[0] ? "2":"1"), "on");
        params.put("answer020" + (result[1] ? "2":"1"), "on");
        params.put("answer030" + (result[2] ? "2":"1"), "on");

        requestFunction(url, params, funcAfterReq);
    }



    public void requestFunction(String url, Map<String, String> newParams, FunctionAfterRequest funcAfterReq) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        funcAfterReq.afterRequest(response);
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = newParams;
                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
    }
}
