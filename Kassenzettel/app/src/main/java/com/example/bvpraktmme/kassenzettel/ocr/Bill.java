package com.example.bvpraktmme.kassenzettel.ocr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Michael Graf on 11.07.2017.
 */

public class Bill {
    String market;
    String dateAndTime;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public ArrayList<ShoppingItem> items;
    String location;
    double sum;

    public Bill(String market, ArrayList<ShoppingItem> items, double sum){
        this.market = market;
        this.dateAndTime = df.format(c.getTime());
        this.items = items;
        this.sum = sum;
    }
}
