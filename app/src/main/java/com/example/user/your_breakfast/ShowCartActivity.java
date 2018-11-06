package com.example.user.your_breakfast;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.your_breakfast.adapter.OrderAdapter;
import com.example.user.your_breakfast.common.ShareData;
import com.example.user.your_breakfast.database.Database;
import com.example.user.your_breakfast.helper.RecyclerItemTouchHelper;
import com.example.user.your_breakfast.helper.RecyclerItemTouchHelperListener;
import com.example.user.your_breakfast.model.Order;
import com.example.user.your_breakfast.model.SubmitOrder;
import com.example.user.your_breakfast.model.User;
import com.example.user.your_breakfast.viewholder.CartViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;
import com.roger.catloadinglibrary.CatLoadingView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ShowCartActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    OrderAdapter mAdapter;
    List<Order> orderList;
    TextView txtSum;
    Button btnOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_show_cart);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }
        Slidr.attach(this);
        txtSum = findViewById(R.id.txtSum);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        btnOrder = findViewById(R.id.btnOrder);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOrderFood();
            }
        });


        orderList = new Database(this).getCarts();
        int sum = getTotalPrice(orderList);
        txtSum.setText("$" + sum);
        mAdapter = new OrderAdapter(orderList, this, txtSum);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper.SimpleCallback item = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(item).attachToRecyclerView(mRecyclerView);

    }

    private void submitOrderFood() {
        ArrayList<Order> orders = (ArrayList<Order>) new Database(this).getCarts();
        if (orders.isEmpty()) return;
        String content = "Submit your cart";
        com.example.user.your_breakfast.utils.SubmitOrder submitOrder = new com.example.user.your_breakfast.utils.SubmitOrder(this, orders, content, mAdapter);
     submitOrder.submit();

    }

    private int getTotalPrice(List<Order> orderList) {
        int result = 0;
        for (Order order : orderList) {
            result += order.getQuantity() * Integer.parseInt(order.getFoodPrice());
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder) {
            String name = orderList.get(viewHolder.getAdapterPosition()).getFoodName();
            final Order deletedItem = orderList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            mAdapter.removeItem(position);
            Snackbar snackbar = Snackbar.make(mRecyclerView, name + " removed from cart!", Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.restoreItem(deletedItem, deletedIndex);

                }
            });
            snackbar.show();
        }
    }




}
