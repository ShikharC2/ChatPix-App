package com.android.chatpix;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class FirebasePersistence extends Application {
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
