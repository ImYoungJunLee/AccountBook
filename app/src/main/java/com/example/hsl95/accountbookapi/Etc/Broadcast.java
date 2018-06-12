package com.example.hsl95.accountbookapi.Etc;

/**
 * Created by hsl95 on 2018-06-05.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.hsl95.accountbookapi.Activity.MainActivity;

import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
문자를 받았을 때 동작하는 클래스
onRecieve함수에 작성
*/

public class Broadcast extends BroadcastReceiver {

    private Bundle bundle;
    private Object messages[];
    private SmsMessage smsMessage[];

    private String origNumber;//발신 번호
    private String receiveMessage;//받은 메세지

    //DB에 저장할 목록 : 카드명, 날짜, 사용금액, 사용장소
    private String cardName;
    private String receiveDate;
    private int useMoney;
    private String useMoneyPlace;

    String cardInfo[];


    //국민15881688,신한15447200,삼성15888900,롯데15888100
    private static final String numberKookmin = "15881688";
    private static final String numberShinhan = "15447200";
    private static final String numberSamsung = "15888900";
    private static final String numberLotte = "15888100";



        @Override
        public void onReceive (Context context, Intent intent) {


            /*if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
                Log.d("onReceive()", "문자가 수신되었습니다");
            }*/

            bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }

            //pdu 형식으로 받음
            messages = (Object[]) bundle.get("pdus");
            if (messages == null) {
                return;
            }

            smsMessage = new SmsMessage[messages.length];
            //pdu형식을 메세지로 변환
            for (int i = 0; i < messages.length; i++) {

                smsMessage[i] = SmsMessage.createFromPdu((byte[]) messages[i]);
            }

            //발신번호
            origNumber = smsMessage[0].getOriginatingAddress();


            //국민,신한,삼성,롯데 일 경우 분석
            boolean validNumber = origNumber.equals(numberKookmin) || origNumber.equals(numberShinhan)
                    || origNumber.equals(numberSamsung) || origNumber.equals(numberLotte);


            //유효한 번호일 경우
            if (validNumber) {

                //카드 이름
                cardName = setCardName(origNumber);

                //받은 메세지
                receiveMessage = smsMessage[0].getMessageBody().toString();

                //메세지를 수신받은 날짜
                receiveDate = new SimpleDateFormat("MM.dd").format(new Date());

                //문자를 읽어 사용한 돈, 사용한 장소 세팅
                switch (origNumber) {
                    case numberKookmin:
                        cardInfo = setKookminCardMoneyInfo(receiveMessage);
                        break;

                    case numberShinhan:
                        cardInfo = setShinhanCardMoneyInfo(receiveMessage);
                        break;

                    case numberSamsung:
                        cardInfo = setSamsungCardMoneyInfo(receiveMessage);
                        break;

                    case numberLotte:
                        cardInfo = setLotteCardMoneyInfo(receiveMessage);
                        break;
                }

                useMoney = Integer.parseInt(cardInfo[0]);
                useMoneyPlace = cardInfo[1];

                //boolean변수
                boolean incomeMoney = receiveMessage.contains("환불") || receiveMessage.contains("취소");

                //환불 or 취소
                if (incomeMoney)
                    useMoney = -useMoney;


                Intent intent1 = new Intent(context, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);


                //문자 메세지가 왔을 때 그 내용을 Database에 Insert
                DBHelper dbHelper = new DBHelper(context, "ACCOUNT.db", null, 1);
                dbHelper.insert(cardName, receiveDate, useMoney, useMoneyPlace);
                dbHelper.close();//Database 닫기

            }
        }


    //Method**************************************************************************************

    public String setCardName(String orgNumber) {

        String cardName = null;

        switch (orgNumber) {
            case numberKookmin:
                cardName = "국민";
                break;

            case numberShinhan:
                cardName = "신한";
                break;

            case numberSamsung://신한 체크카드
                cardName = "삼성";
                break;

            case numberLotte://신한 체크카드
                cardName = "롯데";
                break;
        }

        return cardName;

    }

    //국민 카드 정보 추출 메소드
    public String[] setKookminCardMoneyInfo(String recvMessage) {

        String useMoney = null;
        String useMoneyPlace = null;
        int indexWon;
        int indexColon;

        /*사용금액*/
        recvMessage = recvMessage.replaceAll(",", ""); //금액 사이의 "," 제거
        recvMessage = recvMessage.replaceAll("\\s", "");//문자메세지의 모든 공백,줄바꿈 을 없앰.

        indexColon = recvMessage.indexOf(":");
        indexWon = recvMessage.indexOf("원");//'원' 이라는 글자의 위치를 읽어냄
        useMoney = recvMessage.substring(indexColon+3, indexWon);//'원' 위치를 기준으로 문자를 자름, "원" 은 빼기

        //사용 장소
        useMoneyPlace = recvMessage.substring(indexWon + 1, recvMessage.length() - 2);//'원' 다음부터 국민카드 문자열의 마지막 두글자 제외('사용'제외) 하면 카드를 사용한 장소 읽음

        return new String[]{useMoney, useMoneyPlace};
    }

    //신한 카드 정보 추출 메소드
    public String[] setShinhanCardMoneyInfo(String recvMessage) {

        String useMoney = null;
        String useMoneyPlace = null;
        int indexWon;
        int indexColon;

        /*사용금액*/
        recvMessage = recvMessage.replaceAll(",", ""); //금액 사이의 "," 제거
        recvMessage = recvMessage.replaceAll("\\s", "");//문자메세지의 모든 공백,줄바꿈 을 없앰.

        indexColon = recvMessage.indexOf(":");
        indexWon = recvMessage.indexOf("원");//'원' 이라는 글자의 위치를 읽어냄
        useMoney = recvMessage.substring(indexColon+7, indexWon);//'원' 위치를 기준으로 문자를 자름, "원" 은 빼기

        //사용 장소
        useMoneyPlace = recvMessage.substring(indexWon + 1);

        return new String[]{useMoney, useMoneyPlace};
    }

    //삼성 카드 정보 추출 메소드
    public String[] setSamsungCardMoneyInfo(String recvMessage) {

        String useMoney = null;
        String useMoneyPlace = null;
        int indexWon;
        int indexStar;//*
        int indexColon;//:

        /*사용금액*/
        recvMessage = recvMessage.replaceAll(",", ""); //금액 사이의 "," 제거
        recvMessage = recvMessage.replaceAll("\\s", "");//문자메세지의 모든 공백,줄바꿈 을 없앰.

        indexStar = recvMessage.indexOf("*");
        indexWon = recvMessage.indexOf("원");//'원' 이라는 글자의 위치를 읽어냄
        useMoney = recvMessage.substring(indexStar+2, indexWon);//'원' 위치를 기준으로 문자를 자름, "원" 은 빼기

        //사용 장소
        indexColon = recvMessage.indexOf(":");
        useMoneyPlace = recvMessage.substring(indexColon + 3);

        return new String[]{useMoney, useMoneyPlace};
    }

    //롯데 카드 정보 추출 메소드
    public String[] setLotteCardMoneyInfo(String recvMessage) {

        String useMoney = null;
        String useMoneyPlace = null;
        int indexWon;
        int indexBlacket;

        /*사용금액*/
        recvMessage = recvMessage.replaceAll(",", ""); //금액 사이의 "," 제거
        recvMessage = recvMessage.replaceAll("\\s", "");//문자메세지의 모든 공백,줄바꿈 을 없앰.

        indexBlacket = recvMessage.indexOf(")");
        indexWon = recvMessage.indexOf("원");//'원' 이라는 글자의 위치를 읽어냄
        useMoney = recvMessage.substring(indexBlacket+1, indexWon);//'원' 위치를 기준으로 문자를 자름, "원" 은 빼기

        //사용 장소
        indexWon = recvMessage.lastIndexOf("원");//'원' 이라는 글자의 위치를 읽어냄
        useMoneyPlace = recvMessage.substring(indexWon + 1);

        return new String[]{useMoney, useMoneyPlace};
    }

}





