package com.team.bytedancewaterfall.data.database;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.team.bytedancewaterfall.data.pojo.entity.User;

import java.util.UUID;

public class UserDatabaseHelper {
    public static final String TABLE_NOTES = "users";


    private SQLiteDatabase db;
    public UserDatabaseHelper(Context context) {
        this.db = AppDatabaseHelper.getInstance(context).getWritableDatabase();
    }
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_TOKEN = "token";
    public static final String COLUMN_AVATAR = "avatar";
    public static final String COLUMN_NICKNAME = "nickname";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NOTES + "(" +
                    COLUMN_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_USERNAME + " TEXT NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_TOKEN + " TEXT NULL, " +
                    COLUMN_AVATAR + " TEXT NULL, " +
                    COLUMN_NICKNAME + " TEXT NULL, " +
                    COLUMN_EMAIL + " TEXT NULL, " +
                    COLUMN_PHONE + " TEXT NULL" +
                    ")";
    // 插入
    public boolean insertUser(User user) {
        if (user == null) {
            // 数据为空，插入失败
            return false;
        }
        if (user.getId() == null) {
            // id为空，自定义id
            user.setId(UUID.randomUUID().toString());
        }
        db.beginTransaction();
        long insNum = 0l;
        try {
            insNum = db.insert(TABLE_NOTES, null, user.toContentValues());
            if (insNum > 0) {
                db.setTransactionSuccessful();
                return true;
            }
            return false;
        }catch (Exception e) {
            Log.e(TAG, "Error inserting user", e);
        }finally {
            db.endTransaction();
        }
        return false;
    }

    public int getCount() {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTES, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public User getUserByUserName(String username) {
        String sql = "SELECT * FROM " + TABLE_NOTES + " WHERE " + COLUMN_USERNAME + " = ?";
        User user = null;
        Cursor cursor = null;
        try {
             cursor = db.rawQuery(sql, new String[]{username});
            if (cursor != null && cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
                user.setToken(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOKEN)));
                user.setAvatar(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR)));
                user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
                return user;
            }
        }catch (Exception e) {
            Log.e(TAG, "Error getting user by username", e);
            if (cursor != null) {
                cursor.close();
            }
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return user;
    }
}

