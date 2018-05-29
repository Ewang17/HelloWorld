package com.example.ewang.helloworld;

import android.support.annotation.Nullable;
import android.os.Bundle;

import com.example.ewang.helloworld.service.BaseActivity;

public class ShopActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
    }
}
