package com.example.user.your_breakfast;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.your_breakfast.adapter.OrderDetailAdapter;
import com.example.user.your_breakfast.common.ShareData;
import com.example.user.your_breakfast.model.Order;
import com.example.user.your_breakfast.model.SubmitOrder;
import com.example.user.your_breakfast.model.User;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;

import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {
    DatabaseReference orderDatabase;
    TextView txtStatus, txtUserName, txtPhone, txtAddress, txtSubTotal, txtDelivery, txtTotal;
    Button btnSubmit;
    ImageView imgLocation;
    List<Order> orders;
    SubmitOrder submitOrder;
    RecyclerView recyclerView;
    OrderDetailAdapter adapter;
    double Longitude, Latitude;
    User user;



    private void addControls() {
        hideSystemUI();
        txtStatus = findViewById(R.id.txtTitle);
        txtUserName = findViewById(R.id.txtUserName);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
        txtSubTotal = findViewById(R.id.txtSubTotal);
        txtDelivery = findViewById(R.id.txtDelivery);
        txtTotal = findViewById(R.id.txtTotal);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgLocation = findViewById(R.id.imgLocation);
        Longitude = 0;
        Latitude = 0;
        imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OrderDetailActivity.this, "OK!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OrderDetailActivity.this, LocationTrackingActivity.class);
                Toast.makeText(OrderDetailActivity.this, Longitude + " " + Latitude, Toast.LENGTH_SHORT).show();
                intent.putExtra("Longitude", Longitude);
                intent.putExtra("Latitude", Latitude);
                startActivity(intent);
            }
        });
        user = ShareData.getUser();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_order_detail);
        Slidr.attach(this);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        addControls();

        Intent intent = getIntent();
        String orderId = "";
        if (intent == null) finish();
        else
            orderId = intent.getStringExtra("OrderId");

        orderDatabase = FirebaseDatabase.getInstance().getReference("Requests").child(orderId);
        orderDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                submitOrder = dataSnapshot.getValue(SubmitOrder.class);
                int status = Integer.parseInt(submitOrder.getStatus());
                switch (status) {
                    case 0:
                        txtStatus.setText("Status: Ordered");
                        break;
                    case 1:
                        txtStatus.setText("Status: Cooking");
                        break;
                    case 2:
                        txtStatus.setText("Status: Delivering");
                        break;
                    case 3:
                        txtStatus.setText("Status: Success");
                }
                txtPhone.setText(user.getPhone());
                txtUserName.setText(user.getName());
                txtAddress.setText(user.getAddress().get("-AAAA").getAddress());
                orders = submitOrder.getOrderDetail();
                int subTotal = calculateTotal(orders);
                txtSubTotal.setText("$" + subTotal);
                txtTotal.setText("$" + subTotal);
                txtDelivery.setText("$0");
                adapter = new OrderDetailAdapter(orders);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private int calculateTotal(List<Order> orders) {
        int result = 0;
        for (Order or : orders) {
            result += or.getQuantity() * Integer.parseInt(or.getFoodPrice());
        }
        return result;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
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
