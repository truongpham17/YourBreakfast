package com.example.user.your_breakfast.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptPassword {
    public static String encrypt(String password) {
        String generatedPassword = password;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i: bytes
                 ) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));

            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.d("EncryptPassword: ",  e.getMessage());
        }
        return generatedPassword;
    }
}
