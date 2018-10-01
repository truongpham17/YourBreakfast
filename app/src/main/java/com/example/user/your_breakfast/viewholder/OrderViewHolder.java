package com.example.user.your_breakfast.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.your_breakfast.R;
import com.example.user.your_breakfast.model.MyOnItemClickListener;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtTime, txtDate, txtTotal, txtOrder, txtCooking, txtDeliver, txtReceive;
    public Button btnDetail;
    private MyOnItemClickListener onClickListener;
    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txtTime = itemView.findViewById(R.id.txtTime);
        txtDate = itemView.findViewById(R.id.txtDate);
        txtTotal = itemView.findViewById(R.id.txtTotal);
        txtOrder = itemView.findViewById(R.id.txtOrder);
        txtCooking = itemView.findViewById(R.id.txtCooking);
        txtDeliver = itemView.findViewById(R.id.txtDeliver);
        txtReceive = itemView.findViewById(R.id.txtReceive);
        btnDetail = itemView.findViewById(R.id.btnDetail);
        btnDetail.setOnClickListener(this);
    }

    public void setOnMyClickListener(MyOnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        onClickListener.onItemClick(v);
    }
}
