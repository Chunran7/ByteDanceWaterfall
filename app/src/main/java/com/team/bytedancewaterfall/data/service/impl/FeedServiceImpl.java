package com.team.bytedancewaterfall.data.service.impl;

import com.team.bytedancewaterfall.data.entity.FeedItem;
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
}
