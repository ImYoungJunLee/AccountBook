package com.example.hsl95.accountbookapi.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.hsl95.accountbookapi.Etc.DBHelper;
import com.example.hsl95.accountbookapi.R;

import java.util.Calendar;

public class MainActivity extends Activity {

    private Activity mainActivity = this;

    private TextView monthSpendTv;//이번 달 지출 setfinal
    private TextView monthTermTv;//해당월의 기간 나타냄

    private Button incomeBtn;
    private Button budgetBtn;
    private Button databaseBtn;

    int sumOfSpend;


    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionReadSms = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_SMS);
        int permissionReceiveSms = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECEIVE_SMS);
        if(permissionReadSms == PackageManager.PERMISSION_DENIED || permissionReceiveSms == PackageManager.PERMISSION_DENIED ){
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS}, 2);
        }else {

        }


        //문자읽기
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");

        Intent k = getIntent();


        incomeBtn = (Button) findViewById(R.id.incomeBtn);//IncomeActivity class 이동
        budgetBtn = (Button) findViewById(R.id.budgetBtn);//BudgetActivity class 이동
        databaseBtn = (Button) findViewById(R.id.databaseBtn);//DB class 이동


        incomeBtn.setOnClickListener(new View.OnClickListener() {//Button을 눌렀을 때 incomeBtn 액티비티로 이동,데이터까지 넘겨주기
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, IncomeActivity.class);
                startActivity(intent);

            }
        });

        budgetBtn.setOnClickListener(new View.OnClickListener() {//Button을 눌렀을 때 budgetBtn 액티비티로 이동,데이터까지 넘겨주기
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
                startActivity(intent);

            }
        });

        databaseBtn.setOnClickListener(new View.OnClickListener() {//Button을 눌렀을 때 databaseBtn 액티비티로 이동,데이터까지 넘겨주기
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, DataBaseActivity.class);
                startActivity(intent);

            }
        });


        //MainActivity 에 그달의 첫째날~마지막날 기간 보여줌.
        showMonthTerm();

        //Db의 지출의 합을 세팅
        dbHelper = new DBHelper(getApplicationContext(), "ACCOUNT.db", null, 1);
        getSumOfSpend(dbHelper);

    }




    ///////////////////////////////Method//////////////////////////////////

    @Override
    public void onStart() {
        super.onStart();

        dbHelper = new DBHelper(getApplicationContext(), "ACCOUNT.db", null, 1);
        getSumOfSpend(dbHelper);
    }

    //이번달 지출의 합을 가져옴
    public void getSumOfSpend(DBHelper dbHelper) {

        sumOfSpend =dbHelper.getSum();

        monthSpendTv = (TextView) findViewById(R.id.main_textview3);
        monthSpendTv.setText("");
        monthSpendTv.setText(String.valueOf(sumOfSpend));

    }

    //날짜 가져옴
    public void showMonthTerm(){

        Calendar cal = Calendar.getInstance();

        //첫째~마지막 날짜
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);
        int date=cal.get(Calendar.DATE);

        cal.set(year, month, date);

        //해당월의 1일
        String monthFirst;
        monthFirst = String.valueOf(cal.get(Calendar.MONTH)+1) + ".";
        monthFirst += String.valueOf(cal.getActualMinimum(Calendar.DATE));

        //해당월의 말일
        String monthLast;
        monthLast = String.valueOf(cal.get(Calendar.MONTH)+1) + ".";
        monthLast += String.valueOf(cal.getActualMaximum(Calendar.DATE));

        //Textview에 기간 나타내기
        monthTermTv = (TextView) findViewById(R.id.monthTermTv);
        monthTermTv.setText("("+monthFirst+" ~ "+monthLast+")");

    }

}