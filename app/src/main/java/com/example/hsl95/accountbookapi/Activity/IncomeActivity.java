package com.example.hsl95.accountbookapi.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.hsl95.accountbookapi.Etc.DBHelper;
import com.example.hsl95.accountbookapi.Etc.TableHelper;
import com.example.hsl95.accountbookapi.R;

import java.util.Calendar;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;


/**
 * Created by cosmoslab on 2017-11-20.
 */

public class IncomeActivity extends AppCompatActivity {

    private TableView<String[]>  tb;
    TableHelper tableHelper;

    DBHelper dbHelper;

    private TextView sumIncomeTv;//이번달 수입의 합을 보여주는 TextView
    private TextView monthTermTv;//해당월의 기간

    int sumOfIncome;//Db로 부터 가져온 이번 달 수입의 합

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        getSupportActionBar().hide();

        Intent i = getIntent();

        //DataBase로 부터 이번달 수입에 대한 정보를 가져오기 위해 설정
        dbHelper = new DBHelper(getApplicationContext(), "ACCOUNT.db", null, 1);

        //tableHelper
        tableHelper=new TableHelper(this);


        //이번 달 수입의 합을 Textview에 Set
        sumOfIncome = dbHelper.getIncomeSum();

        sumIncomeTv = (TextView)findViewById(R.id.income_textview3);
        sumIncomeTv.setText("");
        sumIncomeTv.setText(String.valueOf(sumOfIncome));


        //이번달의 기간을 보여줌
        showMonthTerm();

        //Select 수입목록
        selectTable();

    }




    ///////////////////////////////Method//////////////////////////////////

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

    //SelectTable
    public void selectTable(){

        tb = (TableView<String[]>) findViewById(R.id.tableView2);
        tb.setColumnCount(4);
        tb.setHeaderBackgroundColor(Color.parseColor("#00BFA5"));


        tb.setHeaderAdapter(new SimpleTableHeaderAdapter(this,tableHelper.getCardInfoHeaders()));
        tb.setDataAdapter(new SimpleTableDataAdapter(this, tableHelper.getIncomeProbs()));

    }
}