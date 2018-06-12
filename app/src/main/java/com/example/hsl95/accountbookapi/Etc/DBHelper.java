package com.example.hsl95.accountbookapi.Etc;

/**
 * Created by hsl95 on 2018-06-05.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.hsl95.accountbookapi.Model.CardInfoModel;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    Context c;

    static final String ROW_ID="_id";
    static final String CARD="card";
    static final String DATE="date";
    static final String MONEY="money";
    static final String PLACE="place";


    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }



    /* DB를 새로 생성할 때 호출되는 함수
    이름은 ACCOUNT이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
    card,date,place 문자열 컬럼, money 정수형 컬럼으로 구성된 테이블을 생성. */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        /* 이름은 ACCOUNT이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        card,date,place 문자열 컬럼, money 정수형 컬럼으로 구성된 테이블을 생성. */
        db.execSQL("CREATE TABLE ACCOUNT (_id INTEGER PRIMARY KEY, card TEXT, date TEXT, money INTEGER, place TEXT);");
    }


    //DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS ACCOUNT");
        onCreate(db);
    }


    //DB Insert
    public void insert(String card, String date, int money, String place)
    {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();

        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO ACCOUNT VALUES(null, '" + card + "', '" + date + "' , " + money + ", '" + place + "');");
        db.close();
    }


    //DB Update
    public void update(String card , int money, String place) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("UPDATE ACCOUNT SET money=" + money + " WHERE card='" + card + "'and place='" + place + "';");
        db.close();
    }

    //DB Delete
    public void delete(String card,int money,String place) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM ACCOUNT WHERE card='" + card + "'and money='" + money + "'and place='" + place + "';");
        db.close();
    }


    //이번달 사용내역(지출+수입)의 합
    public int getSum()
    {
        SQLiteDatabase db = getReadableDatabase();
        int Sum = 0;

        Cursor cursor = db.rawQuery("SELECT SUM(money) FROM ACCOUNT", null);

        if(cursor != null)
        {
            cursor.moveToFirst();//Cursor를 첫행에 놔두기
        }


        for(int i =0;i<cursor.getColumnCount();i++) {//getColumnCount는 집합의 열의 수
            Sum = Sum + cursor.getInt(i);
        }

        cursor.close();

        return Sum;
    }

    //이번달 수입의 합
    public int getIncomeSum()
    {
        SQLiteDatabase db = getReadableDatabase();
        int Sum = 0;

        // 사용자가 EditText 에 수입을 입력할 때'+값'이면 지출 , '-값' 을 입력하면 수입이라고 본다. 여기서는 수입만 나오도록 조건
        Cursor cursor = db.rawQuery("SELECT SUM(money) FROM ACCOUNT WHERE money<0", null);

        if(cursor != null)
        {
            cursor.moveToFirst();//Cursor를 첫행에 놔두기
        }

        for(int i =0;i<cursor.getColumnCount();i++) {//getColumnCount는 집합의 열의 수
            Sum = Sum + cursor.getInt(i);
        }

        cursor.close();

        return -(Sum);
    }


    public ArrayList<CardInfoModel> selectCardInfo()
    {
        ArrayList<CardInfoModel> cardInfoModels=new ArrayList<>();

        String[] columns={ROW_ID,CARD,DATE,MONEY,PLACE};

        try
        {
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor=db.query("ACCOUNT",columns,null,null,null,null,ROW_ID+" DESC");

            CardInfoModel cIM;

            if(cursor != null)
            {
                while (cursor.moveToNext())
                {
                    String s_card=cursor.getString(1);
                    String s_date=cursor.getString(2);
                    String s_money=cursor.getString(3);
                    String s_place=cursor.getString(4);


                    cIM=new CardInfoModel();
                    cIM.setCard(s_card);
                    cIM.setDate(s_date);
                    cIM.setMoney(s_money);
                    cIM.setPlace(s_place);

                    cardInfoModels.add(cIM);
                }
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }


        return cardInfoModels;
    }


    //수입 목록
    public ArrayList<CardInfoModel> selectIncomeInfo()
    {
        ArrayList<CardInfoModel> cardInfoModels=new ArrayList<>();

        String[] columns={ROW_ID,CARD,DATE,MONEY,PLACE};

        String[] str = new String[]{"0"};

        try
        {
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor=db.query("ACCOUNT",columns,"money<?",str,null,null,ROW_ID+" DESC");

            CardInfoModel cIM;

            if(cursor != null)
            {
                while (cursor.moveToNext())
                {
                    String s_card=cursor.getString(1);
                    String s_date=cursor.getString(2);
                    String s_money=cursor.getString(3);
                    String s_place=cursor.getString(4);

                    cIM=new CardInfoModel();
                    cIM.setCard(s_card);
                    cIM.setDate(s_date);
                    cIM.setMoney(s_money);
                    cIM.setPlace(s_place);

                    cardInfoModels.add(cIM);
                }
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }


        return cardInfoModels;
    }

}

