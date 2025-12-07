package com.team.bytedancewaterfall.activity;

import static com.team.bytedancewaterfall.data.vurtualData.FeedItemData.copyFileToPrivateDirByType;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.team.bytedancewaterfall.R;
import com.team.bytedancewaterfall.data.pojo.entity.User;
import com.team.bytedancewaterfall.data.service.UserService;
import com.team.bytedancewaterfall.data.service.impl.UserServiceImpl;

import cn.javaex.htool.core.string.StringUtils;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEdit;
    private EditText passwordEdit;
    private EditText confirmPasswordEdit;
    private EditText emailEdit;
    private EditText phoneEdit;
    private Button registerButton;
    private TextView loginText;
    private EditText nicknameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        // 初始化UI组件
        initView();

        // 设置事件监听
        setListeners();
    }

    private void initView() {
        usernameEdit = findViewById(R.id.username_edit);
        passwordEdit = findViewById(R.id.password_edit);
        confirmPasswordEdit = findViewById(R.id.confirm_password_edit);
        emailEdit = findViewById(R.id.email_edit);
        phoneEdit = findViewById(R.id.phone_edit);
        registerButton = findViewById(R.id.register_button);
        loginText = findViewById(R.id.login_text);
        nicknameEdit = findViewById(R.id.nickName_edit);
    }

    private void setListeners() {
        // 注册按钮点击事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegister();
            }
        });

        // 跳转到登录页面
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void performRegister() {
        // 获取输入内容
        String username = usernameEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        String confirmPassword = confirmPasswordEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();

        // 输入验证
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "密码长度不能少于6位", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // 邮箱格式验证（可选）
        if (!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "请输入有效的邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        }

        // 手机号格式验证（可选）
        if (!TextUtils.isEmpty(phone) && !android.util.Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(this, "请输入有效的手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        String nickName = nicknameEdit.getText().toString();
        if (StringUtils.isNotEmpty(nickName)) {
            // 昵称不为空
            user.setNickname(nickName);
        }else {
            // 昵称为空
            user.setNickname(username);
        }
        // 初始头像都设置默认的
        user.setAvatar(copyFileToPrivateDirByType(this, user.getAvatar(), "avatar", 0));
        // 调用注册服务
        boolean isSuccess = UserServiceImpl.getInstance().registerUser(this, user);

        if (isSuccess) {
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
            // 注册成功后跳转到登录页面
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "注册失败，用户名可能已存在", Toast.LENGTH_SHORT).show();
        }
    }
}
