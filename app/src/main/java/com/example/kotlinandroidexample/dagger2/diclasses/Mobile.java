package com.example.kotlinandroidexample.dagger2.diclasses;

import android.util.Log;

import javax.inject.Inject;

public class Mobile {
    private Battery battery;
    private Processor processor;
    private static final String TAG = "Dagger2";

    @Inject
    public Mobile( Battery battery,Processor processor) {
        Log.e(TAG, "Mobile: ");
        this.battery = battery;
        this.processor = processor;
    }

    public void getMobileProduct(){
        Log.e(TAG, "getMobileProduct called: ");
    }

    @Inject
    public void charging(Charger charger){
        charger.setCharger(this);
    }
}
