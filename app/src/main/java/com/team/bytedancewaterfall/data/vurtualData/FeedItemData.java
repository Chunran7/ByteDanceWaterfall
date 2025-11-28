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
    private static final String TAG = "FeedItemData";
    public static List<FeedItem> feedItemList;
    static{
        feedItemList = new ArrayList<>();
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0,  "drawable://"+R.drawable.test1_0, "陶瓷水杯", "精品套餐水杯，泡茶接待客人使用", "19.90", Arrays.asList("陶瓷", "杯子"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 1,  "drawable://"+R.drawable.test1_1, "保温杯", "保温杯定制印logo开业宣传广告杯子公司员工活动纪念礼品水杯刻字", "19.80", Arrays.asList("保温", "水杯", "不锈钢"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0,  "drawable://"+R.drawable.test1_2, "双层玻璃杯", "定制广告杯子双层玻璃杯赠品水杯印字保温杯定做礼品杯印logo茶杯", "9.90", Arrays.asList("定制logo", "水杯", "双层玻璃"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0,  "drawable://"+R.drawable.test1_3, "健身运动水杯", "Tritan吨桶吨大容量水杯男运动健身2025年新款水壶耐高温大号杯子", "29.90", Arrays.asList("PC材质", "大容量水杯", "可拆卸吸管"), null));

        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 1,  "drawable://"+R.drawable.test_2, "台灯", "宿舍可用暖色光台灯，可充电使用", "49.99", Arrays.asList("台灯", "500mhA容量", "多模式"), null));

        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0,  "drawable://"+R.drawable.test_3, "办公打印套装", "A4纸+订书机套装，先打印再装订，完美契合", "25.50", Arrays.asList("A4纸", "订书机", "办公用品"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 2, null, "席梦思床垫", "美式复古皮艺床软包床头双人床现代简约卧室家具收纳大床床头柜", "2000.00", Arrays.asList("家具", "床垫"),"drawable://"+R.raw.video_test_1));

        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 1,  "drawable://"+R.drawable.test_4, "电动牙刷", "最新科技电动牙刷，送配套漱口水杯", "69.90", Arrays.asList("牙刷", "全自动", "牙齿清洁"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 1,  "drawable://"+R.drawable.test_5, "抑菌科技毛巾", "100%新疆棉，5A级抑菌毛巾洗脸巾，3条装", "29.9", Arrays.asList("毛巾", "纯棉", "抑菌"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0,  "drawable://"+R.drawable.test_6, "居家拖鞋", "踩屎感凉拖鞋，夏季款可外穿", "15.00", Arrays.asList("拖鞋", "凉爽"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 1,  "drawable://"+R.drawable.test_7, "笔记本", "皮革面商务笔记本，2025新款，书写流畅", "15.00", Arrays.asList("办公", "笔记本", "商务"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 2, "drawable://"+R.drawable.test_11, "意式极简布艺沙发", "意式极简布艺沙发弧形客厅大户型别墅设计师异形转角海盐沙发原创", "1500.00", Arrays.asList("家具", "沙发"),"drawable://"+R.raw.video_test_2));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0,  "drawable://"+R.drawable.test_8, "高颜值双肩大容量背包", "双肩包男士背包大容量电脑包通勤出差休闲旅行包防水轻便书包，2025新款", "119.00", Arrays.asList("双肩包", "大容量", "轻便"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0,  "drawable://"+R.drawable.test_9, "全自动雨伞加大加厚加固晴雨两用", "全自动雨伞加大加厚加固晴雨两用男士折叠女太阳男生自动伞定制，2025新款", "39.90", Arrays.asList("雨伞", "全自动", "可折叠"), null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0,  "drawable://"+R.drawable.test_10, "可爱猫猫书包挂件", "金属钥匙扣挂件定制logo企业周年校庆纪念礼品定做文创景区钥匙链订制", "9.90", Arrays.asList("挂件", "可定制", "文创"), null));

        // 视频数据初始化
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 2, "drawable://"+R.drawable.test2_1, "大疆Action 6", "DJI大疆 Action6 新品运动相机骑行潜水旅游挂脖防抖vlog户外", "2999.00", Arrays.asList("大疆DJ", "相机", "运动"),"drawable://"+R.raw.video_dj_3));

    }
   /* public static List<FeedItem> getFeedItemList() {
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
    }*/

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
                        values.put("imageUrl", copyFileToPrivateDirByType(context, item.getImageUrl(), "image", 0));
                    }
                    values.put("title", item.getTitle());
                    values.put("description", item.getDescription());
                    values.put("price", item.getPrice());
                    // 序列化 tags 列表
                    values.put("tags", JSON.toJSONString(item.getTags()));
                    if (item.getVideoUrl() == null) {
                        values.put("videoUrl", item.getVideoUrl());
                    }else {
                        values.put("videoUrl", copyFileToPrivateDirByType(context, item.getVideoUrl(), "video", 1));
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
     * @param type 文件类型 0为图片，1为视频
     * @return 成功时返回私有目录中的文件路径，失败返回null
     */
    public static String copyDrawableToPrivateDir(Context context, int drawableResId, String subDir, int type) {
        if (drawableResId <= 0) {
            Log.e(TAG, "无效的drawable资源ID");
            return null;
        }
        InputStream inputStream = null;
        try {
            // 获取drawable资源的输入流
            inputStream = context.getResources().openRawResource(drawableResId);
            String resultPath = null;
            if (type == 0) {
                // 保存为PNG格式图片到私有目录
                resultPath = PrivateMediaStorageManager.savePngImageToPrivateDir(
                        context,
                        inputStream,
                        subDir
                );
            }
            if (type == 1) {
                // 保存为MP4格式视频到私有目录
                resultPath = PrivateMediaStorageManager.saveVideoToPrivateDir(
                        context,
                        inputStream,
                        subDir
                );
            }
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
     * @param type 文件类型 0为图片，1为视频
     * @return 成功时返回私有目录中的文件路径，失败返回null
     */
    public static String copyFileToPrivateDirByType(Context context, String sourceFilePath, String subDir, Integer type) {
        if (sourceFilePath == null || sourceFilePath.isEmpty()) {
            Log.e(TAG, "源文件路径为空");
            return null;
        }

        // 检查是否为drawable资源
        if (sourceFilePath.startsWith("drawable://")) {
            try {
                String resourceIdStr = sourceFilePath.substring("drawable://".length());
                int resourceId = Integer.parseInt(resourceIdStr);
                return copyDrawableToPrivateDir(context, resourceId, subDir, type);
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
}
