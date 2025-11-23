package com.team.bytedancewaterfall.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 瀑布流数据项
 * @param id 唯一标识
 * @param type 卡片类型：0=双列商品, 1=大图活动卡, 2=视频商品
 * @param imageUrl 内容图片
 * @param title 标题
 * @param description 描述 (可选)
 * @param price 价格 (可选, 例如商品)
 * @param tags 标签列表 (可选)
 */
@Parcelize
data class FeedItem(
    val id: String,
    val type: Int,
    val imageUrl: String,
    val title: String,
    val description: String? = null,
    val price: String? = null,
    val tags: List<String> = emptyList()
) : Parcelable
