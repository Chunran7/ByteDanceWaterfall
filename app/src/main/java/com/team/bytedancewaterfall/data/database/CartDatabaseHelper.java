package com.team.bytedancewaterfall.data.database;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.alibaba.fastjson2.JSON;
import com.team.bytedancewaterfall.data.pojo.entity.Cart;
import com.team.bytedancewaterfall.data.pojo.vo.CartAndFeed;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车数据库操作类
 */
public class CartDatabaseHelper {
    private static final String TAG = "CartDatabaseHelper";
    public static final String TABLE_NOTES = "cart";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "userId";
    public static final String COLUMN_FEED_ITEM_ID = "feedItemId";
    public static final String COLUMN_COUNT = "count";
    public static final String COLUMN_UPDATE_TIME = "updateTime";
    public static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NOTES + "(" +
                    COLUMN_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_USER_ID + " TEXT NOT NULL, " +
                    COLUMN_FEED_ITEM_ID + " TEXT NOT NULL, " +
                    COLUMN_COUNT + " INTEGER NOT NULL, " +
                    COLUMN_UPDATE_TIME + " TEXT NOT NULL DEFAULT (datetime('now', 'localtime')) "+
                    ")";
    private SQLiteDatabase db;
    public CartDatabaseHelper(Context context) {
        this.db = AppDatabaseHelper.getInstance(context).getWritableDatabase();
    }

    /**
     * 加入购物车
     * @param cart
     * @return
     */
    public boolean insertCartItem(Cart cart) {
        db.beginTransaction();
        long insNum = 0l;
        try {
            insNum = db.insert(TABLE_NOTES, null, cart.toContentValues());
            if (insNum > 0) {
                db.setTransactionSuccessful();
                return true;
            }
            return false;
        }catch (SQLiteException e) {
            Log.e(TAG, "Error inserting cart item", e);
        }finally {
            db.endTransaction();
        }
        return false;
    }
    /**
     * 修改购物车数据，当购物车中已有该商品时，数量加1
     */
    public boolean updateCartItem(Cart cart) {
        db.beginTransaction();
        long updateNum = 0l;
        try {
            updateNum = db.update(TABLE_NOTES, cart.toContentValues(), COLUMN_ID + " = ?", new String[]{cart.getId()});
            if (updateNum > 0) {
                db.setTransactionSuccessful();
                return true;
            }
            return false;
        }catch (SQLiteException e) {
            Log.e(TAG, "Error updating cart item", e);
        }finally {
            db.endTransaction();
        }
        return false;
    }

    /**
     * 根据商品id和用户id查询购物车数据
     * @param feedItemId
     * @param userId
     * @return
     */
    public Cart getCartBy2Id(String feedItemId, String userId) {
        try {
            Cursor cursor = db.query(TABLE_NOTES, null, COLUMN_FEED_ITEM_ID + " = ? AND " + COLUMN_USER_ID + " = ?",
                    new String[]{feedItemId, userId}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // 查到了数据
                Cart cart = new Cart();
                cart.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                cart.setUserId(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
                cart.setFeedItemId(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_ITEM_ID)));
                cart.setCount(cursor.getInt(cursor.getColumnIndex(COLUMN_COUNT)));
                cart.setUpdateTime(cursor.getString(cursor.getColumnIndex(COLUMN_UPDATE_TIME)));
                cursor.close();
                return cart;
            }
        }catch (SQLiteException e) {
            Log.e(TAG, "Error querying cart item", e);
        }
        return null;
    }

    /**
     * 查询用户的购物车数据，按照updateTime倒序
     * @param userId
     * @return
     */
    public List<Cart> getListByUserId(String userId) {
        List<Cart> cartList = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABLE_NOTES, null, COLUMN_USER_ID + " = ?",
                    new String[]{userId}, null, null, COLUMN_UPDATE_TIME + " DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Cart cart = new Cart();
                    cart.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                    cart.setUserId(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
                    cart.setFeedItemId(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_ITEM_ID)));
                    cart.setCount(cursor.getInt(cursor.getColumnIndex(COLUMN_COUNT)));
                    cart.setUpdateTime(cursor.getString(cursor.getColumnIndex(COLUMN_UPDATE_TIME)));
                    cartList.add(cart);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLiteException e) {
            Log.e(TAG, "Error querying cart item", e);
        }
        return cartList;
    }
    /**
     * 联表查询，当前用户的购物车数据，并返回对应的商品信息
     * @param userId
     */
    public List<CartAndFeed> getListByUserIdWithFeedItem(String userId) {
        List<CartAndFeed> cartList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "select cart.count, feed.type, feed.imageUrl,feed.title, feed.description, feed.price, feed.tags, feed.videoUrl" +
                    " from " + CartDatabaseHelper.TABLE_NOTES + " as cart " +
                    "left join " + FeedItemDatabaseHelper.TABLE_NOTES + " as feed " +
                    "on cart." + CartDatabaseHelper.COLUMN_FEED_ITEM_ID + " = feed." + FeedItemDatabaseHelper.COLUMN_ID +
                    " where cart." + CartDatabaseHelper.COLUMN_USER_ID + " = ?";
            cursor = db.rawQuery(sql, new String[]{userId});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    CartAndFeed cartAndFeed = new CartAndFeed();
                    cartAndFeed.setCount(cursor.getInt(cursor.getColumnIndex(CartDatabaseHelper.COLUMN_COUNT)));
                    cartAndFeed.setType(cursor.getInt(cursor.getColumnIndex(FeedItemDatabaseHelper.COLUMN_TYPE)));
                    cartAndFeed.setImageUrl(cursor.getString(cursor.getColumnIndex(FeedItemDatabaseHelper.COLUMN_IMAGE_URL)));
                    cartAndFeed.setTitle(cursor.getString(cursor.getColumnIndex(FeedItemDatabaseHelper.COLUMN_TITLE)));
                    cartAndFeed.setDescription(cursor.getString(cursor.getColumnIndex(FeedItemDatabaseHelper.COLUMN_DESCRIPTION)));
                    cartAndFeed.setPrice(cursor.getString(cursor.getColumnIndex(FeedItemDatabaseHelper.COLUMN_PRICE)));
                    String tagsString = cursor.getString(cursor.getColumnIndexOrThrow("tags"));
                    if (tagsString != null && !tagsString.isEmpty()) {
                        try {
                            List<String> tags = JSON.parseObject(tagsString, new com.alibaba.fastjson2.TypeReference<List<String>>() {});
                            cartAndFeed.setTags(tags);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to parse tags", e);
                        }
                    }
                    cartAndFeed.setVideoUrl(cursor.getString(cursor.getColumnIndex(FeedItemDatabaseHelper.COLUMN_VIDEO_URL)));
                    // 计算总价格
                    cartAndFeed.setTotalPrice(String.format("%.2f", cartAndFeed.getCount() * Double.parseDouble(cartAndFeed.getPrice())));
                    cartList.add(cartAndFeed);
                } while (cursor.moveToNext());
                cursor.close();
            }

        }catch (SQLiteException e) {
            Log.e(TAG, "Error querying cart item", e);
            if (cursor != null) {
                cursor.close();
            }
        }
        return cartList;
    }

    public boolean deleteById(String cartId) {
        return db.delete(TABLE_NOTES, COLUMN_ID + " = ?", new String[]{cartId}) > 0;
    }
}
