package com.team.bytedancewaterfall.data.service.impl;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;
import com.team.bytedancewaterfall.data.service.FeedService;
import com.team.bytedancewaterfall.data.vurtualData.FeedItemData;

import java.util.Collections;
import java.util.List;

import cn.javaex.htool.core.string.StringUtils;

public class FeedServiceImpl implements FeedService {
    @Override
    public List<FeedItem> getFeedList() {
        // TODO 暂时采用本地虚拟数据
        return FeedItemData.getFeedItemList();
    }

    @Override
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
    }
}
