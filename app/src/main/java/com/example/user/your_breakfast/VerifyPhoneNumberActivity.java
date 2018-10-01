package com.example.user.your_breakfast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.your_breakfast.model.Address;
import com.example.user.your_breakfast.model.User;
import com.google.android.gms.signin.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;
import com.raycoarana.codeinputview.CodeInputView;
import com.raycoarana.codeinputview.OnCodeCompleteListener;
import com.roger.catloadinglibrary.CatLoadingView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VerifyPhoneNumberActivity extends AppCompatActivity {
    CodeInputView txtCode;
    Button btnReload;
    FirebaseAuth fbAuth;
    TextView txtTimeLeft;
    CountDownTimer handleTimeLeft;
    String verificationId;
    //    String phoneNumber, verificationId, password, userName;
    User user;
    boolean isForgotPassword;
    PhoneAuthProvider.ForceResendingToken resendToken;
    int timeTryToVerity = 0;
    CatLoadingView catLoadingView;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/nabila.ttf")
                .setFontAttrId(R.attr.fontPath).build());
        Intent intent = getIntent();
        if (intent != null) {
            user = (User) intent.getSerializableExtra("user");
            isForgotPassword = intent.getBooleanExtra("isForgotPassword", false);
        } else {
            finish();
        }
        if (isForgotPassword) {
            Slidr.attach(this);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        setContentView(R.layout.activity_verify_phone_number);
        Slidr.attach(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("VERIFICATION");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        addControls();
//        handleTimeChangeTest();
        handleTimeLeft.start();
        sendCodeToSMS();
        txtCode.addOnCompleteListener(new OnCodeCompleteListener() {
            @Override
            public void onCompleted(String code) {
                verifyCode();
            }
        });
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendCode();
                handleTimeLeft.start();
                btnReload.setEnabled(false);
            }
        });
    }


    private void resendCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                user.getPhone(),
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                        if (phoneAuthCredential.getSmsCode() != null) {
                            txtCode.setCode(phoneAuthCredential.getSmsCode());
                        }
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(VerifyPhoneNumberActivity.this, "Please input valid phone number", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            Toast.makeText(VerifyPhoneNumberActivity.this, "Please wait for a while, and we will resend verify code", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        resendToken = forceResendingToken;
                    }
                }, resendToken
        );
    }

    private void addControls() {
        txtTimeLeft = findViewById(R.id.txtTimeLeft);
        txtTimeLeft.setText("");
        btnReload = findViewById(R.id.btnReload);
        txtCode = findViewById(R.id.txtCode);
        btnReload.setEnabled(false);
        fbAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        userTable = firebaseDatabase.getReference("USER");
        handleTimeLeft = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished / 1000 > 1) {
                    txtTimeLeft.setText(String.format(Locale.getDefault()
                            , "Please wait %d seconds to resend code", millisUntilFinished / 1000));
                } else if (millisUntilFinished / 1000 == 1) {
                    txtTimeLeft.setText(String.format(Locale.getDefault()
                            , "Please wait %d second to resend code", millisUntilFinished / 1000));
                } else {

                }
            }

            @Override
            public void onFinish() {
                btnReload.setEnabled(true);
                txtTimeLeft.setText("");
            }
        };
        catLoadingView = new CatLoadingView();
    }

    private void verifyCode() {
        if (verificationId != null) {
            if (catLoadingView.isAdded()) {
                catLoadingView.getDialog().show();
            } else {
                catLoadingView.show(getSupportFragmentManager(), "CAT_DIALOG_2");
            }
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, txtCode.getCode());
            signInWithPhoneAuthCredential(credential);
        } else {
            txtCode.setCode("");
            txtCode.setEditable(true);
            Toast.makeText(this, "Please wait until you receive message!", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendCodeToSMS() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                user.getPhone(),
                15,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                        if (phoneAuthCredential.getSmsCode() != null) {
                            txtCode.setCode(phoneAuthCredential.getSmsCode());
                        }
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(VerifyPhoneNumberActivity.this, "Please input valid phone number", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            Toast.makeText(VerifyPhoneNumberActivity.this, "Please wait for a while, and we will resend verify code", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        resendToken = forceResendingToken;
                    }
                }
        );
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        if (timeTryToVerity < 5) {
            fbAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                signInUserToServer();
                            } else {
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(VerifyPhoneNumberActivity.this, "Code is not valid!!", Toast.LENGTH_SHORT).show();
                                    txtCode.setCode("");
                                    txtCode.setEditable(true);
                                    timeTryToVerity++;
                                    catLoadingView.dismiss();
                                }
                            }
                        }
                    });
        } else {
            handleTimeLeft.cancel();
            txtTimeLeft.setText("Verify code fail too many times, please come back and check your phone number");
            txtCode.setEditable(false);
            btnReload.setEnabled(false);
            catLoadingView.dismiss();
        }
    }

    private void signInUserToServer() {
        userTable.child(user.getPhone()).setValue(user);
        userTable.child(user.getPhone()).child("address").child("-AAAA").setValue(new Address("Home", "Input your address"));
        // register success, change to login screen
        if (!isForgotPassword) {
            changeToLoginScreen();
        } else {
            changeToForgotPassword();
        }
    }

    private void changeToForgotPassword() {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    private void changeToLoginScreen() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText("SUCCESS")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent(VerifyPhoneNumberActivity.this, SignInActivity.class);
                        startActivity(intent);
                    }
                })
                .setCanceledOnTouchOutside(false);
        sweetAlertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isForgotPassword){
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }
}
