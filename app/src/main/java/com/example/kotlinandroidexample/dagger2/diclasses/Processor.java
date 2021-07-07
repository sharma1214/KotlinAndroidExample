package com.example.kotlinandroidexample.dagger2.diclasses;

import android.util.Log;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;

public class Processor {

    private static final String TAG = "Dagger2";

    @Inject
    public Processor() {
        Log.e(TAG, "Processor: ");
    }
}
