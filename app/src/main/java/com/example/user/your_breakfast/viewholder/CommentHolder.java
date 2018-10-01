package com.example.user.your_breakfast.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.user.your_breakfast.R;
import com.example.user.your_breakfast.model.MyOnItemClickListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtUserName, txtTime;
    public ExpandableTextView txtExpandView;
    private MyOnItemClickListener onItemClickListener;
    public RatingBar ratingBar;
    public CircleImageView imgUser;
    public CommentHolder(@NonNull View itemView) {
        super(itemView);
        txtUserName = itemView.findViewById(R.id.txtUserName);
        txtTime = itemView.findViewById(R.id.txtTime);
        txtExpandView = itemView.findViewById(R.id.txtExpandView);
        ratingBar = itemView.findViewById(R.id.ratingBar);
        imgUser = itemView.findViewById(R.id.imgUser);
    }

    public void setOnItemClickListener(MyOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        onItemClickListener.onItemClick(v);
    }
}
