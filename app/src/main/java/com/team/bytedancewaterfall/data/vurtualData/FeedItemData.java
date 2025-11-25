package com.team.bytedancewaterfall.data.vurtualData;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson2.JSON;
import com.team.bytedancewaterfall.data.database.FeedItemDatabaseHelper;
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;
import com.team.bytedancewaterfall.data.service.FeedService;
//import com.team.bytedancewaterfall.data.service.impl.FeedServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class FeedItemData {
    public static List<FeedItem> feedItemList;
    static{
        feedItemList = new ArrayList<>();
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0, "https://picsum.photos/id/10/300/400", "Stylish Watch", "A very stylish watch for modern people.", "$99.99", Arrays.asList("Fashion", "Accessory", "Men's Style"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 1, "https://picsum.photos/id/20/300/500", "Beautiful Landscape", "Captured this amazing view during my trip.", null, Arrays.asList("Travel", "Nature", "Photography"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0, "https://picsum.photos/id/30/300/450", "Comfortable Shoes", "Perfect for running and daily activities.", "$75.50", Arrays.asList("Sports", "Running"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 1, "https://picsum.photos/id/40/300/420", "City at Night", (String)null, (String)null, Arrays.asList("Cityscape", "Night", "Urban"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 1, "https://picsum.photos/id/50/300/480", "Delicious Food", "Best pasta I've ever had!", null, Arrays.asList("Food", "Recipe", "Italian"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0, "https://picsum.photos/id/60/300/430", "A Very Long Title That Is Meant to Wrap Around to Multiple Lines", null, "$12.00", null, null));
//        feedItemList.forEach(System.out::println);
    }
    public static List<FeedItem> getFeedItemList() {
        return feedItemList;
    }
    public static boolean addFeedItem(FeedItem feedItem) {
        if (feedItem == null) {
            return false;
        }
        // 添加到列表中
        return feedItemList.add(feedItem);
    }
    public static boolean removeFeedItem(String id) {
        if (id == null) {
            return false;
        }
        // 从列表中移除
        return feedItemList.removeIf(item -> item.getId().equals(id));
    }
    public static boolean updateFeedItem(FeedItem feedItem) {
        if (feedItem == null) {
            return false;
        }
        // 在列表中找到匹配的项并更新
        for (int i = 0; i < feedItemList.size(); i++) {
            FeedItem item = feedItemList.get(i);
            if (item.getId().equals(feedItem.getId())) {
                feedItemList.set(i, feedItem);
                return true;
            }
        }
        return false;
    }

    // 提供一个方法，在应用启动时由拥有 Context 的组件调用
    public static void initDatabase(Context context) {
        FeedItemDatabaseHelper dbHelper = new FeedItemDatabaseHelper(context);
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase(); // 这会触发 onCreate 如果数据库不存在
            // 检查是否已经有数据，避免重复插入
            int count = dbHelper.getCount();
//            addTest(context);
            if (count > 0) {
                Log.d(TAG, "Database already exists. Skipping initialization.");
                return;
            }
            // 这里简化处理，每次启动都清空并重新插入
            // 实际项目中应根据需求判断是否需要清空
//            db.delete(FeedItemDatabaseHelper.TABLE_NOTES, null, null);
            db.beginTransaction();
            try {
                for (FeedItem item : feedItemList) {
                    ContentValues values = new ContentValues();
                    values.put("id", item.getId());
                    values.put("type", item.getType());
                    values.put("imageUrl", item.getImageUrl());
                    values.put("title", item.getTitle());
                    values.put("description", item.getDescription());
                    values.put("price", item.getPrice());
                    // 序列化 tags 列表
                    values.put("tags", JSON.toJSONString(item.getTags()));
                    values.put("videoUrl", item.getVideoUrl());

                    long result = db.insert(FeedItemDatabaseHelper.TABLE_NOTES, null, values);
                    if (result == -1) {
                        Log.e(TAG, "Failed to insert item with id: " + item.getId());
                    }
                }
                db.setTransactionSuccessful();
                Log.d(TAG, "Database initialized with mock data.");
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
    /*public static void addTest(Context context) {
        FeedItem feedItem = new FeedItem();
        feedItem.setId(UUID.randomUUID().toString());
        feedItem.setType(0);
        feedItem.setImageUrl("https://picsum.photos/id/10/300/400");
        feedItem.setTitle("Stylish Watch");
        feedItem.setDescription("A very stylish watch for modern people.");
        feedItem.setPrice("$99.99");
        feedItem.setTags(Arrays.asList("Fashion", "Accessory", "Men's Style"));
        FeedService feedService = FeedServiceImpl.getInstance();
        boolean res = feedService.addFeedItem(context, feedItem);
        System.out.println(feedItem.getId()+"addTest:"+ res);
    }*/
}
