package com.example.user.your_breakfast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class ForgotPasswordActivity extends AppCompatActivity {
    Button btnForgotPassword;
    DatabaseReference userDatabase;
    TextView txtPhone;
    User user;
    CatLoadingView catLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultFont();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_forgot_password);
        Slidr.attach(this);
        addControls();
        addEvents();
    }

    private void addControls() {
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        txtPhone = findViewById(R.id.txtPhone);
        userDatabase = FirebaseDatabase.getInstance().getReference("USER");
        user = new User();
        catLoadingView = new CatLoadingView();
    }

    private void addEvents() {
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtPhone.getText().toString().isEmpty()) {
                    txtPhone.setError("Please input your phone number!");
                } else {
                    resetUserPassword();
                }
            }
        });
    }

    private void resetUserPassword() {
        if(catLoadingView.isAdded()){
            catLoadingView.getDialog().show();
        }
        else{
            catLoadingView.show(getSupportFragmentManager(), "WAITING-DIALOG");
        }
        userDatabase.child(txtPhone.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    txtPhone.setError("This phone has not registered yet!");
                    catLoadingView.getDialog().dismiss();
                } else {
                    user = dataSnapshot.getValue(User.class);
                    sendVerifyCode();
                    catLoadingView.getDialog().dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

            private void sendVerifyCode() {
                Intent intent = new Intent(ForgotPasswordActivity.this, VerifyPhoneNumberActivity.class);
                user.setAddress(null);
                intent.putExtra("user", user);
                intent.putExtra("isForgotPassword", true);
                startActivity(intent);
            }
        });

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
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }



}
