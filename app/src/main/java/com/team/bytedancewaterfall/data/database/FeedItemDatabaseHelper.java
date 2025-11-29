package com.team.bytedancewaterfall.data.database;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alibaba.fastjson2.JSON;
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FeedItemDatabaseHelper{
/*    private static final String DATABASE_NAME = "feed_item.db";
    private static final int DATABASE_VERSION = 1;*/
/*    public FeedItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }*/
    private SQLiteDatabase db;

    public FeedItemDatabaseHelper(Context context) {
        this.db = AppDatabaseHelper.getInstance(context).getWritableDatabase();
    }
    public static final String TABLE_NOTES = "feed_item";
    public static final String TABLE_CREATE =
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

/*    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 创建表、
        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // 删除表
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        // 创建表
        onCreate(sqLiteDatabase);
    }*/
    /**
     * 查询所有FeedItem数据并封装为List
     * @return List<FeedItem> 数据列表
     */
    public List<FeedItem> getAllFeedItems() {
        List<FeedItem> feedItems = new ArrayList<>();
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
                    String tagsString = cursor.getString(cursor.getColumnIndexOrThrow("tags"));
                    if (tagsString != null && !tagsString.isEmpty()) {
                        try {
                            List<String> tags = JSON.parseObject(tagsString, new com.alibaba.fastjson2.TypeReference<List<String>>() {});
                            item.setTags(tags);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to parse tags", e);
                        }
                    }
/*                    List<String> tags = (List<String>) JSON.parse(cursor.getString(cursor.getColumnIndexOrThrow("tags")));
                    String tagsString = cursor.getString(cursor.getColumnIndexOrThrow("tags"));
                    if (tags != null && !tags.isEmpty()) {
                        item.setTags(tags);
                    }*/

                    item.setVideoUrl(cursor.getString(cursor.getColumnIndexOrThrow("videoUrl")));

                    feedItems.add(item);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // 移除数据库连接关闭，因为db是从AppDatabaseHelper单例获取的共享连接
            // if (db != null && db.isOpen()) {
            //     db.close();
            // }
        }
        return feedItems;
    }
    /**
     * 查询数据库表数据量
     */
    public int getCount() {
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
        db.beginTransaction();
        long insNum = 0l;
        try {
            insNum = db.insert(TABLE_NOTES, null, feedItem.toContentValues());
            if (insNum > 0) {
                // 标记事务成功，确保数据写入数据库
                db.setTransactionSuccessful();
                return true;
            }
            return false;
        }catch (SQLiteException e) {
            Log.e(TAG, "Error inserting feed item", e);
        }finally {
            db.endTransaction();
        }
        return false;
    }

    /**
     * 根据id查询FeedItem
     * @param id
     * @return
     */
    public FeedItem getFeedItemById(String id) {
        Cursor cursor = db.query(TABLE_NOTES, null, "id=?", new String[]{id}, null, null, null);
        FeedItem item = null;
        if (cursor != null && cursor.moveToFirst()) {
            item = new FeedItem();
            item.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
            item.setType(cursor.getInt(cursor.getColumnIndexOrThrow("type")));
            item.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")));
            item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            item.setPrice(cursor.getString(cursor.getColumnIndexOrThrow("price")));

            // 反序列化tags字段
            String tagsString = cursor.getString(cursor.getColumnIndexOrThrow("tags"));
            if (tagsString != null && !tagsString.isEmpty()) {
                try {
                    List<String> tags = JSON.parseObject(tagsString, new com.alibaba.fastjson2.TypeReference<List<String>>() {});
                    item.setTags(tags);
                }catch (Exception e){
                    Log.e(TAG, "Failed to parse tags", e);
                }
            }
            item.setVideoUrl(cursor.getString(cursor.getColumnIndexOrThrow("videoUrl")));
        }
        return item;
    }

    /**
     * 批量根据id删除FeedItem
     * @param ids
     * @return
     */
    public boolean deleteById(List<String> ids) {
        db.beginTransaction();
        int delNum = 0;
        try {
            for (String id : ids) {
                delNum += db.delete(TABLE_NOTES, "id=?", new String[]{id});
            }
            if (delNum > 0) {
                // 标记事务成功
                db.setTransactionSuccessful();
                return true;
            }
            return false;
        }
        catch (Exception e) {
            Log.e(TAG, "Error deleting feed item", e);
            return false;
        }
        finally {
            db.endTransaction();
        }
    }

    /**
     * 更新FeedItem
     * @param feedItem
     * @return
     */
    public boolean updateFeedItem(FeedItem feedItem) {
        if (feedItem == null || feedItem.getId() == null) {
            return false;
        }
        db.beginTransaction();
        int updateNum = 0;
        try {
            updateNum = db.update(TABLE_NOTES, feedItem.toContentValues(), "id=?", new String[]{feedItem.getId()});
            if (updateNum > 0) {
                // 标记事务成功
                db.setTransactionSuccessful();
                return true;
            }
            return false;
        }catch (Exception e) {
            Log.e(TAG, "Error updating feed item", e);
            return false;
        }finally {
            db.endTransaction();
        }
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    public List<FeedItem> pageQueryFeedList(Integer page, Integer size) {
        String sql = "SELECT * FROM " + TABLE_NOTES + " LIMIT ?, ?";
        String limit = String.format("%d, %d", (page - 1) * size, size);
        Cursor cursor = db.rawQuery(sql, new String[]{limit});
        List<FeedItem> feedItems = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                FeedItem item = new FeedItem();
                item.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                item.setType(cursor.getInt(cursor.getColumnIndexOrThrow("type")));
                item.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")));
                item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                item.setPrice(cursor.getString(cursor.getColumnIndexOrThrow("price")));
                item.setTags(JSON.parseObject(cursor.getString(cursor.getColumnIndexOrThrow("tags")), new com.alibaba.fastjson2.TypeReference<List<String>>() {}));
                item.setVideoUrl(cursor.getString(cursor.getColumnIndexOrThrow("videoUrl")));
                feedItems.add(item);
                cursor.moveToNext();
            } while (cursor.moveToNext());
        }
        return feedItems;
    }
}
