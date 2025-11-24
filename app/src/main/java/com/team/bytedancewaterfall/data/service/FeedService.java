package com.team.bytedancewaterfall.data.service;

import android.content.Context;

import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;

import java.util.List;

public interface FeedService {
    // 返回所有的瀑布流数据
    List<FeedItem> getFeedList();
    // 添加瀑布流数据
    boolean addFeedItem(FeedItem feedItem);
    // 根据id删除瀑布流数据
    boolean removeFeedItem(String id);
    // 更新瀑布流数据
    boolean updateFeedItem(FeedItem feedItem);
    // 查询某个tag标签的数据
    List<FeedItem> getFeedListByTag(String tag);
    // 分页查询 page为第几页，size为每页大小
    List<FeedItem> pageQueryFeedList(Integer page, Integer size);
    // 本地数据库查询数据
    List<FeedItem> getFeedList(Context context);
    // 本地数据库添加数据
    boolean addFeedItem(Context context,FeedItem feedItem);
}
