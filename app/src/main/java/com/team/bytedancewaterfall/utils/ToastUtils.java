package com.team.bytedancewaterfall.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Toast工具类，防止连续点击导致Toast连续弹出
 */
public class ToastUtils {

    private static Toast mToast;
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static boolean isToastShowing = false;

    /**
     * 显示Toast，防止连续弹出
     * @param context 上下文
     * @param message 消息内容
     * @param duration 显示时长
     */
    public static void showToast(final Context context, final String message, final int duration) {
        // 如果当前Toast正在显示，直接返回
        if (isToastShowing) {
            return;
        }

        // 在主线程中显示Toast
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showToastOnMainThread(context, message, duration);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showToastOnMainThread(context, message, duration);
                }
            });
        }
    }

    /**
     * 在主线程中显示Toast
     */
    private static void showToastOnMainThread(Context context, String message, int duration) {
        // 标记Toast开始显示
        isToastShowing = true;

        // 如果Toast已存在，复用它
        if (mToast == null) {
            mToast = Toast.makeText(context, message, duration);
        } else {
            mToast.setText(message);
            mToast.setDuration(duration);
        }

        // 显示Toast
        mToast.show();

        // 计算Toast显示的时长
        long delayTime = duration == Toast.LENGTH_SHORT ? 2000 : 3500;
        
        // 设置延时任务，当Toast显示完成后重置标记
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isToastShowing = false;
            }
        }, delayTime);
    }

    /**
     * 显示短时Toast
     */
    public static void showShortToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    /**
     * 显示长时Toast
     */
    public static void showLongToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

    /**
     * 取消当前Toast
     */
    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
            isToastShowing = false;
        }
    }
}