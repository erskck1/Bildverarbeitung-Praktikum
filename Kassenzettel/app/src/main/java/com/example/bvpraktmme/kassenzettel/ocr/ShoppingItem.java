package com.example.bvpraktmme.kassenzettel.ocr;

/**
 * Created by Michael Graf on 11.07.2017.
 */

public class ShoppingItem {
    String product;
    double price;
    double weight;
    double pricePerKg;

    public ShoppingItem(String product, double price, double weight, double pricePerKg){
        this.product = product;
        this.price = price;
        this.weight = weight;
        this.pricePerKg = pricePerKg;
    }
}
