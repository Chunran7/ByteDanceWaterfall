package com.team.bytedancewaterfall.data.database;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alibaba.fastjson2.JSON;
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FeedItemDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "feed_item.db";
    private static final int DATABASE_VERSION = 1;
    public FeedItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public static final String TABLE_NOTES = "feed_item";
    private static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NOTES + "("
                    + "id TEXT PRIMARY KEY, " +
                    "type INTEGER NOT NULL, " +
                    "imageUrl TEXT, " +
                    "title TEXT, " +
                    "description TEXT, " +
                    "price TEXT, " +
                    "tags TEXT, " +
                    "videoUrl TEXT" +
                    ")";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 创建表、
        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    /**
     * 查询所有FeedItem数据并封装为List
     * @return List<FeedItem> 数据列表
     */
    public List<FeedItem> getAllFeedItems() {
        List<FeedItem> feedItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NOTES, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    FeedItem item = new FeedItem();
                    item.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                    item.setType(cursor.getInt(cursor.getColumnIndexOrThrow("type")));
                    item.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")));
                    item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                    item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    item.setPrice(cursor.getString(cursor.getColumnIndexOrThrow("price")));

                    // 反序列化tags字段
                    List<String> tags = (List<String>) JSON.parse(cursor.getString(cursor.getColumnIndexOrThrow("tags")));
                    String tagsString = cursor.getString(cursor.getColumnIndexOrThrow("tags"));
                    if (tags != null && !tags.isEmpty()) {
                        item.setTags(tags);
                    }

                    item.setVideoUrl(cursor.getString(cursor.getColumnIndexOrThrow("videoUrl")));

                    feedItems.add(item);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return feedItems;
    }
    /**
     * 查询数据库表数据量
     */
    public int getCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTES, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }
    /**
     * 本地数据库插入数据
     */
    public boolean insertFeedItem(FeedItem feedItem) {
        if (feedItem == null) {
            // 数据为空，插入失败
            return false;
        }
        if (feedItem.getId() == null) {
            // id为空，自定义id
            feedItem.setId(UUID.randomUUID().toString());
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long insNum = 0l;
        try {
            insNum = db.insert(TABLE_NOTES, null, feedItem.toContentValues());
        }
        catch (Exception e) {
            Log.e(TAG, "Error inserting feed item", e);
        }finally {
            db.endTransaction();
        }
        return insNum > 0;
    }
}
