package com.team.bytedancewaterfall.data.database;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_IMAGE_URL = "imageUrl";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_TAGS = "tags";
    public static final String COLUMN_VIDEO_URL = "videoUrl";
    public static final String COLUMN_IMAGE_WIDTH = "imgWidth";
    public static final String COLUMN_IMAGE_HEIGHT = "imgHeight";
    public static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NOTES + "(" +
                    COLUMN_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_TYPE + " INTEGER NOT NULL, " +
                    COLUMN_IMAGE_URL + " TEXT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_PRICE + " TEXT, " +
                    COLUMN_TAGS + " TEXT, " +
                    COLUMN_VIDEO_URL + " TEXT, " +
                    COLUMN_IMAGE_WIDTH + " INTEGER, " +
                    COLUMN_IMAGE_HEIGHT + " INTEGER " +
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
                // 获取列索引
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int typeIndex = cursor.getColumnIndexOrThrow(COLUMN_TYPE);
                int imageUrlIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL);
                int titleIndex = cursor.getColumnIndexOrThrow(COLUMN_TITLE);
                int descriptionIndex = cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION);
                int priceIndex = cursor.getColumnIndexOrThrow(COLUMN_PRICE);
                int tagsIndex = cursor.getColumnIndexOrThrow(COLUMN_TAGS);
                int videoUrlIndex = cursor.getColumnIndexOrThrow(COLUMN_VIDEO_URL);
                int imgWidthIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGE_WIDTH);
                int imgHeightIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGE_HEIGHT);
                do {
                    FeedItem item = new FeedItem();
                    item.setId(cursor.getString(idIndex));
                    item.setType(cursor.getInt(typeIndex));
                    item.setImageUrl(cursor.getString(imageUrlIndex));
                    item.setTitle(cursor.getString(titleIndex));
                    item.setDescription(cursor.getString(descriptionIndex));
                    item.setPrice(cursor.getString(priceIndex));

                    // 反序列化tags字段
                    String tagsString = cursor.getString(tagsIndex);
                    if (tagsString != null && !tagsString.isEmpty()) {
                        try {
                            List<String> tags = JSON.parseObject(tagsString, new com.alibaba.fastjson2.TypeReference<List<String>>() {});
                            item.setTags(tags);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to parse tags", e);
                        }
                    }
                    item.setVideoUrl(cursor.getString(videoUrlIndex));
                    item.setImgWidth(cursor.getInt(imgWidthIndex));
                    item.setImgHeight(cursor.getInt(imgHeightIndex));
                    feedItems.add(item);
                } while (cursor.moveToNext());
            }
        }catch (SQLiteException e) {
            Log.e(TAG, "Error querying feed items", e);
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return feedItems;
    }
    /**
     * 查询数据库表数据量
     */
    public int getCount() {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTES, null);
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return 0;
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
        Cursor cursor = null;
        FeedItem item = null;
        try {
            cursor = db.query(TABLE_NOTES, null, "id=?", new String[]{id}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int typeIndex = cursor.getColumnIndexOrThrow(COLUMN_TYPE);
                int imageUrlIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL);
                int titleIndex = cursor.getColumnIndexOrThrow(COLUMN_TITLE);
                int descriptionIndex = cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION);
                int priceIndex = cursor.getColumnIndexOrThrow(COLUMN_PRICE);
                int tagsIndex = cursor.getColumnIndexOrThrow(COLUMN_TAGS);
                int videoUrlIndex = cursor.getColumnIndexOrThrow(COLUMN_VIDEO_URL);
                int imgWidthIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGE_WIDTH);
                int imgHeightIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGE_HEIGHT);
                item = new FeedItem();
                item.setId(cursor.getString(idIndex));
                item.setType(cursor.getInt(typeIndex));
                item.setImageUrl(cursor.getString(imageUrlIndex));
                item.setTitle(cursor.getString(titleIndex));
                item.setDescription(cursor.getString(descriptionIndex));
                item.setPrice(cursor.getString(priceIndex));

                // 反序列化tags字段
                String tagsString = cursor.getString(tagsIndex);
                if (tagsString != null && !tagsString.isEmpty()) {
                    try {
                        List<String> tags = JSON.parseObject(tagsString, new com.alibaba.fastjson2.TypeReference<List<String>>() {});
                        item.setTags(tags);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse tags", e);
                    }
                }
                item.setVideoUrl(cursor.getString(videoUrlIndex));
                item.setImgWidth(cursor.getInt(imgWidthIndex));
                item.setImgHeight(cursor.getInt(imgHeightIndex));
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error querying feed item by id", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
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
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int offset = (page - 1) * size;
        String sql = "SELECT * FROM " + TABLE_NOTES + " LIMIT ? OFFSET ?";
        Cursor cursor = null;
        List<FeedItem> feedItems = new ArrayList<>();
        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(size), String.valueOf(offset)});
            if (cursor != null && cursor.moveToFirst()) {
                // 获取列索引，提高查询效率
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int typeIndex = cursor.getColumnIndexOrThrow(COLUMN_TYPE);
                int imageUrlIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL);
                int titleIndex = cursor.getColumnIndexOrThrow(COLUMN_TITLE);
                int descriptionIndex = cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION);
                int priceIndex = cursor.getColumnIndexOrThrow(COLUMN_PRICE);
                int tagsIndex = cursor.getColumnIndexOrThrow(COLUMN_TAGS);
                int videoUrlIndex = cursor.getColumnIndexOrThrow(COLUMN_VIDEO_URL);
                int imgWidthIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGE_WIDTH);
                int imgHeightIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGE_HEIGHT);
                do {
                    FeedItem item = new FeedItem();
                    item.setId(cursor.getString(idIndex));
                    item.setType(cursor.getInt(typeIndex));
                    item.setImageUrl(cursor.getString(imageUrlIndex));
                    item.setTitle(cursor.getString(titleIndex));
                    item.setDescription(cursor.getString(descriptionIndex));
                    item.setPrice(cursor.getString(priceIndex));

                    // 反序列化tags字段
                    String tagsString = cursor.getString(tagsIndex);
                    if (tagsString != null && !tagsString.isEmpty()) {
                        try {
                            List<String> tags = JSON.parseObject(tagsString, new com.alibaba.fastjson2.TypeReference<List<String>>() {});
                            item.setTags(tags);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to parse tags", e);
                        }
                    }
                    item.setVideoUrl(cursor.getString(videoUrlIndex));
                    item.setImgWidth(cursor.getInt(imgWidthIndex));
                    item.setImgHeight(cursor.getInt(imgHeightIndex));
                    feedItems.add(item);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error querying feed items with pagination", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return feedItems;
    }
}
