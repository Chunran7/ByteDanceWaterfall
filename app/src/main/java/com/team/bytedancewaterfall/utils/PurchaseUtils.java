package com.team.bytedancewaterfall.utils;

import android.content.Context;
import android.widget.Toast;

import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;

/**
 * 购物车和购买功能工具类
 * 提供通用的购物车和购买相关功能
 */
public class PurchaseUtils {
    /**
     * 显示Toast消息
     * @param context 上下文
     * @param message 消息内容
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}