package com.example.user.your_breakfast.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.user.your_breakfast.model.Comment;
import com.example.user.your_breakfast.viewholder.CommentHolder;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentHolder> {
    private List<Comment> commentList;
    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
