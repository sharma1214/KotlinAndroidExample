package com.example.kotlinandroidexample.dagger2.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.kotlinandroidexample.R;
import com.example.kotlinandroidexample.dagger2.di.DaggerMobileComponent;
import com.example.kotlinandroidexample.dagger2.di.MobileComponent;
import com.example.kotlinandroidexample.dagger2.diclasses.Mobile;

import javax.inject.Inject;

public class DaggerActivity extends AppCompatActivity {
    @Inject
    Mobile mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dagger);

        MobileComponent component  = DaggerMobileComponent.create();
        component.inject(this);
         mobile.getMobileProduct();
    }
}