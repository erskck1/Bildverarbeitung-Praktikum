package com.example.bvpraktmme.kassenzettel.database;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bvpraktmme.kassenzettel.R;
import com.example.bvpraktmme.kassenzettel.ocr.ShoppingItem;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Michael Graf on 21.07.2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private Context context;
    private List<ShoppingItem> items;
    //TODO not neede probably
    private SQliteDatabase mDatabase;

    public RecyclerAdapter(Context context, List<ShoppingItem> items){
        this.context = context;
        this.items = items;
        mDatabase = new SQliteDatabase(context);

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        final ShoppingItem item = items.get(position);

        holder.itemPrice.setText(String.valueOf(new DecimalFormat("#.##").format(item.getPrice())) + " €");
        holder.itemName.setText(item.getProduct());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
