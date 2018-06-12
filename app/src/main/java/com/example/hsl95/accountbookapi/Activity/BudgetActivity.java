package com.example.hsl95.accountbookapi.Activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hsl95.accountbookapi.Etc.DBHelper;
import com.example.hsl95.accountbookapi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.hsl95.accountbookapi.Etc.IpHelper.getLocalIpAddress;

/**
 * Created by cosmoslab on 2017-11-20.
 */

public class BudgetActivity extends AppCompatActivity {

    private EditText etBudget;//예산
    private TextView tvBudget;//예산
    private TextView tvReamin;//설정예산 - 사용금액
    private Button setBudgetBtn;

    private DBHelper dbHelper;

    //EditText입력 완료시 키보드를 내리기 위해 선언
    private InputMethodManager iMM;


    int sumOfSpend;//dbHelper로 부터 지출금액 가져옴
    int getBudget;//tvBudget
    int getReamin;//tvReamin
    String strGetBudget;
    String strSumOfSpend;

    private static String URL = "http://172.30.1.26/AccountBookApi/send.php";
    private static String URL2 = "http://172.30.1.26/AccountBookApi/response.php";

    /*private static String URL = "http://"+getLocalIpAddress()+"/AccountBookApi/send.php";
    private static String URL2 = "http://"+getLocalIpAddress()+"/AccountBookApi/response.php";
*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        getSupportActionBar().hide();

        //Intent j = getIntent();//메인에서 intent받음

        //DBHelper
        dbHelper =  new DBHelper(getApplicationContext(), "ACCOUNT.db", null, 1);

        etBudget = (EditText) findViewById(R.id.budget);
        tvBudget = (TextView)findViewById(R.id.budget_textview3);
        tvReamin = (TextView)findViewById(R.id.budget_textview5);
        setBudgetBtn = (Button)findViewById(R.id.setBudgetBtn);

        iMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //받음
        responseBudgetInfo(URL2);



        setBudgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();
                strGetBudget = etBudget.getText().toString();

                if(strGetBudget.getBytes().length <= 0){//빈값이 넘어올때의 처리
                    Toast.makeText(BudgetActivity.this, "값을 입력하세요.",Toast.LENGTH_SHORT).show();

                }else {
                    sendBudgetInfo(URL, strGetBudget);
                    responseBudgetInfo(URL2);
                    etBudget.setText("");
                }
            }
        });

    }




    ///////////////////////////////Method//////////////////////////////////

    private void hideKeyboard() {

        iMM.hideSoftInputFromWindow(etBudget.getWindowToken(), 0);
    }



    //보내기만 함
    private void sendBudgetInfo(String Url, final String budget) {

        RequestQueue requestQueue = new Volley().newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }
        })

        {
            protected Map<String, String> getParams() {

                Map<String, String> param = new HashMap<String, String>();

                param.put("bd", budget);

                return param;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }




    //받기만 함
    private void responseBudgetInfo(String Url) {

        strSumOfSpend = null;
        strGetBudget = null;

        RequestQueue requestQueue = new Volley().newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {



                try {
                    //Json 형태로 받음
                    JSONObject jsonObject = new JSONObject(response);


                        if (jsonObject.getInt("success") == 1) {

                            JSONObject jsonObjectInfo = jsonObject.getJSONObject("Money");
                            strSumOfSpend = jsonObjectInfo.getString("sumofspend");
                            strGetBudget = jsonObjectInfo.getString("budget");

                        //Textview에 셋팅
                        int remain = Integer.parseInt(strGetBudget) - Integer.parseInt(strSumOfSpend);

                        tvBudget.setText(strGetBudget);
                        tvReamin.setText(String.valueOf(remain));

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }
        });


        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);

    }


}



























