package com.example.user.your_breakfast;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.user.your_breakfast.common.ShareData;
import com.example.user.your_breakfast.database.Database;
import com.example.user.your_breakfast.model.Comment;
import com.example.user.your_breakfast.model.Food;
import com.example.user.your_breakfast.model.Order;
import com.example.user.your_breakfast.model.User;
import com.example.user.your_breakfast.viewholder.CommentHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class FoodActivity extends AppCompatActivity {
    DatabaseReference foodDatabase, commentData;
    FloatingActionButton fab;
    String foodId;
    Food food;
    ImageView imageFood;
    Toolbar toolbar;
    SharedPreferences preferences;
    TextView txtPrice;
    EditText edtComment;
    RatingBar ratingBar;
    ElegantNumberButton quantity;
    FirebaseRecyclerAdapter<Comment, CommentHolder> adapter;
    TextView txtComment;
    ExpandableLayout expandableLayout;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Button btnComment, btnAddToCard, btnDelivery;
    LikeButton btnLike;
    boolean isCollapsed;
    private static final String PREFERENCES_MODE = "food_added_to_cart";
    private static final String LOVE_MODE = "food_favorite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_food);

        Slidr.attach(this);
        AppBarLayout appBar = findViewById(R.id.appBar);
        appBar.setExpanded(true);
        foodDatabase = FirebaseDatabase.getInstance().getReference("Foods");
        addControls();
        loadData();
    }

    private void addControls() {
        isCollapsed = true;
        ratingBar = findViewById(R.id.ratingBar);
        toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "fonts/nabila.ttf"));
        collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "fonts/nabila.ttf"));

        imageFood = findViewById(R.id.imageFood);
        ratingBar.setRating(4.0f);
        edtComment = findViewById(R.id.edtComment);
        txtComment = findViewById(R.id.txtComment);
        expandableLayout = findViewById(R.id.expandLayout);
        expandableLayout.collapse();

        recyclerView = findViewById(R.id.recyclerViewComment);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        btnComment = findViewById(R.id.btnComment);
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommentToServer();
            }
        });
        txtPrice = findViewById(R.id.txtPrice);
        txtComment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isCollapsed) {
                    txtComment.setText(R.string.comment_collapse);
                    expandableLayout.expand();
                } else {
                    txtComment.setText(R.string.comment_expand);
                    expandableLayout.collapse();
                }
                isCollapsed = !isCollapsed;
            }
        });
        btnLike = findViewById(R.id.btnLike);
        preferences = getSharedPreferences(PREFERENCES_MODE, Context.MODE_PRIVATE);

        btnLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(foodId, true);
                editor.apply();
                Snackbar.make(findViewById(R.id.mCoordinateLayout), "Added to favorite!", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(foodId, false);
                editor.apply();
                Snackbar.make(findViewById(R.id.mCoordinateLayout), "Removed from favorite!", Snackbar.LENGTH_SHORT).show();
            }
        });
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogComfirmBuying();
            }
        });
        quantity = findViewById(R.id.quantity);
        quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                txtPrice.setText("Total: $" + Integer.parseInt(food.getPrice()) * newValue);
            }
        });
        btnAddToCard = findViewById(R.id.btnAddToCart);
        btnAddToCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
                Snackbar snackbar = Snackbar.make(findViewById(R.id.mCoordinateLayout), "Added to cart!", Snackbar.LENGTH_LONG);
                snackbar.setAction("Redo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        redoAddedToCart(Integer.parseInt(quantity.getNumber()), foodId);
                    }
                });
                snackbar.show();
            }
        });
        btnDelivery = findViewById(R.id.btnDelivery);
        btnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogComfirmBuying();
            }
        });

    }

    private void addToCart() {
        java.util.Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
        String time = sdf.format(currentTime);
        Order order = new Order(foodId, food.getName(), food.getPrice(), Integer.parseInt(quantity.getNumber()), time, food.getImage());
        new Database(this).addToCarts(order);
    }

    private void redoAddedToCart(int realQuantity, String foodId) {
        new Database(this).redoAddToCart(foodId, realQuantity);
    }

    private void showDialogComfirmBuying() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.order_item, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        EditText edtUserName = dialogView.findViewById(R.id.txtUserName);
        EditText edtPhone = dialogView.findViewById(R.id.txtPhone);
        EditText edtAddress = dialogView.findViewById(R.id.txtAddress);
        TextView txtContent = dialogView.findViewById(R.id.txtContent);
        txtContent.setText("You order " + quantity.getNumber() + " " + changeToLowerCase(food.getName())
                + ". Total: $" + Integer.parseInt(food.getPrice()) * Integer.parseInt(quantity.getNumber()));
        User user = ShareData.getUser();
        edtUserName.setText(user.getName());
        edtPhone.setText(user.getPhone());
        edtAddress.setText(user.getAddress().get("-AAAA").getAddress());
        boolean flat = true;
        if (edtAddress.getText().toString().isEmpty()) {
            flat = false;
            edtAddress.setError("Please enter your address!");
        }
        if (edtPhone.getText().toString().isEmpty()) {
            flat = false;
            edtPhone.setError("Please enter your phone!");
        }
        if (edtUserName.getText().toString().isEmpty()) {
            flat = false;
            edtUserName.setError("Please enter your Name!");
        }
        Button btnDelivery = dialogView.findViewById(R.id.btnDelivery);
        if (flat) {
            btnDelivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FoodActivity.this, "Thanks you! You can see your order in your history.", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            });
        }


    }

    private void sendCommentToServer() {
        String userComment = edtComment.getText().toString();
        final MaterialRatingBar ratingBar = findViewById(R.id.materialRatingBar);
        float star = ratingBar.getRating();
        CharSequence notification = "Please rating this food to submit comment";
        if (star == 0f) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.mCoordinateLayout), notification, Snackbar.LENGTH_SHORT);
            snackbar.setAction("Add star", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ratingBar.setRating(4f);
                }
            });
            snackbar.show();
            return;
        }
        User user = ShareData.getUser();
        final String phoneNumber = user.getPhone();
        String userName = user.getName();
        String imgUser = user.getImage();
        java.util.Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
        String time = sdf.format(currentTime);
        final Comment comment = new Comment(userName, time, star + "", userComment, imgUser);
        commentData.child(phoneNumber).setValue(comment);
    }

    private void setData() {
        toolbar.setTitle(changeToLowerCase(food.getName()));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Picasso.get().load(food.getImage()).into(imageFood);
        TextView txtUserName = findViewById(R.id.txtUserName);
        User user = ShareData.getUser();
        txtUserName.setText(user.getName());
        ImageView imgUser = findViewById(R.id.imgUser);
        if (!user.getImage().equals("null")) {
            Picasso.get().load(user.getImage()).into(imgUser);
        }
        TextView txtDescription = findViewById(R.id.txtDescription);
        txtDescription.setText(food.getDescription());
        txtPrice.setText("Total: $" + food.getPrice());
        TextView txtFoodName = findViewById(R.id.txtFoodName);
        txtFoodName.setText(food.getName());
    }

    private void loadData() {

        // take food category name
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        } else {
            foodId = intent.getStringExtra("foodId");
        }
        commentData = FirebaseDatabase.getInstance().getReference("Comment").child(foodId);
        foodDatabase.child(foodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    food = dataSnapshot.getValue(Food.class);
                    setData();
                } else {
                    onBackPressed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adapter = new FirebaseRecyclerAdapter<Comment, CommentHolder>(Comment.class, R.layout.comment_item, CommentHolder.class, commentData) {
            @Override
            protected void populateViewHolder(CommentHolder viewHolder, Comment model, int position) {
                viewHolder.ratingBar.setRating(Float.parseFloat(model.getStar()));
                viewHolder.txtExpandView.setText(model.getContentComment());
                viewHolder.txtTime.setText(model.getTimeSubmit());
                viewHolder.txtUserName.setText(model.getUserName());
                if (model.getImgUser() != null) {
                    Picasso.get().load(model.getImgUser()).into(viewHolder.imgUser);
                }
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

    @NonNull
    private String changeToLowerCase(String foodName) {
        String[] arr = foodName.split(" ");
        StringBuilder result = new StringBuilder();
        for (String s : arr) {
            result.append(s.charAt(0) + s.substring(1).toLowerCase() + " ");
        }
        return result.toString().substring(0, result.toString().length() - 1);
    }
}