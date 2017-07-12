package com.example.bvpraktmme.kassenzettel.ocr;

public class ShoppingItem {
    private String product;
    private double price;
    private double weight;
    private double pricePerKg;

    public ShoppingItem(String product, double price, double weight, double pricePerKg){
        this.product = product;
        this.price = price;
        this.weight = weight;
        this.pricePerKg = pricePerKg;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
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
}
