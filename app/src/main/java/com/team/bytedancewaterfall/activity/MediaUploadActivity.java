// MediaUploadActivity.java
package com.team.bytedancewaterfall.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.team.bytedancewaterfall.R;
import com.team.bytedancewaterfall.data.fileManage.PublicMediaStorageManager;

import java.io.InputStream;
/**
 * 媒体上传活动页面，用于拍照、录像或从相册选择图片/视频，并将其保存到公共目录。
 * 支持接收外部应用分享的媒体文件（如图片、视频）并处理。
 */
public class MediaUploadActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;
    private static final int REQUEST_PICK_IMAGE = 3;
    private static final int REQUEST_PICK_VIDEO = 4;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private ImageView previewImageView;
    private TextView resultTextView;
    private Button captureImageButton;
    private Button captureVideoButton;
    private Button pickImageButton;
    private Button pickVideoButton;
    private Button confirmButton;

    private Uri currentMediaUri;
    private String savedFilePath;
    private boolean isImage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_upload);

        initViews();
        requestPermissions();

        // 检查是否有通过Intent传入的文件
        handleIncomingIntent();
    }
        /**
         * 初始化视图组件，设置点击监听器，并初始化确认按钮状态为不可用。
         */
        private void initViews() {
            // 绑定界面控件
            previewImageView = findViewById(R.id.preview_image);
            resultTextView = findViewById(R.id.result_text);
            captureImageButton = findViewById(R.id.capture_image_button);
            captureVideoButton = findViewById(R.id.capture_video_button);
            pickImageButton = findViewById(R.id.pick_image_button);
            pickVideoButton = findViewById(R.id.pick_video_button);
            confirmButton = findViewById(R.id.confirm_button);

            // 设置按钮点击事件
            captureImageButton.setOnClickListener(v -> dispatchTakePictureIntent());
            captureVideoButton.setOnClickListener(v -> dispatchTakeVideoIntent());
            pickImageButton.setOnClickListener(v -> pickImageFromGallery());
            pickVideoButton.setOnClickListener(v -> pickVideoFromGallery());
            confirmButton.setOnClickListener(v -> returnResultAndFinish());

            // 初始状态下禁用确认按钮
            confirmButton.setEnabled(false);
        }

        /**
         * 请求必要的运行时权限：相机和读取外部存储权限。
         */
        private void requestPermissions() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        PERMISSION_REQUEST_CODE);
            }
        }

        /**
         * 处理由其他应用发送过来的共享意图（ACTION_SEND），支持图像和视频类型。
         */
        private void handleIncomingIntent() {
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    handleSendImage(intent); // 处理接收到的图片
                } else if (type.startsWith("video/")) {
                    handleSendVideo(intent); // 处理接收到的视频
                }
            }
        }

        /**
         * 处理接收到的图片数据，显示预览并开始处理流程。
         *
         * @param intent 包含图片 URI 的 Intent 对象
         */
        private void handleSendImage(Intent intent) {
            Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (imageUri != null) {
                currentMediaUri = imageUri;
                previewImageView.setImageURI(imageUri);
                isImage = true;
                processMediaFile(); // 启动文件处理线程
            }
        }

        /**
         * 处理接收到的视频数据，在界面上展示图标表示视频，并启动处理流程。
         *
         * @param intent 包含视频 URI 的 Intent 对象
         */
        private void handleSendVideo(Intent intent) {
            Uri videoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (videoUri != null) {
                currentMediaUri = videoUri;
                previewImageView.setImageResource(R.drawable.ic_videocam_black_24dp);
                isImage = false;
                processMediaFile(); // 启动文件处理线程
            }
        }

        /**
         * 调用系统相机进行拍照操作。
         */
        private void dispatchTakePictureIntent() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

        /**
         * 调用系统摄像机录制视频。
         */
        private void dispatchTakeVideoIntent() {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        }

        /**
         * 打开图库以供用户选取一张图片。
         */
        private void pickImageFromGallery() {
            Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pickIntent.setType("image/*");
            startActivityForResult(pickIntent, REQUEST_PICK_IMAGE);
        }

        /**
         * 打开图库以供用户选取一个视频。
         */
        private void pickVideoFromGallery() {
            Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pickIntent.setType("video/*");
            startActivityForResult(pickIntent, REQUEST_PICK_VIDEO);
        }

        /**
         * 根据请求码和结果码处理子 Activity 返回的数据。
         *
         * @param requestCode 请求标识符
         * @param resultCode  结果状态码
         * @param data        返回的数据 Intent
         */
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK && data != null) {
                switch (requestCode) {
                    case REQUEST_IMAGE_CAPTURE:
                    case REQUEST_PICK_IMAGE:
                        isImage = true;
                        currentMediaUri = data.getData();
                        if (currentMediaUri == null && data.getExtras() != null) {
                            // 处理某些相机应用返回的Bitmap数据
                            currentMediaUri = data.getParcelableExtra("data");
                        }
                        previewImageView.setImageURI(currentMediaUri);
                        processMediaFile();
                        break;
                    case REQUEST_VIDEO_CAPTURE:
                    case REQUEST_PICK_VIDEO:
                        isImage = false;
                        currentMediaUri = data.getData();
                        previewImageView.setImageResource(R.drawable.ic_videocam_black_24dp);
                        processMediaFile();
                        break;
                }
            }
        }

        /**
         * 在后台线程中将当前选中的媒体文件复制到指定的公共目录下保存。
         * 成功后更新 UI 显示路径并启用确认按钮。
         */
        private void processMediaFile() {
            if (currentMediaUri != null) {
                new Thread(() -> {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(currentMediaUri);
                        String filePath;

                        if (isImage) {
                            filePath = PublicMediaStorageManager.saveImageToPublicDir(
                                    this, inputStream, "uploaded_images");
                        } else {
                            filePath = PublicMediaStorageManager.saveVideoToPublicDir(
                                    this, inputStream, "uploaded_videos");
                        }

                        runOnUiThread(() -> {
                            savedFilePath = filePath;
                            resultTextView.setText("文件已保存至: " + filePath);
                            confirmButton.setEnabled(true);
                            Toast.makeText(this, "文件保存成功", Toast.LENGTH_SHORT).show();
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "保存文件失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                }).start();
            }
        }

        /**
         * 将保存后的文件路径作为结果返回给调用方，并结束当前 Activity。
         */
        private void returnResultAndFinish() {
            if (savedFilePath != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("media_path", savedFilePath);
                resultIntent.putExtra("is_image", isImage);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }

        /**
         * 处理权限申请的结果。如果未获得所有必需权限，则提示用户。
         *
         * @param requestCode  权限请求码
         * @param permissions  请求的权限列表
         * @param grantResults 授权结果数组
         */
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == PERMISSION_REQUEST_CODE) {
                boolean allGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }
                }

                if (!allGranted) {
                    Toast.makeText(this, "需要必要权限才能正常使用功能", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

