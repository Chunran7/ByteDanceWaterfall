// TestMediaUploadActivity.java
package com.team.bytedancewaterfall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.team.bytedancewaterfall.R;
import com.team.bytedancewaterfall.data.fileManage.PublicMediaStorageManager;
import com.team.bytedancewaterfall.data.vurtualData.FeedItemData;

import java.io.File;

public class TestMediaUploadActivity extends AppCompatActivity {
    private static final int REQUEST_MEDIA_UPLOAD = 1001;
    
    private Button uploadMediaButton;
    private TextView resultPathTextView;
    private ImageView previewImageView;
    private TextView mediaTypeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_media_upload);
        FeedItemData.initDatabase(this);
        initViews();
    }

    private void initViews() {
        uploadMediaButton = findViewById(R.id.upload_media_button);
        resultPathTextView = findViewById(R.id.result_path_text);
        previewImageView = findViewById(R.id.preview_image);
        mediaTypeTextView = findViewById(R.id.media_type_text);
        
        uploadMediaButton.setOnClickListener(v -> startMediaUploadActivity());
    }

    private void startMediaUploadActivity() {
        Intent intent = new Intent(this, MediaUploadActivity.class);
        startActivityForResult(intent, REQUEST_MEDIA_UPLOAD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_MEDIA_UPLOAD && resultCode == RESULT_OK && data != null) {
            String mediaPath = data.getStringExtra("media_path");
            boolean isImage = data.getBooleanExtra("is_image", true);
            
            // 显示结果
            resultPathTextView.setText("存储路径: " + mediaPath);
            mediaTypeTextView.setText("媒体类型: " + (isImage ? "图片" : "视频"));
            
            // 如果是图片，尝试显示预览
            if (isImage && mediaPath != null) {
                // 注意：这里可能需要根据实际路径处理显示逻辑
                // 如果是Content URI可能需要特殊处理
                if (mediaPath.startsWith("content://")) {
                    previewImageView.setImageURI(android.net.Uri.parse(mediaPath));
                } else {
                    // 处理文件路径
                    File imageFile = new File(mediaPath);
                    if (imageFile.exists()) {
                        previewImageView.setImageURI(android.net.Uri.fromFile(imageFile));
                    }
                }
            } else if (!isImage) {
                // 视频显示占位符
                previewImageView.setImageResource(R.drawable.ic_videocam_black_24dp);
            }
            
            Toast.makeText(this, "媒体文件上传成功", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_MEDIA_UPLOAD && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "操作已取消", Toast.LENGTH_SHORT).show();
        }
    }
}
