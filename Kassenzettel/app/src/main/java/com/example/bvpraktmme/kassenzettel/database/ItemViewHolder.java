package com.example.bvpraktmme.kassenzettel.database;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.bvpraktmme.kassenzettel.R;

public class ItemViewHolder extends RecyclerView.ViewHolder{
    public TextView itemName;
    public TextView itemPrice;

    public ItemViewHolder(View itemView) {
        super(itemView);
        itemName = (TextView) itemView.findViewById(R.id.product_name);
        itemPrice = (TextView) itemView.findViewById(R.id.product_price);
    }
}
