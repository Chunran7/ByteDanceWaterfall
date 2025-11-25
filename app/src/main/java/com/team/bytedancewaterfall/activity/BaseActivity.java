package com.team.bytedancewaterfall.activity;

import android.Manifest;
import android.os.Build;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    // 权限申请启动器
    private ActivityResultLauncher<String[]> permissionLauncher;
    // 权限申请回调（成功后执行存储/渲染操作）
    private Runnable onPermissionGranted;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化权限申请器
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    for (Boolean granted : result.values()) {
                        if (!granted) {
                            allGranted = false;
                            break;
                        }
                    }
                    if (allGranted && onPermissionGranted != null) {
                        onPermissionGranted.run();
                    } else {
                        // 权限被拒，提示用户（可选）
                        android.widget.Toast.makeText(this, "需要存储权限才能保存资源", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * 申请公共目录存储/访问权限
     * @param isImage 是否申请图片权限（true=图片，false=视频）
     * @param callback 权限通过后的回调
     */
    public void requestMediaPermission(boolean isImage, Runnable callback) {
        this.onPermissionGranted = callback;
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+：精准权限
            permissions = isImage ?
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES} :
                    new String[]{Manifest.permission.READ_MEDIA_VIDEO};
        } else {
            // Android 6.0-12：读写权限
            permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }
        // 启动权限申请
        permissionLauncher.launch(permissions);
    }
}