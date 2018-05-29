package com.example.ewang.helloworld;

import android.os.Bundle;

import com.example.ewang.helloworld.service.BaseActivity;

public class SelfActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self);
    }
}
