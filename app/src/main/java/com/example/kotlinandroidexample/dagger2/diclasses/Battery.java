package com.example.kotlinandroidexample.dagger2.diclasses;

import android.util.Log;

import javax.inject.Inject;

public class Battery {
    private static final String TAG = "Dagger2";

    @Inject
    public Battery() {
        Log.e(TAG, "Battery: ");
    }
}
