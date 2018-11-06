package com.example.user.your_breakfast;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
    TextView txtStatus, txtUserName, txtPhone, txtAddress, txtSubTotal, txtDelivery, txtTotal, txtMoreInfo;
    String address, name, phone, moreInfo;
    Button btnSubmit, btnCancel;
    List<Order> orders;
    SubmitOrder submitOrder;
    RecyclerView recyclerView;
    OrderDetailAdapter adapter;


    private void addControls() {
        txtStatus = findViewById(R.id.txtTitle);
        txtUserName = findViewById(R.id.txtUserName);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
        txtSubTotal = findViewById(R.id.txtSubTotal);
        txtDelivery = findViewById(R.id.txtDelivery);
        txtTotal = findViewById(R.id.txtTotal);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
        txtMoreInfo = findViewById(R.id.txtMoreInfo);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phone.equals(txtPhone.getText().toString())
                        || !name.equals(txtUserName.getText().toString())
                        || !address.equals(txtAddress.getText().toString())
                        || !moreInfo.equals(txtMoreInfo.getText().toString())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                    builder.setTitle("Save change?").setPositiveButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            submitOrder.setAddress(txtAddress.getText().toString());
                            submitOrder.setPhoneNumber(txtPhone.getText().toString());
                            submitOrder.setName(txtUserName.getText().toString());
                            submitOrder.setMoreInfo(txtMoreInfo.getText().toString());
                            orderDatabase.setValue(submitOrder);
                            onBackPressed();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    onBackPressed();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDatabase.child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String status = (String) dataSnapshot.getValue();
                        if (status == null) {
                            status = "";
                        }
                        if (status.equals("0") || status.equals("1")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                            builder.setTitle("Are you sure to cancel this order?")
                                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            orderDatabase.setValue(null);
                                            onBackPressed();
                                        }
                                    })
                                    .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else if (status.equals("2")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                            builder.setTitle("Sure to delete?")
                                    .setMessage("Your order are delivering, if you can, please wait for a couple of minutes " +
                                            "to receive your order. Otherwise, your account will be blocked for 1 week if you cancel delivering order 2 times. " +
                                            "Still want to cancel order?")
                                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            orderDatabase.setValue(null);
                                            onBackPressed();
                                        }
                                    })
                                    .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            Snackbar.make(btnCancel, "Cannot cancel this order", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
                txtPhone.setText(submitOrder.getPhoneNumber());
                phone = txtPhone.getText().toString();
                txtUserName.setText(submitOrder.getName());
                name = txtUserName.getText().toString();
                txtAddress.setText(submitOrder.getAddress());
                address = txtAddress.getText().toString();
                txtMoreInfo.setText(submitOrder.getMoreInfo());
                moreInfo = txtMoreInfo.getText().toString();
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


}
