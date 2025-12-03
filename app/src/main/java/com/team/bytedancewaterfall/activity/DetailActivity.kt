package com.team.bytedancewaterfall.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.team.bytedancewaterfall.R
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem
import com.team.bytedancewaterfall.data.service.impl.CartServiceImpl
import com.team.bytedancewaterfall.data.service.impl.UserServiceImpl
import com.team.bytedancewaterfall.utils.ToastUtils

/**
 * 详情页Activity
 * 用于展示FeedItem的详细信息
 */
class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FEED_ITEM = "extra_feed_item"
    }
    private lateinit var mediaScrollView: HorizontalScrollView
    private lateinit var mediaContainer: LinearLayout
    private lateinit var tvDetailTitle: TextView
    private lateinit var tvDetailPrice: TextView
    private lateinit var tvDetailDescription: TextView
    private lateinit var chipGroupTags: ChipGroup
    private lateinit var tvDetailId: TextView
    private lateinit var btnAddToCart: Button
    private lateinit var btnBuyNow: Button
    private var currentFeedItem: FeedItem? = null
    private var currentOperatingFeedItem: FeedItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // 初始化视图
        initViews()

        // 获取传递的FeedItem数据
        val feedItem = intent.getSerializableExtra(EXTRA_FEED_ITEM) as? FeedItem

        if (feedItem != null) {
            // 绑定数据到视图
            bindData(feedItem)
        } else {
            // 如果没有数据，显示错误并返回
            finish()
        }
    }
    private val loginLauncher = registerForActivityResult<Intent?, ActivityResult?>(
        StartActivityForResult(),
        ActivityResultCallback { result: ActivityResult? ->
            if (result!!.getResultCode() == RESULT_OK) {
                // 登录成功，重新获取当前用户并继续购物车操作
                var user = UserServiceImpl.getInstance().getCurrentUser(this)
                if (user != null) {
                    // 可以在这里执行添加到购物车的逻辑
                    user = UserServiceImpl.getInstance().getCurrentUser(this)
                    if (user == null) {
                        ToastUtils.showShortToast(this, "请先登录")
                        return@ActivityResultCallback
                    }
                    // 预留加入购物车方法调用
                    CartServiceImpl.getInstance().addCart(
                        this,
                        currentOperatingFeedItem,
                        user.getId()
                    )
                }
            }
        }
    )
    /**
     * 初始化所有视图组件
     */
    private fun initViews() {
        mediaScrollView = findViewById(R.id.media_scroll_view)
        mediaContainer = findViewById(R.id.media_container)
//        btnBack = findViewById(R.id.btn_back)
        tvDetailTitle = findViewById(R.id.tv_detail_title)
        tvDetailPrice = findViewById(R.id.tv_detail_price)
        tvDetailDescription = findViewById(R.id.tv_detail_description)
        chipGroupTags = findViewById(R.id.chip_group_tags)
        tvDetailId = findViewById(R.id.tv_detail_id)
        
        // 初始化购物车和购买按钮
        btnAddToCart = findViewById(R.id.btn_add_to_cart)
        btnBuyNow = findViewById(R.id.btn_buy_now)
        
        // 设置按钮点击事件, 添加到购物车
        btnAddToCart.setOnClickListener {
            currentFeedItem?.let { item ->
                // 获取当前用户
                val user = UserServiceImpl.getInstance().getCurrentUser(this)
                if (user == null) {
                    // 保存当前要操作的商品信息
                    currentOperatingFeedItem = item
                    // 跳转到登录页面等待结果
                    val intent = Intent(this, LoginActivity::class.java)
                    loginLauncher.launch(intent)
                    return@setOnClickListener
                }
                // 加入购物车方法调用
                CartServiceImpl.getInstance().addCart(this, item, user.id)
            }
        }
        // 购买按钮
        btnBuyNow.setOnClickListener {
            // TODO 预留立即购买方法调用
            ToastUtils.showShortToast(this, "购买方法开发中")
//            currentFeedItem?.let { item ->
//                PurchaseUtils.buyNow(this, item)
//            }
        }
        
        // 添加滚动监听器
        mediaScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            handleMediaScroll(scrollX)
        }
    }
    /**
     * 将FeedItem数据绑定到视图
     */
    private fun bindData(feedItem: FeedItem) {
        // 保存当前FeedItem引用
        currentFeedItem = feedItem
        // 清空媒体容器
        mediaContainer.removeAllViews()
        val screenWidth = resources.displayMetrics.widthPixels
        // 只添加一次视频播放器，避免重复添加
        if (feedItem.type == 2 && feedItem.videoUrl != null) {
            // 创建视频容器
            val videoContainer = FrameLayout(this).apply {
                layoutParams = ViewGroup.LayoutParams(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT)
            }

            val videoView = VideoView(this).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }

            videoContainer.addView(videoView)

            // 添加 MediaController 控制器
            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)
            videoView.setVideoPath(feedItem.videoUrl)

            mediaContainer.addView(videoContainer)
            // 自动播放
            videoView.start()
        }
        // 添加图片（如果存在）
        if (feedItem.imageUrl != null) {
            val imageView = ImageView(this).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                layoutParams = ViewGroup.LayoutParams(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT)
            }

            Glide.with(this)
                .load(feedItem.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView)

            mediaContainer.addView(imageView)
        }
        // 设置标题
        tvDetailTitle.text = feedItem.title ?: "无标题"

        // 设置价格（如果有）
        if (feedItem.price != null && feedItem.price.isNotEmpty()) {
            tvDetailPrice.visibility = View.VISIBLE
            tvDetailPrice.text = "￥"+feedItem.price
        } else {
            tvDetailPrice.visibility = View.GONE
        }

        // 设置描述（如果有）
        if (feedItem.description != null && feedItem.description.isNotEmpty()) {
            tvDetailDescription.visibility = View.VISIBLE
            tvDetailDescription.text = feedItem.description
        } else {
            tvDetailDescription.visibility = View.GONE
        }

        // 设置标签（如果有）
        if (feedItem.tags != null && feedItem.tags.isNotEmpty()) {
            chipGroupTags.visibility = View.VISIBLE
            chipGroupTags.removeAllViews()
            
            feedItem.tags.forEach { tag ->
                val chip = Chip(this)
                chip.text = tag
                chip.isClickable = false
                chip.isCheckable = false
                chipGroupTags.addView(chip)
            }
        } else {
            chipGroupTags.visibility = View.GONE
        }
        
        // 设置ID信息
        tvDetailId.text = "商品ID: ${feedItem.id}"
    }

    override fun onPause() {
        super.onPause()
        // 暂停所有视频播放
        pauseAllVideos()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放视频资源
        releaseAllVideos()
    }

    private fun pauseAllVideos() {
        for (i in 0 until mediaContainer.childCount) {
            val child = mediaContainer.getChildAt(i)
            if (child is FrameLayout) {
                for (j in 0 until child.childCount) {
                    val subChild = child.getChildAt(j)
                    if (subChild is VideoView) {
                        if (subChild.isPlaying) {
                            subChild.pause()
                        }
                    }
                }
            }
        }
    }

    private fun releaseAllVideos() {
        for (i in 0 until mediaContainer.childCount) {
            val child = mediaContainer.getChildAt(i)
            if (child is FrameLayout) {
                for (j in 0 until child.childCount) {
                    val subChild = child.getChildAt(j)
                    if (subChild is VideoView) {
                        subChild.stopPlayback()
                    }
                }
            }
        }
    }
    private fun handleMediaScroll(scrollX: Int) {
        val screenWidth = resources.displayMetrics.widthPixels
        if (screenWidth <= 0) return

        val currentPage = scrollX / screenWidth

        // 重置所有视频播放状态
        pauseAllVideos()

        // 只播放当前页面的视频
        if (currentPage < mediaContainer.childCount) {
            val currentChild = mediaContainer.getChildAt(currentPage)
            if (currentChild is FrameLayout) { // 视频容器
                for (i in 0 until currentChild.childCount) {
                    val child = currentChild.getChildAt(i)
                    if (child is VideoView) {
                        // 准备并播放视频
                        if (!child.isPlaying) {
                            child.start()
                        }
                    }
                }
            }
        }
    }
}
