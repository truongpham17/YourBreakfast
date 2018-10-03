package com.example.user.your_breakfast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.your_breakfast.common.ShareData;
import com.example.user.your_breakfast.model.MyOnItemClickListener;
import com.example.user.your_breakfast.model.SubmitOrder;
import com.example.user.your_breakfast.viewholder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.r0adkll.slidr.Slidr;
import com.roger.catloadinglibrary.CatLoadingView;

public class ShowOrderActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    DatabaseReference orderDatabase;
    TextView txtEmpty;
    FirebaseRecyclerAdapter<SubmitOrder, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSystemUI();
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_show_order);
        Slidr.attach(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        txtEmpty = findViewById(R.id.txtEmpty);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        orderDatabase = FirebaseDatabase.getInstance().getReference("Requests");
        String phoneNumber = ShareData.getUser().getPhone();
        adapter =
                new FirebaseRecyclerAdapter<SubmitOrder, OrderViewHolder>(
                        SubmitOrder.class,
                        R.layout.order_item_2,
                        OrderViewHolder.class,
                        orderDatabase.orderByChild("phoneNumber").equalTo(phoneNumber)) {
                    @Override
                    protected void populateViewHolder(OrderViewHolder viewHolder, SubmitOrder model, final int position) {
                        String time[] = model.getTime().split("-");
                        viewHolder.txtDate.setText(time[1]);
                        viewHolder.txtTime.setText(time[0]);
                        viewHolder.txtTotal.setText("Total: " + model.getTotal());
                        txtEmpty.setVisibility(View.INVISIBLE);
                        int status = Integer.parseInt(model.getStatus());
                        switch (status) {
                            case 0:
                                viewHolder.txtOrder.setTextColor(getResources().getColor(R.color.secondaryDark));
                                break;
                            case 1:
                                viewHolder.txtCooking.setTextColor(getResources().getColor(R.color.secondaryDark));
                                break;
                            case 2:
                                viewHolder.txtDeliver.setTextColor(getResources().getColor(R.color.secondaryDark));
                                break;
                            case 3:
                                viewHolder.txtReceive.setTextColor(getResources().getColor(R.color.secondaryDark));
                                break;
                        }
                        viewHolder.setOnMyClickListener(new MyOnItemClickListener() {
                            @Override
                            public void onItemClick(View view) {
                                Intent intent = new Intent(ShowOrderActivity.this, OrderDetailActivity.class);
                                intent.putExtra("OrderId", adapter.getRef(position).getKey());
                                startActivity(intent);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(adapter);

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
