package com.team.bytedancewaterfall.data.service.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.team.bytedancewaterfall.data.database.AppDatabaseHelper;
import com.team.bytedancewaterfall.data.database.UserDatabaseHelper;
import com.team.bytedancewaterfall.data.pojo.dto.LoginDTO;
import com.team.bytedancewaterfall.data.pojo.entity.User;
import com.team.bytedancewaterfall.data.service.UserService;
import com.team.bytedancewaterfall.utils.JWTUtil;

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
    private boolean isValid(String password, String passwordInDB) {
        // TODO 明文验证
        return password.equals(passwordInDB);
    }
}

