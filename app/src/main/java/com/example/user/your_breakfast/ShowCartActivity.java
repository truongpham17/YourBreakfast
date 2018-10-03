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

import java.text.SimpleDateFormat;
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
        hideSystemUI();
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
        List<Order> orders = new Database(this).getCarts();
        if (orders.isEmpty()) return;
        DatabaseReference orderDatabase = FirebaseDatabase.getInstance().getReference("Requests");
        User user = ShareData.getUser();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm-dd MMM yyyy", Locale.getDefault());
        String realDate = sdf.format(date);
        SubmitOrder submitOrder = new SubmitOrder(user.getPhone(), user.getAddress().get("-AAAA").getAddress(), "0", txtSum.getText().toString(), user.getName(), orders, realDate);
        CatLoadingView catLoadingView = new CatLoadingView();
        catLoadingView.show(getSupportFragmentManager(), "Cat-loading");
        String requestId = UUID.randomUUID().toString();
        new Database(this).addRequestID(requestId, user.getPhone());
        orderDatabase.child(requestId).setValue(submitOrder);
        mAdapter.deleteAllItem();
        catLoadingView.dismiss();
        SweetAlertDialog successDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        successDialog.setTitleText("Order successfully!")
                .setContentText("Thanks you for your order!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent(ShowCartActivity.this, ShowOrderActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setCanceledOnTouchOutside(false);
        successDialog.show();


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


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideSystemUI();
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}
