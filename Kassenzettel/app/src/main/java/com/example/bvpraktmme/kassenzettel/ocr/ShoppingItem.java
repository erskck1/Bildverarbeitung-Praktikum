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

    /**
     * Default empty constructor
     */
    public ShoppingItem(){


    }

    public String getProduct() {
        return product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public void setProduct(String product) {

        this.product = product;
    }

}
