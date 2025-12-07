package com.team.bytedancewaterfall.data.service;

import android.content.Context;
import android.view.View;

import com.team.bytedancewaterfall.activity.ForgetPwdActivity;
import com.team.bytedancewaterfall.data.pojo.dto.LoginDTO;
import com.team.bytedancewaterfall.data.pojo.entity.User;

public interface UserService {
    String userLogin(Context context, LoginDTO loginDTO);

    User getCurrentUser(Context context);
    User getUserByToken(Context context, String token);
    boolean registerUser(Context context, User user);
    boolean updateUserAvatar(Context context, String userId, String avatarPath);
    User getUserById(Context context, String userId);
    User getUserByUsername(Context context, String username);
    boolean updateUser(Context context, User user);
}
