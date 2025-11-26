package com.team.bytedancewaterfall.data.vurtualData;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.team.bytedancewaterfall.data.fileManage.PrivateMediaStorageManager.savePngImageToPrivateDir;
import static com.team.bytedancewaterfall.data.fileManage.PrivateMediaStorageManager.saveVideoToPrivateDir;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson2.JSON;
import com.team.bytedancewaterfall.R;
import com.team.bytedancewaterfall.data.database.FeedItemDatabaseHelper;
import com.team.bytedancewaterfall.data.fileManage.PrivateMediaStorageManager;
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;
import com.team.bytedancewaterfall.data.service.FeedService;
//import com.team.bytedancewaterfall.data.service.impl.FeedServiceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0,  "drawable://"+R.drawable.test_1, "Stylish Watch", "A very stylish watch for modern people.", "$99.99", Arrays.asList("Fashion", "Accessory", "Men's Style"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 1,  "drawable://"+R.drawable.test_2, "Beautiful Landscape", "Captured this amazing view during my trip.", null, Arrays.asList("Travel", "Nature", "Photography"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0,  "drawable://"+R.drawable.test_3, "Comfortable Shoes", "Perfect for running and daily activities.", "$75.50", Arrays.asList("Sports", "Running"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 1,  "drawable://"+R.drawable.test_4, "City at Night", (String)null, (String)null, Arrays.asList("Cityscape", "Night", "Urban"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 1,  "drawable://"+R.drawable.test_5, "Delicious Food", "Best pasta I've ever had!", null, Arrays.asList("Food", "Recipe", "Italian"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0,  "drawable://"+R.drawable.test_6, "A Very Long Title That Is Meant to Wrap Around to Multiple Lines", null, "$12.00", null, null));
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
                    if (item.getImageUrl() == null) {
                        values.put("imageUrl", item.getImageUrl());
                    }else {
                        values.put("imageUrl", copyFileToPrivateDirByType(context, item.getImageUrl(), "image"));
                    }
                    values.put("title", item.getTitle());
                    values.put("description", item.getDescription());
                    values.put("price", item.getPrice());
                    // 序列化 tags 列表
                    values.put("tags", JSON.toJSONString(item.getTags()));
                    if (item.getVideoUrl() == null) {
                        values.put("videoUrl", item.getVideoUrl());
                    }else {
                        values.put("videoUrl", copyFileToPrivateDirByType(context, item.getVideoUrl(), "video"));
                    }
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
    /**
     * 从drawable资源复制图片到应用私有目录
     * @param context 上下文对象
     * @param drawableResId drawable资源ID
     * @param subDir 子目录名称（可选）
     * @return 成功时返回私有目录中的文件路径，失败返回null
     */
    public static String copyDrawableToPrivateDir(Context context, int drawableResId, String subDir) {
        if (drawableResId <= 0) {
            Log.e(TAG, "无效的drawable资源ID");
            return null;
        }
        InputStream inputStream = null;
        try {
            // 获取drawable资源的输入流
            inputStream = context.getResources().openRawResource(drawableResId);

            // 保存为PNG格式图片到私有目录
            String resultPath = PrivateMediaStorageManager.savePngImageToPrivateDir(
                    context,
                    inputStream,
                    subDir
            );

            inputStream.close();
            Log.d(TAG, "Drawable资源复制成功: " + resultPath);
            return resultPath;

        } catch (Exception e) {
            Log.e(TAG, "复制drawable资源到私有目录失败", e);
            return null;
        }finally {
            try {
                if (inputStream != null){
                inputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * 根据文件路径或资源标识识别文件类型并将文件复制到应用私有目录
     * @param context 上下文对象
     * @param sourceFilePath 源文件路径或资源标识
     * @param subDir 子目录名称（可选）
     * @return 成功时返回私有目录中的文件路径，失败返回null
     */
    public static String copyFileToPrivateDirByType(Context context, String sourceFilePath, String subDir) {
        if (sourceFilePath == null || sourceFilePath.isEmpty()) {
            Log.e(TAG, "源文件路径为空");
            return null;
        }

        // 检查是否为drawable资源
        if (sourceFilePath.startsWith("drawable://")) {
            try {
                String resourceIdStr = sourceFilePath.substring("drawable://".length());
                int resourceId = Integer.parseInt(resourceIdStr);
                return copyDrawableToPrivateDir(context, resourceId, subDir);
            } catch (Exception e) {
                Log.e(TAG, "解析drawable资源ID失败: " + sourceFilePath, e);
                return null;
            }
        }
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            Log.e(TAG, "源文件不存在: " + sourceFilePath);
            return null;
        }

        try {
            // 根据文件扩展名判断文件类型
            String fileName = sourceFile.getName().toLowerCase();
            InputStream inputStream = new FileInputStream(sourceFile);

            String resultPath = null;

            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                // JPEG图片文件
                resultPath = PrivateMediaStorageManager.saveJpgImageToPrivateDir(
                        context,
                        inputStream,
                        subDir
                );
            } else if (fileName.endsWith(".png")) {
                // PNG图片文件
                resultPath = savePngImageToPrivateDir(
                        context,
                        inputStream,
                        subDir);
            } else if (fileName.endsWith(".mp4")) {
                // MP4视频文件
                resultPath = saveVideoToPrivateDir(
                        context,
                        inputStream,
                        subDir
                );
            } else {
                Log.w(TAG, "不支持的文件类型: " + fileName);
                inputStream.close();
                return null;
            }

            inputStream.close();
            Log.d(TAG, "文件复制成功: " + resultPath);
            return resultPath;

        } catch (Exception e) {
            Log.e(TAG, "复制文件到私有目录失败: " + sourceFilePath, e);
            return null;
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
