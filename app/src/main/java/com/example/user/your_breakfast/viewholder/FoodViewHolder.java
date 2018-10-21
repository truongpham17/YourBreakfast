package com.example.user.your_breakfast.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.your_breakfast.R;
import com.example.user.your_breakfast.model.MyOnItemClickListener;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtCategoryName, txtPrice;
    public ImageView imgOrder, imgCategory, imgShareToFb;
    private MyOnItemClickListener onClickListener;
    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        this.txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
        txtPrice = itemView.findViewById(R.id.txtPrice);
        imgOrder= itemView.findViewById(R.id.imgOrder);
        imgShareToFb = itemView.findViewById(R.id.imgShareToFb);
        imgCategory = itemView.findViewById(R.id.imageCategory);
        imgOrder.setOnClickListener(this);
        imgShareToFb.setOnClickListener(this);
        itemView.setOnClickListener(this);
    }

    public void setOnClickListener(MyOnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        onClickListener.onItemClick(v);
    }
}
