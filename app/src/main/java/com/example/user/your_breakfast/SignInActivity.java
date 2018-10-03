package com.example.user.your_breakfast;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.your_breakfast.common.ShareData;
import com.example.user.your_breakfast.model.Address;
import com.example.user.your_breakfast.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;
import com.roger.catloadinglibrary.CatLoadingView;

import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignInActivity extends AppCompatActivity {
    Button btnSignIn;
    TextView txtForgot, txtSignUp, txtPhone, txtPassword;
    DatabaseReference userDatabase;
    CatLoadingView catLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSystemUI();
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/nabila.ttf").setFontAttrId(R.attr.fontPath).build());
        setContentView(R.layout.activity_sign_in);
        addControls();
        addEvents();
    }

    private void addEvents() {
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // change to sign up screen
                Intent signUpScreen = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(signUpScreen);
            }
        });
        txtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // user forgot password, change to verify phone number Activity
                Intent forgotPassword = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPassword);
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });

    }

    private void signInUser() {
        final String phone = txtPhone.getText().toString();
        final String password = txtPassword.getText().toString();
        boolean flat = true;
        if (phone.isEmpty()) {
            txtPhone.setError("Please enter your phone!");
            flat = false;
        }
        if (password.isEmpty()) {
            txtPassword.setError("Please enter your password");
            flat = false;
        }
        if (flat) {
            if (catLoadingView.isAdded()) {
                catLoadingView.getDialog().show();
            } else {
                catLoadingView.show(getSupportFragmentManager(), "WAITING-DIALOG");
            }
            userDatabase.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user.getPhone().equals(phone) && user.getPassword().equals(password)) {
                            ShareData.setUser(user);
                            changeToMainMenu();
                            catLoadingView.getDialog().dismiss();
                        } else {
                            Toast.makeText(SignInActivity.this, "Password is not correct!", Toast.LENGTH_SHORT).show();
                            catLoadingView.getDialog().dismiss();
                        }
                    } else {
                        Toast.makeText(SignInActivity.this, "Phone number is not correct!", Toast.LENGTH_SHORT).show();
                        catLoadingView.getDialog().dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SignInActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void changeToMainMenu() {
        Intent intent = new Intent(this, FoodCategoryActivity.class);
        startActivity(intent);
        finish();
    }

    private void addControls() {
        btnSignIn = findViewById(R.id.btnSignIn);
        txtForgot = findViewById(R.id.txtForgot);
        txtSignUp = findViewById(R.id.txtSignUp);
        txtForgot.setPaintFlags(txtForgot.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtPhone = findViewById(R.id.txtPhone);
        txtPassword = findViewById(R.id.txtPassword);
        userDatabase = FirebaseDatabase.getInstance().getReference("USER");
        catLoadingView = new CatLoadingView();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
