package com.team.bytedancewaterfall.data.service.impl;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.team.bytedancewaterfall.data.database.FeedItemDatabaseHelper;
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;
import com.team.bytedancewaterfall.data.service.FeedService;
import com.team.bytedancewaterfall.data.vurtualData.FeedItemData;

import java.util.Collections;
import java.util.List;

import cn.javaex.htool.core.string.StringUtils;

public class FeedServiceImpl implements FeedService {
    private static FeedServiceImpl instance;
    public static FeedServiceImpl getInstance() {
        if (instance == null) {
            synchronized (FeedServiceImpl.class) {
                if (instance == null) {
                    instance = new FeedServiceImpl();
                }
            }
        }
        return instance;
    }
    private FeedServiceImpl(){

    }
/*    @Override
    public List<FeedItem> getFeedList() {
        // TODO 暂时采用本地虚拟数据
        return FeedItemData.getFeedItemList();
    }*/
    public List<FeedItem> getFeedList(Context context) {
        FeedItemDatabaseHelper dbHelper = new FeedItemDatabaseHelper(context);
        List<FeedItem> feedItems = dbHelper.getAllFeedItems();
        return feedItems;
    }

    @Override
    public boolean addFeedItem(Context context, FeedItem feedItem) {
        if (feedItem == null) {
            // 没有插入对象
            Log.e("FeedServiceImpl", "addFeedItem: 插入对象为空");
            return false;
        }
        FeedItemDatabaseHelper dbHelper = new FeedItemDatabaseHelper(context);
        return dbHelper.insertFeedItem(feedItem);
    }

    @Override
    public boolean removeFeedItem(Context context, List<String> ids) {
        if (ids != null && !ids.isEmpty()) {
            FeedItemDatabaseHelper dbHelper = new FeedItemDatabaseHelper(context);
            return dbHelper.deleteById(ids);
        }
        return false;
    }

    @Override
    public boolean updateFeedItem(Context context, FeedItem feedItem) {
        if (feedItem != null && StringUtils.isNotEmpty(feedItem.getId())) {
            FeedItemDatabaseHelper dbHelper = new FeedItemDatabaseHelper(context);
            return dbHelper.updateFeedItem(feedItem);
        }
        return false;
    }

    @Override
    public FeedItem getFeedItemById(Context context, String id) {
        if (StringUtils.isNotEmpty(id)) {
            FeedItemDatabaseHelper dbHelper = new FeedItemDatabaseHelper(context);
            return dbHelper.getFeedItemById(id);
        }
        return null;
    }

    @Override
    public List<FeedItem> pageQueryFeedList(Context context, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        FeedItemDatabaseHelper dbHelper = new FeedItemDatabaseHelper(context);
        return dbHelper.pageQueryFeedList(page, size);
    }

   /* @Override
    public boolean addFeedItem(FeedItem feedItem) {
        if (StringUtils.isNotEmpty(feedItem.getId())) {
            return FeedItemData.addFeedItem(feedItem);
        }
        return false;
    }

    @Override
    public boolean removeFeedItem(String id) {
        if (StringUtils.isNotEmpty(id)) {
            return FeedItemData.removeFeedItem(id);
        }
        return false;
    }

    @Override
    public boolean updateFeedItem(FeedItem feedItem) {
        if (StringUtils.isNotEmpty(feedItem.getId())) {
            return FeedItemData.updateFeedItem(feedItem);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public List<FeedItem> getFeedListByTag(String tag) {
        if (StringUtils.isEmpty(tag)) {
            return Collections.emptyList();
        }
        return FeedItemData.getFeedItemList().stream()
                .filter(item -> item.getTags().contains(tag))
                .toList();
    }

    @Override
    public List<FeedItem> pageQueryFeedList(Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        // 开始的数据条数
        int begin = (page - 1) * size;
        if (begin > FeedItemData.getFeedItemList().size()) {
            // 超过数据范围
            return Collections.emptyList();
        }
        if (begin + size <= FeedItemData.getFeedItemList().size()) {
            // 获取指定范围内的数据
            return FeedItemData.getFeedItemList().subList(begin, begin + size);
        }
        return Collections.emptyList();
    }*/
}
