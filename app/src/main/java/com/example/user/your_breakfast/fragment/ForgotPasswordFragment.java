package com.example.user.your_breakfast.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.user.your_breakfast.R;

public class ForgotPasswordFragment extends DialogFragment {
    Dialog mDialog;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(R.layout.activity_forgot_password)
                .setTitle("Forgot password");
        return builder.create();
    }
}
