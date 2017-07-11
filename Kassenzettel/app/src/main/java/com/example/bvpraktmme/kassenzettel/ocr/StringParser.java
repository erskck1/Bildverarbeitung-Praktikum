package com.example.bvpraktmme.kassenzettel.ocr;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Michael Graf on 11.07.2017.
 */

public class StringParser {
    String ocrResult;
    Bill bill;
    public StringParser(String ocrResult){
        this.ocrResult = ocrResult;
    }

    public void parse(){
        String[] lines = ocrResult.split("\n");

    }
}

