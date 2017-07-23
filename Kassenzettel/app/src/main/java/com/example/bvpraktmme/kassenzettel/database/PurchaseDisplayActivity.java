package com.example.bvpraktmme.kassenzettel.database;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.bvpraktmme.kassenzettel.R;
import com.example.bvpraktmme.kassenzettel.ocr.ShoppingItem;

import java.util.List;

public class PurchaseDisplayActivity extends AppCompatActivity {
    SQliteDatabase mDatabase;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //TODO maybe set this as date and market of the display
        getSupportActionBar().setTitle("");


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mDatabase = new SQliteDatabase(this);

        //Get the time of the purchase we want to display
        Bundle extras = getIntent().getExtras();
        String dateTime = extras.getString("dateTime");

        //Get meta info of purchase
        String[] marketInfo = mDatabase.getMarketInfoByDate(dateTime);

        //set the appropriate views to it
        //TODO something here is null, why?
        TextView market = (TextView) findViewById(R.id.market);
        market.setText(marketInfo[0]);

        TextView address = (TextView)findViewById(R.id.address);
        address.setText(marketInfo[1]);

        TextView timeDate = (TextView) findViewById(R.id.time_date);
        timeDate.setText(dateTime);


        //get the shopping items and set the adapter to them to populate the recycler view

        List<ShoppingItem> items = mDatabase.findItemsByPurchaseDate(dateTime);
        mRecyclerView.setVisibility(View.VISIBLE);
        RecyclerAdapter adapter = new RecyclerAdapter(this, items);
        mRecyclerView.setAdapter(adapter);
        //TODO limit the numbers of characters after , to 2
        //TODO  something is wrong with the display of the items

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.close();
    }
}
