package com.example.user.your_breakfast.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.your_breakfast.R;
import com.example.user.your_breakfast.model.MyOnItemClickListener;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtCategory;
    public ImageView imageCategory;
    private MyOnItemClickListener listener;
    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        txtCategory = itemView.findViewById(R.id.txtCategoryName);
        imageCategory = itemView.findViewById(R.id.imageCategory);
        itemView.setOnClickListener(this);
    }

    public void setListener(MyOnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onItemClick(v);
    }
}
