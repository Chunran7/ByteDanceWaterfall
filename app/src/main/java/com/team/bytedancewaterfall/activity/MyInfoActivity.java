package com.team.bytedancewaterfall.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.team.bytedancewaterfall.R;

public class MyInfoActivity extends BaseBottomNavActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_info_activity);
        initBottomNavigation();
    }
}
