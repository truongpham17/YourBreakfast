package com.example.user.your_breakfast.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.your_breakfast.R;
import com.example.user.your_breakfast.model.Category;
import com.example.user.your_breakfast.viewholder.CategoryViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CatergoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    private List<Category> mData;

    public CatergoryAdapter(List<Category> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catergory_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.txtCategory.setText(mData.get(position).getName());
        Picasso.get().load(mData.get(position).getImage()).into(holder.imageCategory);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
