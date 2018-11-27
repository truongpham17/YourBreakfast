package com.example.user.your_breakfast.common;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.user.your_breakfast.R;
import com.example.user.your_breakfast.ShowOrderActivity;
import com.example.user.your_breakfast.adapter.OrderAdapter;
import com.example.user.your_breakfast.common.ShareData;
import com.example.user.your_breakfast.database.Database;
import com.example.user.your_breakfast.model.Order;
import com.example.user.your_breakfast.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roger.catloadinglibrary.CatLoadingView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SubmitOrder {
    private Context parentContext;
    private List<Order> orders;
    private String moreInfo, content;
    private ArrayList<String> addresses;

    private String address;
    private ArrayAdapter spAdapter;
    private OrderAdapter mAdapter;


    public SubmitOrder(Context parentContext, ArrayList<Order> orders, String content, OrderAdapter mAdapter) {
        this.parentContext = parentContext;
        this.orders = orders;
        addresses = new ArrayList<>();
        this.content = content;
        this.mAdapter = mAdapter;

    }
    public void submit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(parentContext);
        LayoutInflater inflater = (LayoutInflater) parentContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.order_item, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        TextView txtContent = dialogView.findViewById(R.id.txtContent);
        final TextView txtMoreInfo = dialogView.findViewById(R.id.edtInfo);
        final Spinner spAddress = dialogView.findViewById(R.id.spAddress);

        txtContent.setText(content);
        User user = ShareData.getUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("USER").child(user.getPhone()).child("address");
        spAdapter = new ArrayAdapter(parentContext, android.R.layout.simple_list_item_1, addresses);
        spAddress.setAdapter(spAdapter);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                    addresses.add((String)childDataSnapshot.child("address").getValue());
                    spAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Button btnDelivery = dialogView.findViewById(R.id.btnDelivery);
        btnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = spAddress.getSelectedItem().toString();
                moreInfo = txtMoreInfo.getText().toString();
                submitOrder();
                alertDialog.dismiss();
            }
        });

    }
    private void submitOrder(){
        DatabaseReference orderDatabase = FirebaseDatabase.getInstance().getReference("Requests");
        User user = ShareData.getUser();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm-dd MMM yyyy", Locale.getDefault());
        String realDate = sdf.format(date);
        com.example.user.your_breakfast.model.SubmitOrder submitOrder = new com.example.user.your_breakfast.model.SubmitOrder(user.getPhone(), address, "0", getTotalPrice(orders)+"", user.getName(),orders , realDate);
        submitOrder.setMoreInfo(moreInfo);
        CatLoadingView catLoadingView = new CatLoadingView();
        catLoadingView.show(((FragmentActivity) parentContext).getSupportFragmentManager(), "Cat-loading");
        int time = (int) (System.currentTimeMillis());
        String requestId =  time+"";
        orderDatabase.child(requestId).setValue(submitOrder);
        catLoadingView.dismiss();
        final SweetAlertDialog successDialog = new SweetAlertDialog(parentContext, SweetAlertDialog.SUCCESS_TYPE);
        successDialog.setTitleText("Order successfully!")
                .setContentText("Thanks you for your order!").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if(mAdapter != null){
                    Intent intent = new Intent(parentContext, ShowOrderActivity.class);
                    parentContext.startActivity(intent);
                    ((FragmentActivity) parentContext).finish();
                }
                successDialog.dismissWithAnimation();

            }
        })
        ;
        successDialog.show();
        if(mAdapter != null){
            mAdapter.deleteAllItem();
            new Database(parentContext).cleanCart();

        }
    }
    private int getTotalPrice(List<Order> orderList) {
        int result = 0;
        for (Order order : orderList) {
            result += order.getQuantity() * Integer.parseInt(order.getFoodPrice());
        }
        return result;
    }
}
