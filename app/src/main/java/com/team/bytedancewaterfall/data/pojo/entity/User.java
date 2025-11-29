package com.team.bytedancewaterfall.data.pojo.entity;

import android.content.ContentValues;

public class User {
    // 用户id
    private String id;
    // 登录用户名
    private String username;
    // 登录密码
    private String password;
    // 登录令牌
    private String token;
    // 头像
    private String avatar;
    // 昵称
    private String nickname;
    // 邮箱
    private String email;
    // 手机
    private String phone;
    public User(){}
    public User(String username, String password, String token, String avatar, String nickname, String email, String phone) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.token = token;
        this.avatar = avatar;
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("username", username);
        values.put("password", password);
        values.put("token", token);
        values.put("avatar", avatar);
        values.put("nickname", nickname);
        values.put("email", email);
        values.put("phone", phone);
        return values;
    }
}
