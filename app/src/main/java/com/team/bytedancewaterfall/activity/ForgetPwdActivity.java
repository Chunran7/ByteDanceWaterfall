package com.team.bytedancewaterfall.activity;


import static com.team.bytedancewaterfall.activity.LoginActivity.USER_TOKEN;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.team.bytedancewaterfall.R;
import com.team.bytedancewaterfall.data.pojo.entity.User;
import com.team.bytedancewaterfall.data.service.impl.UserServiceImpl;
import com.team.bytedancewaterfall.utils.PasswordEncryptUtil;
import com.team.bytedancewaterfall.utils.SPUtils;
import com.team.bytedancewaterfall.utils.ToastUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


public class ForgetPwdActivity extends AppCompatActivity {
    EditText forget_username_edit;
    EditText forget_old_pwd_edit;
    EditText new_pwd_edit;
    EditText confirm_pwd_edit;
    Button confirm_button;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_pwd_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initVIew();
        setListener();
    }
    private void initVIew() {
        // 忘记密码页面的初始化
        forget_username_edit = findViewById(R.id.forget_username_edit);
        forget_old_pwd_edit = findViewById(R.id.old_pwd_edit);
        new_pwd_edit = findViewById(R.id.new_pwd_edit);
        confirm_pwd_edit = findViewById(R.id.forget_confirm_password_edit);
        confirm_button = findViewById(R.id.forget_confirm_password);
    }
    private void setListener() {
        // 忘记密码页面的监听
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPwd = new_pwd_edit.getText().toString();
                String confirmPwd = confirm_pwd_edit.getText().toString();
                String username = forget_username_edit.getText().toString();
                String oldPwd = forget_old_pwd_edit.getText().toString();
                if (username.isEmpty()) {
                    forget_username_edit.setError("用户名不能为空");
                    return;
                }
                if (newPwd.isEmpty() || confirmPwd.isEmpty()) {
                    new_pwd_edit.setError("密码不能为空");
                    return;
                }
                if (!newPwd.equals(confirmPwd)) {
                    confirm_pwd_edit.setError("密码不一致");
                    return;
                }
                if (oldPwd.isEmpty()) {
                    forget_old_pwd_edit.setError("旧密码不能为空");
                    return;
                }
                // 验证用户名和密码
                User user = UserServiceImpl.getInstance().getUserByUsername(ForgetPwdActivity.this, forget_username_edit.getText().toString());
                if (user == null) {
                    ToastUtils.showShortToast(ForgetPwdActivity.this, "用户不存在");
                    return;
                }
                try {
                    if (!PasswordEncryptUtil.verifyPassword(oldPwd, user.getPassword())) {
                        ToastUtils.showShortToast(ForgetPwdActivity.this, "旧密码错误");
                        return;
                    }
                    // 判断新密码与旧密码是否相同
                    if (PasswordEncryptUtil.verifyPassword(newPwd, user.getPassword())) {
                        ToastUtils.showShortToast(ForgetPwdActivity.this, "新密码不能与旧密码相同");
                        return;
                    }
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }

                // 修改密码
                if (modifyPassword(newPwd)) {
                    ToastUtils.showShortToast(ForgetPwdActivity.this, "修改密码成功");
                    // 删除当前缓存(如果有)
                    SPUtils.getInstance(ForgetPwdActivity.this).remove(USER_TOKEN);
                    finish();
                }
            }
        });
    }
    private boolean modifyPassword(String newPwd) {
        // 查询数据库中用户
        User user = UserServiceImpl.getInstance().getUserByUsername(this, forget_username_edit.getText().toString());
        if (user == null) {
            // 用户不存在
            return false;
        }
        try {
            user.setPassword(PasswordEncryptUtil.encryptPassword(newPwd));
            return UserServiceImpl.getInstance().updateUser(this, user);
        } catch (NoSuchAlgorithmException |InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
