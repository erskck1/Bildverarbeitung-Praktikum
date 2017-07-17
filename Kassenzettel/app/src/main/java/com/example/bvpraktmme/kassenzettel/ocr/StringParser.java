package com.example.bvpraktmme.kassenzettel.ocr;
import android.util.Log;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import info.debatty.java.stringsimilarity.*;

/**
 * Created by Michael Graf on 11.07.2017.
 */

public class StringParser {
    String ocrResult;
    Bill bill;
    String [] resultList;
    String [] markets = {"Aldi", "Coop", "Edeka", "Marktkauf", "E Center", "Netto", "SPAR", "NP-Markt",
            "Famila", "K+K", "Metro", "Real", "Netto", "Norma", "REWE", "Penny", "Kaufland", "Lidl"};

    public StringParser(String ocrResult){
        this.ocrResult = ocrResult;
        bill = new Bill();
    }

    //TODO what to do when price/kg is none, simply set none? handle on databse entry
    public Bill parse(){
        resultList = ocrResult.split("\n");
        ArrayList<String> ppResult = new ArrayList<>();
        int startIndex = 0;
        //Strip potential nonsense characters at the beginning of the result
        while(resultList[startIndex].length() < 5){
            startIndex += 1;
        }
        for(int i = startIndex; i< resultList.length; i++){
            ppResult.add(resultList[i].replaceAll("\\*", ""));
        }
        for (String x: ppResult) {
            Log.d("ppresult", "parse: " + x);
        }
        parseGeneralInformation(ppResult);
        ArrayList<ShoppingItem> items = parseItemList(ppResult);
        bill.setItems(items);

        return bill;
    }

    /**
     * From the index of the first line parse the item area for items and add them to the list
     * @param ppResult
     */
    private ArrayList<ShoppingItem> parseItemList(ArrayList<String> ppResult){
        ArrayList<ShoppingItem> items = new ArrayList<>();
        NGram nGram = new NGram(2);
        boolean itemArea = false;
        for ( int i = 0; i < ppResult.size(); i++){
            boolean end = false;
            if(ppResult.get(i).isEmpty())
                continue;
            String [] split = ppResult.get(i).split(" ");
            for (int j = 0; j < split.length; j++) {
                if (nGram.similarity(split[j], "Summe") > 0.3)
                    end = true;
            }
            if (end)
                break;
            if (itemArea){
                if(ppResult.get(i).matches(".*\\d+.*")){
                    items.add(parseItem(ppResult, i));
                }
                //Skip over the next line if an item spans more than two lines
                else{
                    items.add(parseItem(ppResult,i));
                    i += 1;
                }
            }
            if(nGram.similarity(ppResult.get(i), "EUR") > 0.2 || nGram.similarity(ppResult.get(i), "Preis") > 0.2)
                itemArea = true;
        }


        return items;
    }

    /**
     * Parse a line or two into an item
     * @param strings
     * @param index
     * @return
     */
    private ShoppingItem parseItem(ArrayList<String> strings, int index){
        ShoppingItem item = new ShoppingItem();
        boolean priceOnLine = false;
        //See if there are numbers in the line
        if(strings.get(index).matches(".*\\d+.*"))
            priceOnLine = true;
        // Parse the line into a shopping item
        if (priceOnLine){
            String [] splitLine = strings.get(index).split(" ");
            String product = "";
            for (int i = 0; i < splitLine.length - 2 ; i++) {
                if(i == 0)
                    product += splitLine[i];
                else
                    product += " " + splitLine[i];
            }
            item.setProduct(product);
            item.setPrice(Double.parseDouble(splitLine[splitLine.length - 2].replace(",", ".")));
        }

        //No number on line and line not empty
        if(!priceOnLine && strings.get(index).length() > 2 && strings.get(index + 1).split(" ").length > 2){
            item.setProduct(strings.get(index));
            String [] weightInfo = strings.get(index +1).split(" ");
            item.setWeight(Double.parseDouble(weightInfo[0].replace(",",".")));
            item.setPricePerKg(Double.parseDouble(weightInfo[3].replace(",", ".")));
            item.setPrice(Double.parseDouble(weightInfo[5].replace("," ,".")));

        }


        return item;
    }

    /**
     * Find the general information such as the market and location, also find the line? when the actual
     * items begin
     */
    private void parseGeneralInformation(ArrayList<String> lines){
        bill =  new Bill();
        bill.setMarket(parseMarket(lines.get(0)));
        bill.setAdress(lines.get(1));
        bill.setCity(lines.get(2));

    }

    //TODO maybe use this for other things as well/ add more markets drogerien and such

    /**
     * Check the ocr result for similarity to the most common markets, if no close match simply use the
     * result produced by ocr
     * @param marketResult
     * @return
     */
    private String parseMarket(String marketResult){
        NGram ngram = new NGram(2);
        int index = 0;
        double stringDist = 0.0;
        for(int i = 0; i < markets.length; i++){
            double similarity = ngram.similarity(marketResult,markets[i]);
            Log.d(marketResult, "parseMarket: " + markets[i] + " : " + similarity);
            if(similarity > stringDist){
                stringDist = similarity;
                index = i;
            }
        }
        if(stringDist > 0.1) {
            return markets[index];
        }
        else{
            return marketResult;
        }
    }
}



