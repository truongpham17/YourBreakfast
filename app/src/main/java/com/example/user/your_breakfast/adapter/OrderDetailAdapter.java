package com.example.user.your_breakfast.adapter;

import android.support.annotation.NonNull;
import android.support.constraint.Placeholder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.your_breakfast.OrderDetailActivity;
import com.example.user.your_breakfast.R;
import com.example.user.your_breakfast.model.Order;
import com.example.user.your_breakfast.viewholder.OrderDetailViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailViewHolder> {
    private List<Order> orders;

    public OrderDetailAdapter(List<Order>orders){
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_item, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.txtSum.setText(order.getQuantity() * Integer.parseInt(order.getFoodPrice() + ""));
        String quantityString = String.format(Locale.getDefault(), "%dX", order.getQuantity());
        holder.txtQuantity.setText(quantityString);
        holder.txtFoodName.setText(order.getFoodName());
        Picasso.get().load(order.getImgURL()).into(holder.imgFood);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
