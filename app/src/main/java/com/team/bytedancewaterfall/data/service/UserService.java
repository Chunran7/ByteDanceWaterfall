package com.team.bytedancewaterfall.data.service;

import android.content.Context;

import com.team.bytedancewaterfall.data.pojo.dto.LoginDTO;
import com.team.bytedancewaterfall.data.pojo.entity.User;

public interface UserService {
    String userLogin(Context context, LoginDTO loginDTO);

    User getCurrentUser(Context context);
    User getUserByToken(Context context, String token);
}
