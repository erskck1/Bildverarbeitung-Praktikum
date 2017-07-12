package com.example.bvpraktmme.kassenzettel.ocr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Bill {
    private String market;
    private String dateAndTime;
    private Calendar c = Calendar.getInstance();
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private ArrayList<ShoppingItem> items;
    private String location;
    private double sum;

    public Bill(String market, ArrayList<ShoppingItem> items, double sum, String location){
        this.market = market;
        this.dateAndTime = df.format(c.getTime());
        this.items = items;
        this.sum = sum;
        this.location = location;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("Market   : %s\n", market));
        stringBuilder.append(String.format("Date     : %s\n", dateAndTime));
        stringBuilder.append(String.format("Location : %s\n", location));
        stringBuilder.append(String.format("Products : \n"));

        for(ShoppingItem item : items) {
            stringBuilder.append(String.format("%s\t%.2f €", item.getProduct(), item.getPrice()));
            if(item.getWeight() != 0 && item.getPricePerKg() != 0) {
                stringBuilder.append(String.format("%.2f Kg\\Stück\t%.2f € Kg\\Stück", item.getWeight(), item.getPricePerKg()));
            }
        }

        stringBuilder.append(String.format("Sum      : %.2f €", sum));
        return stringBuilder.toString();
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public Calendar getC() {
        return c;
    }

    public void setC(Calendar c) {
        this.c = c;
    }

    public ArrayList<ShoppingItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<ShoppingItem> items) {
        this.items = items;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}
