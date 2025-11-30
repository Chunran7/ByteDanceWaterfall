package com.team.bytedancewaterfall.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SPUtils {
    // 全局单例实例
    private static volatile SPUtils INSTANCE;
    // SharedPreferences 实例（全局唯一）
    private final SharedPreferences sp;
    // 异步执行器（处理耗时操作，避免主线程阻塞）
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    // 主线程 Handler（用于回调结果）
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // 私有化构造方法
    private SPUtils(Context context, String spName) {
        // 使用 Application Context 避免内存泄漏
        this.sp = context.getApplicationContext()
                .getSharedPreferences(spName, Context.MODE_PRIVATE);
    }

    // 获取全局单例（指定 SP 文件名，默认用包名）
    public static SPUtils getInstance(Context context) {
        return getInstance(context, context.getPackageName() + "_preferences");
    }

    public static SPUtils getInstance(Context context, String spName) {
        if (INSTANCE == null) {
            synchronized (SPUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SPUtils(context, spName);
                }
            }
        }
        return INSTANCE;
    }

    // -------------------- 同步操作（建议子线程调用）--------------------
    public void putString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public void putInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public void putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public void putLong(String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    public long getLong(String key, long defValue) {
        return sp.getLong(key, defValue);
    }

    public void putFloat(String key, float value) {
        sp.edit().putFloat(key, value).apply();
    }

    public float getFloat(String key, float defValue) {
        return sp.getFloat(key, defValue);
    }

    public void putStringSet(String key, Set<String> value) {
        sp.edit().putStringSet(key, value).apply();
    }

    public Set<String> getStringSet(String key, Set<String> defValue) {
        return sp.getStringSet(key, defValue);
    }

    public Map<String, ?> getAll() {
        return sp.getAll();
    }

    public void remove(String key) {
        sp.edit().remove(key).apply();
    }

    public void clear() {
        sp.edit().clear().apply();
    }

    // -------------------- 异步操作（带回调，主线程触发）--------------------
    public void putStringAsync(String key, String value, OnResultCallback callback) {
        executor.execute(() -> {
            putString(key, value);
            mainHandler.post(() -> callback.onResult(true));
        });
    }

    public void getStringAsync(String key, String defValue, OnResultCallback<String> callback) {
        executor.execute(() -> {
            String result = getString(key, defValue);
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    // 回调接口
    public interface OnResultCallback<T> {
        void onResult(T result);
    }

    // 销毁资源（如 Activity/Fragment 销毁时调用）
    public void destroy() {
        executor.shutdown();
        mainHandler.removeCallbacksAndMessages(null);
    }
}