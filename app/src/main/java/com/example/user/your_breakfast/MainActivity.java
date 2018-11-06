package com.example.user.your_breakfast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.user.your_breakfast.common.ShareData;
import com.example.user.your_breakfast.model.User;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roger.catloadinglibrary.CatLoadingView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    Button btnSignIn, btnSignUp;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set default font for all element on this activity
        setDefaultFont();
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        //set controls for element view
        addControls();
        //add action for element objects
        addEvents();
        if (checkConnection()) {
            loginRememerUser();

        } else {
            Snackbar.make(relativeLayout, "There is no Internet connection. Please connect and try again!", Snackbar.LENGTH_LONG).show();
        }
    }

    private boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private boolean loginRememerUser() {
        SharedPreferences preferences = getSharedPreferences("MY_PREF", MODE_PRIVATE);
        String userID = preferences.getString("USER", "null");
        if (!userID.equals("null")) {
            final CatLoadingView catLoadingView = new CatLoadingView();
            catLoadingView.show(getSupportFragmentManager(), "CatLoadingView");
            catLoadingView.setCanceledOnTouchOutside(false);
            DatabaseReference user = FirebaseDatabase.getInstance().getReference("USER").child(userID);
            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Intent intent = new Intent(MainActivity.this, FoodCategoryActivity.class);
                    ShareData.setUser(user);
                    startActivity(intent);
                    catLoadingView.dismiss();
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return true;
        }
        return false;
    }

    private void addEvents() {
        // When user click on sign in button, change to sign in screen, no extra require
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection()) {
                    if (!loginRememerUser()) {
                        changeToLoginScreen();
                        finish();
                    }
                } else {
                    Snackbar.make(relativeLayout, "There is no Internet connection. Please connect and try again!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        // When user click on sign up button, change to sign up scren, no extra require
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection()) {
                    changeToSignUpScreen();
                } else {
                    Snackbar.make(relativeLayout, "There is no Internet connection. Please connect and try again!", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    // action when user click on sign up button
    private void changeToSignUpScreen() {
        Intent signUpIntent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(signUpIntent);
    }

    // action when user click on sign in button
    private void changeToLoginScreen() {
        Intent signInIntent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(signInIntent);
    }

    private void addControls() {
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        relativeLayout = findViewById(R.id.relativeLayout);

    }

    private void setDefaultFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/nabila.ttf")
                .setFontAttrId(R.attr.fontPath).build());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
