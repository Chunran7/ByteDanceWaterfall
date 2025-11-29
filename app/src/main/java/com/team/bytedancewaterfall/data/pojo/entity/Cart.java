package com.team.bytedancewaterfall.data.pojo.entity;

import android.content.ContentValues;

import java.util.Date;

/**
 * 购物车表
 */
public class Cart {
    private String id;
    // 用户id
    private String userId;
    // 商品id
    private String feedItemId;
    // 数量
    private int count;
    // 创建时间
    private String updateTime;

    public Cart(String id, String userId, String feedItemId, int count) {
        this.id = id;
        this.userId = userId;
        this.feedItemId = feedItemId;
        this.count = count;
    }

    public Cart() {
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("userId", userId);
        values.put("feedItemId", feedItemId);
        values.put("count", count);
        values.put("updateTime", updateTime);
        return values;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFeedItemId() {
        return feedItemId;
    }

    public void setFeedItemId(String feedItemId) {
        this.feedItemId = feedItemId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
