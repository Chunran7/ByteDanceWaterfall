package com.team.bytedancewaterfall.activity;

import static com.team.bytedancewaterfall.activity.LoginActivity.USER_TOKEN;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team.bytedancewaterfall.R;
import com.team.bytedancewaterfall.data.fileManage.PrivateMediaStorageManager;
import com.team.bytedancewaterfall.data.pojo.entity.User;
import com.team.bytedancewaterfall.data.service.impl.UserServiceImpl;
import com.team.bytedancewaterfall.utils.SPUtils;
import com.team.bytedancewaterfall.utils.ToastUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import java.io.File;

public class MyInfoActivity extends BaseBottomNavActivity {
    private ImageView userIconView;
    private TextView userNameView;
    private Button logoutButton;
    private Button logintButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_info_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
        setView();
        setListener();
        updateLoginBotton();
        initBottomNavigation();
    }

    // 拍照和选择图片的请求码
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private String currentPhotoPath; // 保存拍照后的图片路径

    private void setListener() {
        // 设置各项监听
        // 头像点击事件
        userIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userToken = SPUtils.getInstance(MyInfoActivity.this).getString(USER_TOKEN, "");
                if (userToken.isEmpty()) {
                    ToastUtils.showShortToast(MyInfoActivity.this, "请先登录");
                    return;
                }
                showAvatarOptionDialog();
            }
        });

        // 登出按钮监听
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示登出确认弹窗
                new AlertDialog.Builder(MyInfoActivity.this)
                        .setTitle("确认退出登录")
                        .setMessage("确定要退出登录吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 用户确认退出，删除token
                                SPUtils.getInstance(MyInfoActivity.this).remove(USER_TOKEN);
                                Intent intent = new Intent(MyInfoActivity.this, MyInfoActivity.class);
                                startActivity(intent);
                                updateLoginBotton();
                                setView();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 用户取消退出，关闭对话框
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });
        logintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInfoActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        // 浏览历史按钮监听
        findViewById(R.id.brow_history_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到浏览历史界面
                ToastUtils.showShortToast(MyInfoActivity.this, "功能暂未实现");
            }
        });
        findViewById(R.id.person_info_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到个人信息界面
                ToastUtils.showShortToast(MyInfoActivity.this, "功能暂未实现");
            }
        });
        findViewById(R.id.my_collection_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到我的收藏界面
                ToastUtils.showShortToast(MyInfoActivity.this, "功能暂未实现");
            }
        });
        findViewById(R.id.settings_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到设置界面
                ToastUtils.showShortToast(MyInfoActivity.this, "功能暂未实现");
            }
        });
        findViewById(R.id.about_us_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到关于我们
                ToastUtils.showShortToast(MyInfoActivity.this, "功能暂未实现");
            }
        });
        findViewById(R.id.feedback_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到意见反馈
                ToastUtils.showShortToast(MyInfoActivity.this, "功能暂未实现");
            }
        });
    }

    /**
     * 显示头像选择对话框
     */
    private void showAvatarOptionDialog() {
        String[] options = {"拍照", "从相册选择"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择头像");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // 拍照
                        dispatchTakePictureIntent();
                        break;
                    case 1:
                        // 从相册选择
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, REQUEST_GALLERY);
                        break;
                }
            }
        });
        builder.create().show();
    }

    /**
     * 启动相机拍照
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 确保有相机应用可以处理这个Intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // 创建一个临时文件来保存照片
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // 错误发生时处理
                Log.e("MyInfoActivity", "Error creating image file", ex);
            }
            // 继续只有当文件被成功创建时
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.team.bytedancewaterfall.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    /**
     * 创建临时图片文件
     */
    private File createImageFile() throws IOException {
        // 创建一个唯一的文件名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* 前缀 */
                ".jpg",         /* 后缀 */
                storageDir      /* 目录 */
        );

        // 保存文件的绝对路径
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * 处理相机和相册返回的结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String imagePath = null;

            if (requestCode == REQUEST_CAMERA) {
                // 从相机返回
                try {
                    // 使用PrivateMediaStorageManager保存图片到私有目录
                    FileInputStream fis = new FileInputStream(currentPhotoPath);
                    imagePath = PrivateMediaStorageManager.saveJpgImageToPrivateDir(this, fis, "avatars");
                    fis.close();
                } catch (IOException e) {
                    Log.e("MyInfoActivity", "Error saving camera image", e);
                }
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                // 从相册返回
                Uri selectedImage = data.getData();
                try {
                    // 使用PrivateMediaStorageManager保存图片到私有目录
                    InputStream is = getContentResolver().openInputStream(selectedImage);
                    if (is != null) {
                        imagePath = PrivateMediaStorageManager.saveJpgImageToPrivateDir(this, is, "avatars");
                        is.close();
                    }
                } catch (IOException e) {
                    Log.e("MyInfoActivity", "Error saving gallery image", e);
                }
            }

            if (imagePath != null) {
                // 更新数据库中的头像路径
                String userId = UserServiceImpl.getInstance().getCurrentUser(this).getId();
                UserServiceImpl.getInstance().updateUserAvatar(this, userId, imagePath);
                // 刷新界面
                setView();
            }
        }
    }

    /**
     * 设置组件动态属性
     */
    private void setView() {
        // 获取当前用户
        String userToken = SPUtils.getInstance(this).getString(USER_TOKEN, "");
        // 获取用户
        User user  = UserServiceImpl.getInstance().getUserByToken(this, userToken);
        if (user == null) {
            // 未登录，显示默认头像
            userIconView.setBackgroundResource(R.drawable.person);
            userNameView.setText("未登录用户");
            return;
        }
        // 已登录，显示用户头像和昵称
        // 设置用户名
        userNameView.setText(user.getNickname());

        // 尝试从用户对象的avatar字段加载头像（文件路径）
        String avatarPath = user.getAvatar();
        if (avatarPath != null) {
            Glide.with(this).load(avatarPath).into(userIconView);
            return;
        }

        // 如果用户没有头像信息或头像加载失败，设置默认头像
        userIconView.setBackgroundResource(R.drawable.person);
        userIconView.setImageDrawable(null); // 清空ImageBitmap，确保显示背景资源

    }

    /**
     * 初始化组件
     */
    private void initView() {
        userIconView = findViewById(R.id.user_icon);
        userNameView = findViewById(R.id.user_name);
        logoutButton = findViewById(R.id.login_out);
        logintButton = findViewById(R.id.login_btn);
    }
    private void updateLoginBotton() {
        // 判断当前是否已登录
        boolean isLogin = UserServiceImpl.getInstance().getCurrentUser(this) != null;
        if (isLogin) {
            // 已登录，显示登出按钮
            logoutButton.setVisibility(View.VISIBLE);
            logintButton.setVisibility(View.GONE);
        }else {
            // 未登录，显示登录按钮
            logoutButton.setVisibility(View.GONE);
            logintButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
