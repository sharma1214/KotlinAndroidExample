package com.example.kotlinandroidexample.dagger2.di;

import com.example.kotlinandroidexample.dagger2.diclasses.Mobile;
import com.example.kotlinandroidexample.dagger2.ui.DaggerActivity;

import dagger.Component;

@Component
public interface MobileComponent {
    //Mobile getMobile();
    void inject(DaggerActivity activity);
}
