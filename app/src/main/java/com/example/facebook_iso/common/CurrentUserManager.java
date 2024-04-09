package com.example.facebook_iso.common;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.facebook_iso.entities.User;
import com.example.facebook_iso.entities.UserClass;
import com.example.facebook_iso.keys;

import java.util.Objects;

public class CurrentUserManager {

    private static CurrentUserManager instance;
    private final Context context;

    private CurrentUserManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized CurrentUserManager getInstance(Context context) {
        if (instance == null) {
            instance = new CurrentUserManager(context);
        }
        return instance;
    }

    @Nullable
    public User getCurrentUser() {
        User user = SharedPreferencesManager.getObject(context, keys.currentUser, User.class);
        return user;
    }
}
