package com.team.bytedancewaterfall.data.pojo.vo;

import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;

public class CartAndFeed extends FeedItem {
    // 购物车中商品数量
    private int count;
    private String totalPrice;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
