package com.team.bytedancewaterfall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.team.bytedancewaterfall.R;

/**
 * 底部导航栏基类 Activity，用于统一管理具有底部导航栏的页面。
 * 子类需继承此类以获得统一的底部导航功能。
 */
public abstract class BaseBottomNavActivity extends AppCompatActivity {
    protected LinearLayout homeButton, cartButton, myInfoButton;
    protected ImageView homeIcon, cartIcon, myInfoIcon;
    protected TextView homeText, cartText, myInfoText;

    /**
     * Activity 创建时调用的方法。
     *
     * @param savedInstanceState 保存的实例状态 Bundle 对象
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化底部导航栏组件并绑定点击事件。
     * 包括主页、购物车和个人信息三个按钮及其图标和文字。
     */
    protected void initBottomNavigation() {
        // 初始化底部导航栏控件引用
        homeButton = findViewById(R.id.home_button);
        cartButton = findViewById(R.id.cart_button);
        myInfoButton = findViewById(R.id.my_info_button);

        homeIcon = findViewById(R.id.home_icon);
        cartIcon = findViewById(R.id.cart_icon);
        myInfoIcon = findViewById(R.id.my_info_icon);

        homeText = findViewById(R.id.home_text);
        cartText = findViewById(R.id.cart_text);
        myInfoText = findViewById(R.id.my_info_text);

        // 设置主页按钮点击事件：跳转到 MainActivity（若当前不是该页面）
        homeButton.setOnClickListener(v -> {
            if (!(this.getClass().getSimpleName().equals("MainActivity"))) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // 设置购物车按钮点击事件：跳转到 CartActivity（若当前不是该页面）
        cartButton.setOnClickListener(v -> {
            if (!(this.getClass().getSimpleName().equals("CartActivity"))) {
                Intent intent = new Intent(this, CartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // 设置个人信息按钮点击事件：跳转到 MyInfoActivity（若当前不是该页面）
        myInfoButton.setOnClickListener(v -> {
            if (!(this.getClass().getSimpleName().equals("MyInfoActivity"))) {
                Intent intent = new Intent(this, MyInfoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // 更新导航栏选中状态
        updateNavigationSelection();
    }

    /**
     * 根据当前 Activity 类型更新底部导航栏的选中状态。
     * 主要通过判断当前类名来决定哪个按钮处于激活状态。
     */
    protected void updateNavigationSelection() {
        // 重置所有按钮为未选中状态
        resetNavigationState();

        // 获取当前 Activity 的简单类名，并根据名称设置对应按钮为选中状态
        String className = getClass().getSimpleName();
        if ("MainActivity".equals(className)) {
            setSelectedState(homeIcon, homeText);
        } else if ("CartActivity".equals(className)) {
            setSelectedState(cartIcon, cartText);
        } else if ("MyInfoActivity".equals(className)) {
            setSelectedState(myInfoIcon, myInfoText);
        }
    }

    /**
     * 将所有底部导航按钮的状态重置为未选中样式。
     */
    private void resetNavigationState() {
        setUnselectedState(homeIcon, homeText);
        setUnselectedState(cartIcon, cartText);
        setUnselectedState(myInfoIcon, myInfoText);
    }

    /**
     * 设置指定按钮为选中状态。
     *
     * @param icon 图标 ImageView 控件
     * @param text 文字 TextView 控件
     */
    private void setSelectedState(ImageView icon, TextView text) {
        icon.setColorFilter(getResources().getColor(R.color.colorPrimary));
        text.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    /**
     * 设置指定按钮为未选中状态。
     *
     * @param icon 图标 ImageView 控件
     * @param text 文字 TextView 控件
     */
    private void setUnselectedState(ImageView icon, TextView text) {
        icon.setColorFilter(getResources().getColor(R.color.gray_light));
        text.setTextColor(getResources().getColor(R.color.gray_light));
    }
}
