package com.team.bytedancewaterfall.data.service.impl;

import android.content.Context;

import com.team.bytedancewaterfall.activity.FeedScrollActivity;
import com.team.bytedancewaterfall.data.database.CartDatabaseHelper;
import com.team.bytedancewaterfall.data.pojo.entity.Cart;
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;
import com.team.bytedancewaterfall.data.pojo.entity.User;
import com.team.bytedancewaterfall.data.pojo.vo.CartAndFeed;
import com.team.bytedancewaterfall.data.service.CartService;
import com.team.bytedancewaterfall.utils.TimeUtil;
import com.team.bytedancewaterfall.utils.ToastUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import cn.javaex.htool.core.string.StringUtils;

public class CartServiceImpl implements CartService {

    private static final String TAG = "CartServiceImpl";
    private static CartService cartService;
    private CartServiceImpl() {}
    public static CartService getInstance() {
        if (cartService == null) {
            synchronized (CartServiceImpl.class) {
                if (cartService == null) {
                    cartService = new CartServiceImpl();
                }
            }
        }
        return cartService;
    }
    @Override
    public boolean addCartItem(Context context, String feedItemId, String userId) {
        CartDatabaseHelper dbHelper = new CartDatabaseHelper(context);
        Cart cart = dbHelper.getCartBy2Id(feedItemId, userId);
        if (cart == null) {
            // 购物车中没有该商品，插入
            cart = new Cart();
            cart.setFeedItemId(feedItemId);
            cart.setUserId(userId);
            cart.setCount(1);
            cart.setId(UUID.randomUUID().toString());
            cart.setUpdateTime(TimeUtil.getCurTime());
            return dbHelper.insertCartItem(cart);
        }else {
            // 本身有该商品，数量加1
            cart.setCount(cart.getCount() + 1);
            cart.setUpdateTime(TimeUtil.getCurTime());
            return dbHelper.updateCartItem(cart);
        }
    }
    public void addCart(Context context, FeedItem feedItem, String userId) {
        boolean b = CartServiceImpl.getInstance().addCartItem(context, feedItem.getId(), userId);
        if (b) {
            // 显示Toast提示
            ToastUtils.showShortToast(context, "加入购物车成功");
        } else {
            ToastUtils.showShortToast(context, "加入购物车失败");
        }
    }
    @Override
    public List<Cart> getListByUserId(Context context, String userId) {
        if (StringUtils.isEmpty(userId)) {
            // 用户id为空
            return Collections.emptyList();
        }
        CartDatabaseHelper dbHelper = new CartDatabaseHelper(context);
        return dbHelper.getListByUserId(userId);
    }

    @Override
    public List<CartAndFeed> getListByUserIdWithFeedItem(Context context, String userId) {
        if (StringUtils.isEmpty(userId)){
            return Collections.emptyList();
        }
        CartDatabaseHelper dbHelper = new CartDatabaseHelper(context);
        List<CartAndFeed> res = dbHelper.getListByUserIdWithFeedItem(userId);
        return res;
    }

    @Override
    public boolean updateCartItem(Context context, Cart cart) {
        if (cart == null || StringUtils.isEmpty(cart.getId())) {
            return false;
        }
        CartDatabaseHelper dbHelper = new CartDatabaseHelper(context);
        return dbHelper.updateCartItem(cart);
    }

    @Override
    public boolean deleteCartItem(Context context, String cartId) {
        if (StringUtils.isEmpty(cartId)) {
            return false;
        }
        CartDatabaseHelper dbHelper = new CartDatabaseHelper(context);
        return dbHelper.deleteById(cartId);
    }
}
