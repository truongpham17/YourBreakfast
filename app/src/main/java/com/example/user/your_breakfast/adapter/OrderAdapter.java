package com.example.user.your_breakfast.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.user.your_breakfast.R;
import com.example.user.your_breakfast.database.Database;
import com.example.user.your_breakfast.model.Order;
import com.example.user.your_breakfast.viewholder.CartViewHolder;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<CartViewHolder> {
    List<Order> orders;
    private Context parentContext;
    private TextView txtSum;
    private int sum;

    public OrderAdapter(List<Order> orders, Context parentContext, TextView txtSum) {
        this.orders = orders;
        this.parentContext = parentContext;
        this.txtSum = txtSum;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {
        holder.txtFoodPrice.setText("Price: $" + orders.get(position).getFoodPrice());
        holder.txtFoodName.setText(orders.get(position).getFoodName());
        Picasso.get().load(orders.get(position).getImgURL()).into(holder.imgFood);
        holder.quantity.setNumber(orders.get(position).getQuantity() + "");
        holder.txtSum.setText("$" + Integer.parseInt(orders.get(position).getFoodPrice()) *
                orders.get(position).getQuantity());
        sum += Integer.parseInt(holder.txtSum.getText().toString().substring(1));
        holder.quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                new Database(parentContext).addToCarts(orders.get(position).getFoodId(), newValue);
                orders.get(position).setQuantity(newValue);
                holder.txtSum.setText("$" + Integer.parseInt(orders.get(position).getFoodPrice()) *
                        orders.get(position).getQuantity());
                sum += Integer.parseInt(orders.get(position).getFoodPrice()) * (newValue - oldValue);
                txtSum.setText("$" + sum);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void removeItem(int position) {
        new Database(parentContext).deleteCart(orders.get(position).getFoodId());
        orders.remove(position);
        notifyItemRemoved(position);
    }

    public void deleteAllItem() {
        new Database(parentContext).cleanCart();
        for (int i = 0; i < orders.size(); i++) {
            notifyItemRemoved(0);
        }
        orders.clear();
        txtSum.setText("$0");
    }

    public void restoreItem(Order order, int position) {
        orders.add(position, order);
        new Database(parentContext).addToCarts(order);
        notifyItemInserted(position);
    }
}
