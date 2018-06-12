package com.example.hsl95.accountbookapi.Activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.hsl95.accountbookapi.Etc.TableHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

/**
 * Created by cosmoslab on 2017-11-18.
 */

public class DataBaseActivity extends AppCompatActivity  {

    private TableView<String[]>  tb;
    TableHelper tableHelper;

    DBHelper dbHelper;

    //private EditText etId;
    private EditText etCard;
    private EditText etDate;
    private EditText etMoney;
    private EditText etPlace;

    private Button insertCardInfoBtn;
    private Button deleteCardInfoBtn;
    private Button updateCardInfoBtn;
    private Button selectCardInfoBtn;

    //EditText입력 완료시 키보드를 내리기 위해 선언
    private InputMethodManager iMM;


    String card;
    String date;
    int money;
    String place;

    private static String URL = "http://172.30.1.26/AccountBookApi/dbsend.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        getSupportActionBar().hide();

        //dbHelper
        dbHelper = new DBHelper(getApplicationContext(), "ACCOUNT.db", null, 1);

        //tableHelper
        tableHelper=new TableHelper(this);

        //Intent k = getIntent();//broadcast Intent

        //etId = (EditText) findViewById(R.id.ID);
        etCard = (EditText) findViewById(R.id.card);
        etDate = (EditText) findViewById(R.id.date);
        etMoney = (EditText) findViewById(R.id.money);
        etPlace = (EditText) findViewById(R.id.place);

        insertCardInfoBtn = (Button) findViewById(R.id.insert);
        deleteCardInfoBtn = (Button) findViewById(R.id.delete);
        updateCardInfoBtn = (Button) findViewById(R.id.update);
        selectCardInfoBtn = (Button) findViewById(R.id.select);

        iMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);



        //set Date
        setDate();

        //Select Table
        selectTable();

        //TABLE CLICK시 장소 보여줌
        tb.addDataClickListener(new TableDataClickListener() {
            @Override
            public void onDataClicked(int rowIndex, Object clickedData) {
                Toast.makeText(DataBaseActivity.this, ((String[])clickedData)[3], Toast.LENGTH_SHORT).show();
            }
        });



        insertCardInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

                getEditText();

                if(card.getBytes().length <= 0||String.valueOf(money).getBytes().length <= 0||place.getBytes().length <= 0){//빈값이 넘어올때의 처리

                    Toast.makeText(DataBaseActivity.this, "값을 입력하세요.",Toast.LENGTH_SHORT).show();
                }else {
                    //Insert
                    dbHelper.insert(card, date, money, place);
                    //reset
                    resetEditext();
                }

                //Select
                selectTable();

                //getSum정보 보냄
                String strGetSum = String.valueOf(dbHelper.getSum());
                sendGetSumInfo(URL,strGetSum);

            }
        });


        updateCardInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

                card = etCard.getText().toString();
                money = Integer.parseInt(etMoney.getText().toString());
                place = etPlace.getText().toString();

                if(card.getBytes().length <= 0||String.valueOf(money).getBytes().length <= 0||place.getBytes().length <= 0){//빈값이 넘어올때의 처리

                    Toast.makeText(DataBaseActivity.this, "값을 입력하세요.",Toast.LENGTH_SHORT).show();
                }else {
                    //수정
                    dbHelper.update(card, money, place);
                    //reset
                    resetEditext();
                }

                //Select
                selectTable();

                //getSum정보 보냄
                String strGetSum = String.valueOf(dbHelper.getSum());
                sendGetSumInfo(URL,strGetSum);

            }
        });



        deleteCardInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

                card = etCard.getText().toString();
                money = Integer.parseInt(etMoney.getText().toString());
                place = etPlace.getText().toString();

                if(card.getBytes().length <= 0||String.valueOf(money).getBytes().length <= 0||place.getBytes().length <= 0){//빈값이 넘어올때의 처리

                    Toast.makeText(DataBaseActivity.this, "값을 입력하세요.",Toast.LENGTH_SHORT).show();
                }else {
                    //삭제
                    dbHelper.delete(card, money, place);
                    //reset
                    resetEditext();

                }

                //Select
                selectTable();

                //getSum정보 보냄
                String strGetSum = String.valueOf(dbHelper.getSum());
                sendGetSumInfo(URL,strGetSum);

            }
        });


        selectCardInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Select
                selectTable();

                //reset
                resetEditext();
            }
        });

    }




    ///////////////////////////////Method//////////////////////////////////

    public void getEditText(){
        card = etCard.getText().toString();
        date = etDate.getText().toString();
        money = Integer.parseInt(etMoney.getText().toString());
        place = etPlace.getText().toString();
    }

    public void setDate(){

        long currentDate = System.currentTimeMillis();
        Date date = new Date(currentDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd");

        etDate.setText(simpleDateFormat.format(date));
    }

    public void selectTable(){

        tb = (TableView<String[]>) findViewById(R.id.tableView);
        tb.setColumnCount(4);
        tb.setHeaderBackgroundColor(Color.parseColor("#00BFA5"));
        tb.setHeaderAdapter(new SimpleTableHeaderAdapter(this,tableHelper.getCardInfoHeaders()));
        tb.setDataAdapter(new SimpleTableDataAdapter(this, tableHelper.getCardProbs()));

    }

    public void resetEditext(){

        etCard.setText("");
        etMoney.setText("");
        etPlace.setText("");

    }

    private void hideKeyboard() {

        iMM.hideSoftInputFromWindow(etCard.getWindowToken(), 0);
        iMM.hideSoftInputFromWindow(etMoney.getWindowToken(), 0);
        iMM.hideSoftInputFromWindow(etPlace.getWindowToken(), 0);
    }


    //getSum값을 보냄
    private void sendGetSumInfo(String Url, final String getSum) {

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

                param.put("sos", getSum);

                return param;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }


}
