package com.team.bytedancewaterfall.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "feed_item.db";
    private static final int DATABASE_VERSION = 2;
    private static AppDatabaseHelper INSTANCE;
    private final Context mContext;
    private AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context.getApplicationContext();    // 强制转为ApplicationContext
    }
    public static synchronized AppDatabaseHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabaseHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppDatabaseHelper(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建表
        db.execSQL(FeedItemDatabaseHelper.TABLE_CREATE);
        db.execSQL(UserDatabaseHelper.TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // 删除表
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FeedItemDatabaseHelper.TABLE_NOTES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserDatabaseHelper.TABLE_NOTES);
        // 创建表
        onCreate(sqLiteDatabase);
    }
}