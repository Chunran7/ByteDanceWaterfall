package com.team.bytedancewaterfall.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.team.bytedancewaterfall.R
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem

/**
 * 详情页Activity
 * 用于展示FeedItem的详细信息
 */
class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FEED_ITEM = "extra_feed_item"
    }

    private lateinit var ivDetailImage: ImageView
    private lateinit var btnBack: ImageButton
    private lateinit var tvDetailTitle: TextView
    private lateinit var tvDetailPrice: TextView
    private lateinit var tvDetailDescription: TextView
    private lateinit var chipGroupTags: ChipGroup
    private lateinit var tvDetailId: TextView

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

        // 设置返回按钮点击事件
        btnBack.setOnClickListener {
            finish()
        }
    }

    /**
     * 初始化所有视图组件
     */
    private fun initViews() {
        ivDetailImage = findViewById(R.id.iv_detail_image)
        btnBack = findViewById(R.id.btn_back)
        tvDetailTitle = findViewById(R.id.tv_detail_title)
        tvDetailPrice = findViewById(R.id.tv_detail_price)
        tvDetailDescription = findViewById(R.id.tv_detail_description)
        chipGroupTags = findViewById(R.id.chip_group_tags)
        tvDetailId = findViewById(R.id.tv_detail_id)
    }

    /**
     * 将FeedItem数据绑定到视图
     */
    private fun bindData(feedItem: FeedItem) {
        // 加载图片
        Glide.with(this)
            .load(feedItem.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(ivDetailImage)

        // 设置标题
        tvDetailTitle.text = feedItem.title ?: "无标题"

        // 设置价格（如果有）
        if (feedItem.price != null && feedItem.price.isNotEmpty()) {
            tvDetailPrice.visibility = View.VISIBLE
            tvDetailPrice.text = feedItem.price
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
        tvDetailId.text = "ID: ${feedItem.id}"

        // 根据类型显示不同的信息
        when (feedItem.type) {
            0 -> {
                // 商品类型
                // 已经显示了价格和描述
            }
            1 -> {
                // 活动类型
                // 可以添加特殊的活动信息显示
            }
            2 -> {
                // 视频类型
                // 可以添加视频播放功能
                if (feedItem.videoUrl != null) {
                    // TODO: 添加视频播放器
                }
            }
        }
    }
}
