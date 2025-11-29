package com.team.bytedancewaterfall.data.service;

import android.content.Context;

import com.team.bytedancewaterfall.data.pojo.entity.Cart;
import com.team.bytedancewaterfall.data.pojo.vo.CartAndFeed;

import java.util.List;

public interface CartService {
    boolean addCartItem(Context context, String feedItemId, String userId);
    List<Cart> getListByUserId(Context context, String userId);
    // 获取用户所有购物车数据并封装对应的FeedItem信息
    List<CartAndFeed> getListByUserIdWithFeedItem(Context context, String userId);
    // 修改购物车信息
    boolean updateCartItem(Context context, Cart cart);
    // 删除购物车信息
    boolean deleteCartItem(Context context, String cartId);
}
