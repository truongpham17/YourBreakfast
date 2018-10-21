package com.example.user.your_breakfast.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.user.your_breakfast.FoodActivity;
import com.example.user.your_breakfast.FoodListActivity;
import com.example.user.your_breakfast.R;
import com.example.user.your_breakfast.database.Database;
import com.example.user.your_breakfast.model.Category;
import com.example.user.your_breakfast.model.Food;
import com.example.user.your_breakfast.model.MyOnItemClickListener;
import com.example.user.your_breakfast.model.Order;
import com.example.user.your_breakfast.utils.ShareToFacebook;
import com.example.user.your_breakfast.viewholder.FoodViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodViewHolder>{
    private List<Food> mData;
    private Context parentContext;

    public FoodAdapter(List<Food> data, Context parentContext){
        this.mData = data;
        this.parentContext = parentContext;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FoodViewHolder holder, final int position) {
        holder.txtPrice.setText("Price: $" + mData.get(position).getPrice());
        holder.txtCategoryName.setText(mData.get(position).getName());
        Picasso.get().load(mData.get(position).getImage()).into(holder.imgCategory);
        holder.imgOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(parentContext, "Added to cart!", Toast.LENGTH_SHORT).show();
                java.util.Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
                String time = sdf.format(currentTime);
                Order order = new Order(mData.get(position).getDescription(), mData.get(position).getName(), mData.get(position).getPrice(), 1, time, mData.get(position).getImage());
                new Database(parentContext).addToCarts(order);
            }
        });

        holder.imgShareToFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ShareToFacebook(mData.get(position).getImage(), (Activity) parentContext).share();


            }
        });

        holder.setOnClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                Intent intent = new Intent(parentContext, FoodActivity.class);
                intent.putExtra("foodId", mData.get(position).getDescription());
                parentContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }
}
