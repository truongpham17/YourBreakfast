package com.example.user.your_breakfast.common;

import com.example.user.your_breakfast.model.User;

public class ShareData {
    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        ShareData.user = user;
    }
}
