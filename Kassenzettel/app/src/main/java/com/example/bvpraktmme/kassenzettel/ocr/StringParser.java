package com.example.bvpraktmme.kassenzettel.ocr;

public class StringParser {
    private String ocrResult;
    private Bill bill;

    public StringParser(String ocrResult){
        this.ocrResult = ocrResult;
    }

    public void parse(){
        String[] lines = ocrResult.split("\n");

    }

    public String getOcrResult() {
        return ocrResult;
    }

    public void setOcrResult(String ocrResult) {
        this.ocrResult = ocrResult;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }
}

