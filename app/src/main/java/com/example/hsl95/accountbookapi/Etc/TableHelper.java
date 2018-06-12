package com.example.hsl95.accountbookapi.Etc;

import android.content.Context;
import android.util.Log;

import com.example.hsl95.accountbookapi.Model.CardInfoModel;

import java.util.ArrayList;

/**
 * TABLE HELPER CLASS.
 */
public class TableHelper {

    //DECLARATIONS
    Context c;
    private String[] cardInfoHeaders ={"Card","Date","Money","Place"};
    private String[][] cardProbs;

    //CONSTRUCTOR
    public TableHelper(Context c) {
        this.c = c;
    }

    //RETURN TABLE HEADERS
    public String[] getCardInfoHeaders()
    {
        return cardInfoHeaders;
    }

    //RETURN TABLE ROWS
    public  String[][] getCardProbs()
    {
        ArrayList<CardInfoModel> cardInfoModels=new DBHelper(c, "ACCOUNT.db", null, 1).selectCardInfo();
        CardInfoModel cIM;


        cardProbs = new String[cardInfoModels.size()][4];

        for (int i=0;i<cardInfoModels.size();i++) {

             cIM=cardInfoModels.get(i);

            cardProbs[i][0]=cIM.getCard();
            cardProbs[i][1]=cIM.getDate();
            cardProbs[i][2]=cIM.getMoney();
            cardProbs[i][3]=cIM.getPlace();
        }

        return cardProbs;

    }

    //Income
    public  String[][] getIncomeProbs()
    {

        ArrayList<CardInfoModel> cardInfoModels=new DBHelper(c, "ACCOUNT.db", null, 1).selectIncomeInfo();
        CardInfoModel cIM;

        cardProbs = new String[cardInfoModels.size()][4];

        for (int i=0;i<cardInfoModels.size();i++) {

            cIM=cardInfoModels.get(i);

            cardProbs[i][0]=cIM.getCard();
            cardProbs[i][1]=cIM.getDate();
            cardProbs[i][2]=cIM.getMoney();
            cardProbs[i][3]=cIM.getPlace();
        }

        return cardProbs;

    }
}





