package com.team.bytedancewaterfall.activity;

import static com.team.bytedancewaterfall.activity.LoginActivity.USER_TOKEN;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.team.bytedancewaterfall.R;
import com.team.bytedancewaterfall.activity.LoginActivity;
import com.team.bytedancewaterfall.data.pojo.entity.User;
import com.team.bytedancewaterfall.data.service.impl.UserServiceImpl;
import com.team.bytedancewaterfall.utils.JWTUtil;
import com.team.bytedancewaterfall.utils.SPUtils;
import com.team.bytedancewaterfall.utils.ToastUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.io.File;

public class MyInfoActivity extends BaseBottomNavActivity {
    private ImageView userIconView;
    private TextView userNameView;
    private Button logoutButton;
    private Button logintButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_info_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
        setView();
        setListener();
        updateLoginBotton();
        initBottomNavigation();
    }

    private void setListener() {
        // 设置各项监听
        // 登出按钮监听
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示登出确认弹窗
                new AlertDialog.Builder(MyInfoActivity.this)
                        .setTitle("确认退出登录")
                        .setMessage("确定要退出登录吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 用户确认退出，删除token
                                SPUtils.getInstance(MyInfoActivity.this).remove(USER_TOKEN);
                                Intent intent = new Intent(MyInfoActivity.this, MyInfoActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 用户取消退出，关闭对话框
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });
        logintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInfoActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        // 浏览历史按钮监听
        findViewById(R.id.brow_history_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到浏览历史界面
                ToastUtils.showShortToast(MyInfoActivity.this, "功能暂未实现");
            }
        });
        findViewById(R.id.person_info_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到个人信息界面
                ToastUtils.showShortToast(MyInfoActivity.this, "功能暂未实现");
            }
        });
        findViewById(R.id.my_collection_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到我的收藏界面
                ToastUtils.showShortToast(MyInfoActivity.this, "功能暂未实现");
            }
        });
        findViewById(R.id.settings_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到设置界面
                ToastUtils.showShortToast(MyInfoActivity.this, "功能暂未实现");
            }
        });
        findViewById(R.id.about_us_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到关于我们
                ToastUtils.showShortToast(MyInfoActivity.this, "功能暂未实现");
            }
        });
        findViewById(R.id.feedback_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到意见反馈
                ToastUtils.showShortToast(MyInfoActivity.this, "功能暂未实现");
            }
        });
    }

    /**
     * 设置组件动态属性
     */
    private void setView() {
        // 获取当前用户
        String userToken = SPUtils.getInstance(this).getString(USER_TOKEN, "");
        // 获取用户
        User user  = UserServiceImpl.getInstance().getUserByToken(this, userToken);
        if (user == null) {
            // 未登录，显示默认头像
            userIconView.setBackgroundResource(R.drawable.person);
            userNameView.setText("未登录用户");
            return;
        }
        // 已登录，显示用户头像和昵称
        // 设置用户名
        userNameView.setText(user.getNickname());

        // 尝试从用户对象的avatar字段加载头像（文件路径）
        String avatarPath = user.getAvatar();
        if (avatarPath != null) {
            Glide.with(this).load(avatarPath).into(userIconView);
            return;
        }

        // 如果用户没有头像信息或头像加载失败，设置默认头像
        userIconView.setBackgroundResource(R.drawable.person);
        userIconView.setImageDrawable(null); // 清空ImageBitmap，确保显示背景资源

    }

    /**
     * 初始化组件
     */
    private void initView() {
        userIconView = findViewById(R.id.user_icon);
        userNameView = findViewById(R.id.user_name);
        logoutButton = findViewById(R.id.login_out);
        logintButton = findViewById(R.id.login_btn);
    }
    private void updateLoginBotton() {
        // 判断当前是否已登录
        boolean isLogin = UserServiceImpl.getInstance().getCurrentUser(this) != null;
        if (isLogin) {
            // 已登录，显示登出按钮
            logoutButton.setVisibility(View.VISIBLE);
            logintButton.setVisibility(View.GONE);
        }else {
            // 未登录，显示登录按钮
            logoutButton.setVisibility(View.GONE);
            logintButton.setVisibility(View.VISIBLE);
        }
    }
}
