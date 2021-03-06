package com.example.user.your_breakfast;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.your_breakfast.database.Database;
import com.example.user.your_breakfast.model.Food;
import com.example.user.your_breakfast.model.MyOnItemClickListener;
import com.example.user.your_breakfast.model.Order;
import com.example.user.your_breakfast.common.ShareToFacebook;
import com.example.user.your_breakfast.viewholder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FoodListActivity extends AppCompatActivity {
    String categoryId;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference foodDatabase;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_food_list);
        Slidr.attach(this);
        addControls();

    }

    private void addControls() {
        final Intent intent = getIntent();
        String categoryName = "";
        if (intent == null) {
            finish();
        } else {
            categoryId = intent.getStringExtra("categoryId");
            categoryName = intent.getStringExtra("categoryName");
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(categoryName);
        TextView textView = (TextView) toolbar.getChildAt(0);
        Typeface tp = Typeface.createFromAsset(getAssets(), "fonts/nabila.ttf");
        textView.setTypeface(tp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(FoodListActivity.this, ShowOrderActivity.class);
                startActivity(intent1);
            }
        });

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);




        foodDatabase = FirebaseDatabase.getInstance().getReference("Foods");
        foodDatabase.orderByChild("MenuId");
        adapter =
                new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.food_item, FoodViewHolder.class, foodDatabase.orderByChild("MenuId").equalTo(categoryId)) {
                    @Override
                    protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {
                        viewHolder.txtCategoryName.setText(model.getName());
                        viewHolder.txtPrice.setText("Price : $" + model.getPrice());
                        Picasso.get().load(model.getImage()).into(viewHolder.imgCategory);
                        viewHolder.imgOrder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(FoodListActivity.this, "Added to cart!", Toast.LENGTH_SHORT).show();
                                java.util.Date currentTime = Calendar.getInstance().getTime();
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
                                String time = sdf.format(currentTime);
                                Order order = new Order(adapter.getRef(position).getKey(), model.getName(), model.getPrice(), 1, time, model.getImage());

                                new Database(FoodListActivity.this).addToCarts(order);
                            }
                        });

                        if(position == 0){
                            int resId = R.anim.layout_animation;
                            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(FoodListActivity.this, resId);
                            recyclerView.setLayoutAnimation(animation);
                        }

                        viewHolder.setOnClickListener(new MyOnItemClickListener() {
                            @Override
                            public void onItemClick(View view) {
                                Intent intent1 = new Intent(FoodListActivity.this, FoodActivity.class);
                                intent1.putExtra("foodId", adapter.getRef(position).getKey());
                                startActivity(intent1);
                            }
                        });
                        viewHolder.imgShareToFb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new ShareToFacebook(model.getImage(), FoodListActivity.this).share();
                            }
                        });
                    }
                };
        recyclerView.setAdapter(adapter);



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
