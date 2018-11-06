package com.example.user.your_breakfast.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.user.your_breakfast.R;
import com.example.user.your_breakfast.model.MyOnItemClickListener;

public class CartViewHolder extends RecyclerView.ViewHolder{
    public TextView txtFoodName, txtFoodPrice, txtSum;
    public ImageView imgFood;
    public ElegantNumberButton quantity;
    public CardView foreground;
    public RelativeLayout background;


    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txtFoodName = itemView.findViewById(R.id.txtFoodName);
        txtFoodPrice = itemView.findViewById(R.id.txtFoodPrice);
        txtSum = itemView.findViewById(R.id.txtSum);
        imgFood = itemView.findViewById(R.id.imgFood);
        quantity = itemView.findViewById(R.id.quantity);
        foreground = itemView.findViewById(R.id.view_foreground);
        background = itemView.findViewById(R.id.view_background);
    }


}
