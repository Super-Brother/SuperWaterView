package com.wenchao.superwaterview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private WaterView waterView;
    private FrameLayout frameLayout;
    private FrameLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        frameLayout = findViewById(R.id.frameLayout);
        layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        waterView = new WaterView(this);
        waterView.setLayoutParams(layoutParams);
        frameLayout.removeAllViews();
        frameLayout.addView(waterView);
    }
}
