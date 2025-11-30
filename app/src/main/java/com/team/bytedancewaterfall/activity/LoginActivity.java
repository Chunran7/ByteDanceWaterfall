package com.team.bytedancewaterfall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.team.bytedancewaterfall.R;
import com.team.bytedancewaterfall.data.pojo.dto.LoginDTO;
import com.team.bytedancewaterfall.data.service.impl.UserServiceImpl;
import com.team.bytedancewaterfall.utils.JWTUtil;
import com.team.bytedancewaterfall.utils.SPUtils;
import com.team.bytedancewaterfall.utils.ToastUtils;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static final String USER_TOKEN = "user_token";
    private Button login_button;
    private EditText userNameVide;
    private EditText passwordView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLogin()) {
            // 登录过了，直接进入后续的页面
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
//            SPUtils.getInstance(this).remove(USER_TOKEN);
            finish();
        }
        // 没有登录过，进入登录页面
        setContentView(R.layout.login_activity);
        initView();
        setListener();
    }

    private void setListener() {
        userNameVide.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    userNameVide.setError("此项为必填项");
                } else {
                    userNameVide.setError(null);
                }
            }
        });
        passwordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String pwd = s.toString().trim();
                if (pwd.isEmpty()) {
                    passwordView.setError("密码不能为空");
                    if (pwd.length() < 6) {
                        passwordView.setError("密码长度不能小于6");
                    }
                } else {
                    passwordView.setError(null);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        // 忘记密码监听器
        findViewById(R.id.forget_pwd).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 跳转到忘记密码页面
                Intent intent = new Intent(LoginActivity.this, ForgetPwdActivity.class);
                startActivity(intent);
            }
        });
        // 微信登录监听
        findViewById(R.id.wechat_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShortToast(LoginActivity.this, "微信登录功能暂未实现");
            }
        });
        // Apple登录
        findViewById(R.id.apple_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShortToast(LoginActivity.this, "Apple登录功能暂未实现");
            }
        });
        // 立即注册
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    private void initView() {
        login_button = findViewById(R.id.login_button);
        userNameVide = findViewById(R.id.user_name);
        passwordView = findViewById(R.id.login_password);
        // 监听登录
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitLogin();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isLogin()) {
            // 登录过了，直接进入后续的页面
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
//            SPUtils.getInstance(this).remove(USER_TOKEN);
            finish();
        }
    }

    public boolean isLogin() {
        // 尝试获取登录过的用户
        // 1. 从SharedPreferences中获取用户信息
        String userToken = SPUtils.getInstance(this).getString(USER_TOKEN, "");
        if (userToken.isEmpty()) {
            // 没有token存在，未登录过
            return false;
        }
        if (!JWTUtil.validateToken(userToken)) {
            // token验证失败
            return false;
        }
        return true;
    }
    private void waitLogin() {
        String userName = userNameVide.getText().toString();
        String password = passwordView.getText().toString();
        String token = UserServiceImpl.getInstance().userLogin(LoginActivity.this, new LoginDTO(userName, password));
        if (token != null) {
            // 存储token
            SPUtils.getInstance(LoginActivity.this).putString(USER_TOKEN, token);
            setResult(RESULT_OK);
            finish();
        }else {
            ToastUtils.showShortToast(LoginActivity.this, "登录失败，请检查用户名");
        }
    }

}
