package com.example.user.your_breakfast;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.your_breakfast.common.ShareData;
import com.example.user.your_breakfast.model.Category;
import com.example.user.your_breakfast.model.MyOnItemClickListener;
import com.example.user.your_breakfast.viewholder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FoodCategoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recyclerView;
    DatabaseReference categoryDatabase;
    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_category);

        /*************************************************/

        //auto generate
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something here
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /****************************************************/
        //add controls to xml elements
        addControls();

        // load data from firebase database
        loadCategoryInfo();

    }

    private void addControls() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        categoryDatabase = FirebaseDatabase.getInstance().getReference("Category");
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodCategoryActivity.this, ShowCartActivity.class);
                startActivity(intent);
            }
        });

        //add data to navigation view
        NavigationView nav_view = findViewById(R.id.nav_view);
        View headerView = nav_view.getHeaderView(0);
        TextView txtUserName = headerView.findViewById(R.id.txtUserName);
        txtUserName.setText(ShareData.getUser().getName());
        ImageView img = headerView.findViewById(R.id.imgUser);
        String image = ShareData.getUser().getImage();
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/nabila.ttf");
        txtUserName.setTypeface(typeface);
        if (image.equals("null")) {

        } else {
            Log.d("Loading image", "addControls: Successfully!");
            Picasso.get().load(image).into(img);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView txtTitle = (TextView) toolbar.getChildAt(0);
        txtTitle.setTypeface(typeface);
    }

    private void loadCategoryInfo() {
        adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(Category.class, R.layout.catergory_item, CategoryViewHolder.class, categoryDatabase) {
            @Override
            protected void populateViewHolder(final CategoryViewHolder viewHolder, Category model, final int position) {
                viewHolder.txtCategory.setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.imageCategory);
                viewHolder.setListener(new MyOnItemClickListener() {
                    @Override
                    public void onItemClick(View view) {
                        Intent intent = new Intent(FoodCategoryActivity.this, FoodListActivity.class);
                        intent.putExtra("categoryId", adapter.getRef(position).getKey());
                        intent.putExtra("categoryName", viewHolder.txtCategory.getText().toString());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }


    // auto generate, do not touch it
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.my_cart) {
            Intent intent = new Intent(this, ShowCartActivity.class);
            startActivity(intent);
        } else if (id == R.id.order) {
            Intent intent = new Intent(this, ShowOrderActivity.class);
            startActivity(intent);
        } else if(id == R.id.my_profile) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
        }
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
