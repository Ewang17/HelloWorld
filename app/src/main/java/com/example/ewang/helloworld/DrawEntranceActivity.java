package com.example.ewang.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.ewang.helloworld.service.BaseActivity;

public class DrawEntranceActivity extends BaseActivity implements View.OnClickListener {

    ImageView drawFront;
    ImageView drawLeft;
    ImageView drawBack;
    ImageView drawRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_entrance);
        drawFront = findViewById(R.id.draw_front);
        drawLeft = findViewById(R.id.draw_left);
        drawBack = findViewById(R.id.draw_back);
        drawRight = findViewById(R.id.draw_right);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.draw_front:
                intent = new Intent(this, DrawFrontActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
