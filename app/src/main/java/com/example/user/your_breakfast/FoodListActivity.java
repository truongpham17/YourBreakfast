package com.example.user.your_breakfast;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.user.your_breakfast.model.Category;
import com.example.user.your_breakfast.model.MyOnItemClickListener;
import com.example.user.your_breakfast.viewholder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

public class FoodListActivity extends AppCompatActivity {
    String categoryId;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference foodDatabase;
    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_food_list);
        Slidr.attach(this);
        addControls();

    }

    private void addControls() {
        Intent intent = getIntent();
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
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        foodDatabase = FirebaseDatabase.getInstance().getReference("Foods");
        foodDatabase.orderByChild("MenuId");
        adapter =
                new FirebaseRecyclerAdapter<Category, CategoryViewHolder>
                        (Category.class, R.layout.catergory_item, CategoryViewHolder.class,
                                foodDatabase.orderByChild("MenuId").equalTo(categoryId)) {
                    @Override
                    protected void populateViewHolder(CategoryViewHolder viewHolder, Category model, final int position) {
                        viewHolder.txtCategory.setText(model.getName());
                        Picasso.get().load(model.getImage()).into(viewHolder.imageCategory);
                        viewHolder.setListener(new MyOnItemClickListener() {
                            @Override
                            public void onItemClick(View view) {
                                Intent intent = new Intent(FoodListActivity.this, FoodActivity.class);
                                intent.putExtra("foodId", adapter.getRef(position).getKey());
                                startActivity(intent);
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
