// PrivateMediaStorageManager.java
package com.team.bytedancewaterfall.data.fileManage;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * 私有媒体文件存储管理器
 * 提供将图片、视频等媒体文件保存到应用私有目录的功能
 */
public class PrivateMediaStorageManager {
    private static final String TAG = "PrivateStorage";

    /**
     * 保存JPG图片到应用私有目录
     * @param context 上下文对象，用于获取应用私有目录
     * @param inputStream 图片输入流
     * @param subDir 子目录名称，如果为null或空则直接保存在根私有目录下
     * @return 成功时返回文件的绝对路径，失败返回null
     */
    public static String saveJpegImageToPrivateDir(Context context, InputStream inputStream, String subDir) {
        return saveMediaToPrivateDir(
                context,
                inputStream,
                subDir,
                "img",
                ".jpg",
                "image/jpeg"
        );
    }

    /**
     * 保存JPEG图片到应用私有目录（别名方法）
     * @param context 上下文对象，用于获取应用私有目录
     * @param inputStream 图片输入流
     * @param subDir 子目录名称，如果为null或空则直接保存在根私有目录下
     * @return 成功时返回文件的绝对路径，失败返回null
     */
    public static String saveJpgImageToPrivateDir(Context context, InputStream inputStream, String subDir) {
        return saveJpegImageToPrivateDir(context, inputStream, subDir);
    }

    /**
     * 保存PNG图片到应用私有目录
     * @param context 上下文对象，用于获取应用私有目录
     * @param inputStream PNG图片输入流
     * @param subDir 子目录名称，如果为null或空则直接保存在根私有目录下
     * @return 成功时返回文件的绝对路径，失败返回null
     */
    public static String savePngImageToPrivateDir(Context context, InputStream inputStream, String subDir) {
        return saveMediaToPrivateDir(
                context,
                inputStream,
                subDir,
                "img",
                ".png",
                "image/png"
        );
    }

    /**
     * 保存视频到应用私有目录
     * @param context 上下文对象，用于获取应用私有目录
     * @param inputStream 视频输入流
     * @param subDir 子目录名称，如果为null或空则直接保存在根私有目录下
     * @return 成功时返回文件的绝对路径，失败返回null
     */
    public static String saveVideoToPrivateDir(Context context, InputStream inputStream, String subDir) {
        return saveMediaToPrivateDir(
                context,
                inputStream,
                subDir,
                "video",
                ".mp4",
                "video/mp4"
        );
    }

    /**
     * 将媒体文件保存到应用私有目录的核心实现方法
     * @param context 上下文对象，用于获取应用私有目录
     * @param inputStream 媒体文件输入流
     * @param subDir 子目录名称，可以为null
     * @param filePrefix 文件名前缀（如"img"、"video"）
     * @param fileSuffix 文件扩展名（如".jpg"、".mp4"）
     * @param mimeType 媒体文件MIME类型（目前未使用但保留以备后续扩展）
     * @return 成功时返回文件的绝对路径，失败返回null
     */
    private static String saveMediaToPrivateDir(
            Context context,
            InputStream inputStream,
            String subDir,
            String filePrefix,
            String fileSuffix,
            String mimeType
    ) {
        try {
            // 获取应用私有目录
            File privateDir = context.getExternalFilesDir(null);
            if (privateDir == null) {
                privateDir = context.getFilesDir();
            }

            // 根据文件类型创建不同的子目录
            String typeDirName = filePrefix.startsWith("img") ? "images" : "videos";
            File typeDir = new File(privateDir, typeDirName);

            // 创建最终的目标目录
            File targetDir = typeDir;
            if (subDir != null && !subDir.isEmpty()) {
                targetDir = new File(typeDir, subDir);
            }

            // 递归创建目录（若不存在）
            if (!targetDir.exists()) {
                boolean mkdirSuccess = targetDir.mkdirs();
                if (!mkdirSuccess) {
                    Log.e(TAG, "创建目录失败：" + targetDir.getAbsolutePath());
                    return null;
                }
            }

            // 生成唯一文件名：前缀_时间戳_UUID片段.后缀
            String fileName = String.format(
                    Locale.CHINA,
                    "%s_%s_%s%s",
                    filePrefix,
                    new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date()),
                    UUID.randomUUID().toString().substring(0, 6),
                    fileSuffix
            );

            // 写入文件到私有目录
            File targetFile = new File(targetDir, fileName);
            try (InputStream is = inputStream;
                 java.io.FileOutputStream fos = new java.io.FileOutputStream(targetFile)) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
            }

            // 返回文件绝对路径
            return targetFile.getAbsolutePath();

        } catch (Exception e) {
            Log.e(TAG, "保存媒体文件失败", e);
            return null;
        }
    }
}
