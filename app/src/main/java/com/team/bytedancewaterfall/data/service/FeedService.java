package com.team.bytedancewaterfall.data.service;

import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;

import java.util.List;

public interface FeedService {
    // 返回瀑布流数据
    List<FeedItem> getFeedList();
    // 添加瀑布流数据
    boolean addFeedItem(FeedItem feedItem);
    // 根据id删除瀑布流数据
    boolean removeFeedItem(String id);
    // 更新瀑布流数据
    boolean updateFeedItem(FeedItem feedItem);
    // 查询某个tag标签的数据
    List<FeedItem> getFeedListByTag(String tag);
}
