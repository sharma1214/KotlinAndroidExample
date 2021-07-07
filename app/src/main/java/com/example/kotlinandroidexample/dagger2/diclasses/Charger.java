package com.example.kotlinandroidexample.dagger2.diclasses;

import android.util.Log;

import javax.inject.Inject;

public class Charger {
    private static final String TAG = "Dagger2";

    @Inject
    public Charger(){
        Log.e(TAG, "Charger: ");
    }

    public void setCharger(Mobile mobile){
        Log.e(TAG, "setCharger: ");
    }
}
