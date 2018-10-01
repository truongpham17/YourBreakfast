package com.example.user.your_breakfast;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.your_breakfast.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;
import com.roger.catloadinglibrary.CatLoadingView;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ResetPasswordActivity extends AppCompatActivity {
    User user;
    Button btnNext, btnPrev;
    TextView txtPassword;
    DatabaseReference userDatabase;
    CatLoadingView catLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setDefaultFont();
        setContentView(R.layout.activity_reset_password);
        Slidr.attach(this);

        addControls();
        addEvents();
    }


    private void addControls() {
        Intent intent = getIntent();
        if (intent != null) {
            user = (User) intent.getSerializableExtra("user");
        } else {
            finish();
        }
        btnNext = findViewById(R.id.btnResetPassword);
        btnPrev = findViewById(R.id.btnPrev);
        txtPassword = findViewById(R.id.txtPassword);
        userDatabase = FirebaseDatabase.getInstance().getReference("USER");
        catLoadingView = new CatLoadingView();
    }

    private void addEvents() {
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserPassword();
            }
        });
    }

    private void updateUserPassword() {
        final String password = txtPassword.getText().toString();
        if (password.isEmpty()) {
            txtPassword.setError("Please input password!");
            return;
        } else if (password.length() < 6) {
            txtPassword.setError("Password too short, at least 6 characters!");
            return;
        }
        if (catLoadingView.isAdded()) {
            catLoadingView.getDialog().show();
        } else {
            catLoadingView.show(getSupportFragmentManager(), "WAITING-DIALOG");
        }
        userDatabase.child(user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userDatabase.child(user.getPhone()).child("password").setValue(password);
                    catLoadingView.getDialog().dismiss();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    catLoadingView.getDialog().dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ResetPasswordActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                onBackPressed();
                catLoadingView.getDialog().dismiss();
            }
        });
        // Change password successfully, get back to sign in activity
        showSuccessDialog();

    }

    private void showSuccessDialog() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText("Reset Password successfully!")
                .setContentText("Click to return to login")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent(ResetPasswordActivity.this, SignInActivity.class);
                        startActivity(intent);
                    }
                }).setCanceledOnTouchOutside(false);
        sweetAlertDialog.show();
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
