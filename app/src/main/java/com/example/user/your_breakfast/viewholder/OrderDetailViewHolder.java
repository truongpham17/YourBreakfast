package com.example.user.your_breakfast.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.your_breakfast.R;

public class OrderDetailViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgFood;
    public TextView txtFoodName, txtQuantity, txtSum;

    public OrderDetailViewHolder(@NonNull View itemView) {
        super(itemView);
        imgFood = itemView.findViewById(R.id.imgFood);
        txtFoodName = itemView.findViewById(R.id.txtFoodName);
        txtQuantity = itemView.findViewById(R.id.txtQuantity);
        txtSum = itemView.findViewById(R.id.txtSum);
    }
}
