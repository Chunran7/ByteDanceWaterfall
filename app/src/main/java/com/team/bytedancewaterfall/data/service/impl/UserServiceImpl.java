package com.team.bytedancewaterfall.data.service.impl;

import static com.team.bytedancewaterfall.activity.LoginActivity.USER_TOKEN;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.team.bytedancewaterfall.activity.FeedScrollActivity;
import com.team.bytedancewaterfall.data.database.AppDatabaseHelper;
import com.team.bytedancewaterfall.data.database.UserDatabaseHelper;
import com.team.bytedancewaterfall.data.pojo.dto.LoginDTO;
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;
import com.team.bytedancewaterfall.data.pojo.entity.User;
import com.team.bytedancewaterfall.data.service.UserService;
import com.team.bytedancewaterfall.utils.JWTUtil;
import com.team.bytedancewaterfall.utils.PasswordEncryptUtil;
import com.team.bytedancewaterfall.utils.SPUtils;
import com.team.bytedancewaterfall.utils.ToastUtils;

public class UserServiceImpl implements UserService {
    private static UserService userService;
    private UserServiceImpl() {}
    public static UserService getInstance() {
        if (userService == null) {
            synchronized (UserServiceImpl.class) {
                if (userService == null) {
                    userService = new UserServiceImpl();
                }
            }
        }
        return userService;
    }
    @Override
    public String userLogin(Context context, LoginDTO loginDTO) {
        if (loginDTO.getUsername() == null || loginDTO.getPassword() == null) {
            return null;
        }
        // 验证用户名和密码
        UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(context);
        // 移除重复的null检查
        User userData = userDatabaseHelper.getUserByUserName(loginDTO.getUsername());
        // 添加对userData的null检查，防止空指针异常
        if (userData != null && isValid(loginDTO.getPassword(), userData.getPassword())) {
            userData.setPassword(null);
            // 返回令牌
            String token = JWTUtil.generateToken(userData);
            User user = JWTUtil.extractUserFromToken(token);
            return token;
        }
        return null;
    }
    @Override
    public User getCurrentUser(Context context) {
        // 从缓存中获取当前用户
        String token = SPUtils.getInstance(context).getString(USER_TOKEN, "");
        if (token != null && !token.isEmpty()) {
            User user = JWTUtil.extractUserFromToken(token);
            return user;
        }
        return null;
    }

    private boolean isValid(String password, String passwordInDB) {
        // 密文验证
        try {
            return PasswordEncryptUtil.verifyPassword(password, passwordInDB);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

