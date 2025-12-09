package com.team.bytedancewaterfall.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.team.bytedancewaterfall.R;

/**
 * 促销专区Activity
 * 展示促销活动相关内容
 */
public class PromotionZoneActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_zone);
        
        // 这里可以获取从FeedAdapter传递过来的促销活动信息
        // 并根据这些信息加载相应的促销内容
        // 功能待开发...
    }
}