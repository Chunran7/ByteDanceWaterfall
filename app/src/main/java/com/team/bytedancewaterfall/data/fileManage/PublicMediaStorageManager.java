package com.team.bytedancewaterfall.data.fileManage;


import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class PublicMediaStorageManager {
    // 应用专属目录名（建议用应用英文名，避免中文）
    private static final String APP_DIR_NAME = "ByteDanceWaterfall";

    /**
     * 保存图片到公共目录（DCIM/应用名/）
     * @param context 上下文
     * @param inputStream 图片输入流（网络下载、相机拍摄等）
     * @param subDir 子目录（如 "feed_cover"、"avatar"，可为 null）
     * @return 媒体文件的 Content Uri（推荐存储到数据库，用于渲染/分享），失败返回 null
     */
    public static String saveImageToPublicDir(Context context, InputStream inputStream, String subDir) {
        return saveMediaToPublicDir(
                context,
                inputStream,
                subDir,
                Environment.DIRECTORY_DCIM, // 系统图片目录（DCIM）
                "img",
                ".jpg",
                "image/jpeg"
        );
    }

    /**
     * 保存视频到公共目录（Movies/应用名/）
     * @param context 上下文
     * @param inputStream 视频输入流
     * @param subDir 子目录
     * @return 媒体文件的 Content Uri，失败返回 null
     */
    public static String saveVideoToPublicDir(Context context, InputStream inputStream, String subDir) {
        return saveMediaToPublicDir(
                context,
                inputStream,
                subDir,
                Environment.DIRECTORY_MOVIES, // 系统视频目录（Movies）
                "video",
                ".mp4",
                "video/mp4"
        );
    }

    /**
     * 公共媒体存储核心逻辑
     */
    private static String saveMediaToPublicDir(
            Context context,
            InputStream inputStream,
            String subDir,
            String systemDir,
            String filePrefix,
            String fileSuffix,
            String mimeType
    ) {
        // 1. 校验外部存储是否可用
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            android.widget.Toast.makeText(context, "外部存储不可用", android.widget.Toast.LENGTH_SHORT).show();
            return null;
        }

        // 2. 创建目录：系统目录 → 应用目录 → 子目录（如 DCIM/ByteDanceWaterfall/feed_cover/）
        File rootDir = Environment.getExternalStoragePublicDirectory(systemDir);
        File appDir = new File(rootDir, APP_DIR_NAME);
        File targetDir = appDir;
        if (subDir != null && !subDir.isEmpty()) {
            targetDir = new File(appDir, subDir);
        }

        // 3. 递归创建目录（若不存在）
        if (!targetDir.exists()) {
            // Android 10+ 无需申请 WRITE_EXTERNAL_STORAGE 即可创建应用专属目录
            boolean mkdirSuccess = targetDir.mkdirs();
            if (!mkdirSuccess) {
                android.util.Log.e("PublicStorage", "创建目录失败：" + targetDir.getAbsolutePath());
                return null;
            }
        }

        // 4. 生成唯一文件名（前缀_时间戳_随机数.后缀，避免重复）
        String fileName = String.format(
                Locale.CHINA,
                "%s_%s_%s%s",
                filePrefix,
                new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date()),
                UUID.randomUUID().toString().substring(0, 6), // 6位随机数
                fileSuffix
        );

        // 5. 写入文件到公共目录
        File targetFile = new File(targetDir, fileName);
        try (InputStream is = inputStream;
             java.io.FileOutputStream fos = new java.io.FileOutputStream(targetFile)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // 6. 通知系统扫描文件（关键！让相册识别到新文件）
        MediaScannerConnection.scanFile(
                context,
                new String[]{targetFile.getAbsolutePath()},
                new String[]{mimeType},
                (path, uri) -> {
                    // 扫描完成后的回调（uri 是系统分配的 Content Uri，可用于渲染/分享）
                    Log.d("PublicStorage", "文件扫描成功，Uri：" + uri);
                }
        );

        // 7. 返回文件的绝对路径（或 Content Uri 的字符串形式，推荐存储 Uri）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7+ 推荐存储 Content Uri（通过 FileProvider 生成，避免文件权限问题）
            return getContentUriFromFile(context, targetFile).toString();
        } else {
            // Android 6-：存储绝对路径（兼容性更好）
            return targetFile.getAbsolutePath();
        }
    }

    /**
     * 将文件转换为 Content Uri（Android 7+ 必须，避免 FileUriExposedException）
     */
    private static android.net.Uri getContentUriFromFile(Context context, File file) {
        // 需在 res/xml/file_paths.xml 中配置公共目录路径
        return androidx.core.content.FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider", // 与 AndroidManifest 中一致
                file
        );
    }
}