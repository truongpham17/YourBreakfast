package com.example.user.your_breakfast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.your_breakfast.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;
import com.roger.catloadinglibrary.CatLoadingView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity {
    Button btnSignIn, btnSignUp;
    EditText txtName, txtPhone, txtPassword;
    DatabaseReference userDatabase;
    CatLoadingView catLoadingView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSystemUI();
        super.onCreate(savedInstanceState);
        //set default font for all element on this activity
        setDefaultFont();
        setContentView(R.layout.activity_sign_up);
        // add Controls for elements in activity
        addControls();
        // add Actions for elements in activity
        addEvents();
        }

    private void addControls() {
        // event element
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        // info element
        txtName = findViewById(R.id.txtName);
        txtPassword = findViewById(R.id.txtPassword);
        txtPhone = findViewById(R.id.txtPhone);
        userDatabase = FirebaseDatabase.getInstance().getReference("USER");
        catLoadingView = new CatLoadingView();

    }

    private void addEvents() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to sign in activity when user click on "Have an account" button, no extras info require
                changeToSignInScreen();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSCodeToUser();
            }
        });
    }

    private void sendSMSCodeToUser() {
        // check validate user input
        boolean flat = true;
        if (txtName.getText().toString().isEmpty()) {
            txtName.setError("Please enter your name!");
            flat = false;
        }
        if (txtPhone.getText().toString().isEmpty()) {
            txtPhone.setError("Please enter your phone number");
            flat = false;
        }
        if (txtPassword.getText().toString().isEmpty()) {
            txtPassword.setError("Please enter your password");
            flat = false;
        }
        if (flat) {
            if (catLoadingView.isAdded()) {
                catLoadingView.getDialog().show();
            } else {
                catLoadingView.show(getSupportFragmentManager(), "CAT_DIALOG");
            }
            userDatabase.orderByChild("phone").equalTo(txtPhone.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        txtPhone.setError("This phone has already registered!");
                        catLoadingView.dismiss();
                    } else{
                        Intent intent = new Intent(SignUpActivity.this, VerifyPhoneNumberActivity.class);
                        User user = new User(txtName.getText().toString(), txtPhone.getText().toString(), txtPassword.getText().toString());
                        intent.putExtra("user", user);
                        startActivity(intent);
                        catLoadingView.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


    private void changeToSignInScreen() {
        // s
        Intent signInIntent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(signInIntent);
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
