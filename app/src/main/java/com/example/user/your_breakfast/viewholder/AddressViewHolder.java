package com.example.user.your_breakfast.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.user.your_breakfast.R;
import com.example.user.your_breakfast.model.MyOnItemClickListener;

public class AddressViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtTitle, txtAddress;
    public ImageView imgEdit;
    public CardView foreground;
    public RelativeLayout background;
    public FrameLayout frameLayout;
    private MyOnItemClickListener onClickListener;
    public AddressViewHolder(@NonNull View itemView) {
        super(itemView);
        frameLayout = itemView.findViewById(R.id.frameLayout);
        txtTitle = itemView.findViewById(R.id.txtType);
        txtAddress = itemView.findViewById(R.id.txtAddress);
        imgEdit = itemView.findViewById(R.id.imgEdit);
        foreground = itemView.findViewById(R.id.view_foreground);
        background = itemView.findViewById(R.id.view_background);
        imgEdit.setOnClickListener(this);
    }

    public void setOnClickListener(MyOnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        onClickListener.onItemClick(v);
    }
}
