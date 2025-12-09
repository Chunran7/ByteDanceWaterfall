package com.team.bytedancewaterfall.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.team.bytedancewaterfall.R
import com.team.bytedancewaterfall.activity.PromotionZoneActivity
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem

/**
 * Feed流适配器类，用于在RecyclerView中展示瀑布流内容
 * @param feedItems 需要展示的FeedItem数据列表
 */
class FeedAdapter(private val feedItems: List<FeedItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null

    // 定义视图类型常量
    companion object {
        const val TYPE_NORMAL = 0 // 普通双列商品
        const val TYPE_SINGLE = 1 // 异构：单列大图/广告/活动
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, feedItem: FeedItem)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int, feedItem: FeedItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.onItemLongClickListener = listener
    }

    // 普通卡片ViewHolder
    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.iv_cover)
        val description: TextView = view.findViewById(R.id.tv_description)
        val price: TextView = view.findViewById(R.id.tv_price)
    }

    // 单列横幅卡片ViewHolder
    class SingleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.iv_single_cover)
        val title: TextView = view.findViewById(R.id.tv_single_title)
    }

    // 根据数据类型返回不同的视图类型
    override fun getItemViewType(position: Int): Int {
        return when (feedItems[position].type) {
            1 -> TYPE_SINGLE // 异构：单列大图/广告/活动
            else -> TYPE_NORMAL // 普通双列商品
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SINGLE -> {
                // 创建单列横幅卡片
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_feed_single, parent, false)
                SingleViewHolder(view)
            }
            else -> {
                // 创建普通双列卡片
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_item_product, parent, false)
                ProductViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = feedItems[position]

        // 核心代码：如果是异构卡片，强制设为通栏 (FullSpan)
        if (holder is SingleViewHolder) {
            // 设置为通栏显示
            val layoutParams = holder.itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams
            layoutParams?.isFullSpan = true

            // 绑定数据
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .placeholder(R.color.gray_light)
                .into(holder.cover)
            holder.title.text = item.title

            holder.itemView.setOnClickListener {
                // 跳转到促销专区页面
                val context = holder.itemView.context
                val intent = Intent(context, PromotionZoneActivity::class.java)
                context.startActivity(intent)
            }

            holder.itemView.setOnLongClickListener {
                onItemLongClickListener?.onItemLongClick(position, item)
                true
            }
        } else if (holder is ProductViewHolder) {
            // 1. 获取屏幕宽度的一半 (或者列宽)
            val screenWidth = holder.itemView.context.resources.displayMetrics.widthPixels
            val itemWidth = (screenWidth - 20) / 2 // 假设间距大概是 20px，你需要根据实际 padding 调整

            // 2. 【核心】根据宽高比，算出图片应有的高度
            // 公式：目标高度 = (图片原高 / 图片原宽) * 卡片实际宽
            // 注意：由于我们的图片是drawable资源，无法预先知道真实尺寸，这里使用固定比例
            val targetHeight = (itemWidth * 0.75).toInt() // 假设图片比例为4:3

            // 3. 【核心】在加载图片前，先强制把 ImageView 的高度拉伸到位！
            // 这样 Glide 加载慢也不会导致布局跳动，因为坑位已经占好了
            val layoutParams = holder.cover.layoutParams
            layoutParams.width = itemWidth
            layoutParams.height = targetHeight
            holder.cover.layoutParams = layoutParams

            // 4. 然后再加载图片
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .override(itemWidth, targetHeight) // 精准加载，省内存
                .placeholder(R.color.gray_light) // 占位图
                .into(holder.cover)

            holder.description.text = item.description

            if (item.price != null) {
                holder.price.visibility = View.VISIBLE
                holder.price.text = "¥${item.price}"
            } else {
                holder.price.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(position, item)
            }

            holder.itemView.setOnLongClickListener {
                onItemLongClickListener?.onItemLongClick(position, item)
                true // 消费长按事件
            }
        }
    }

    override fun getItemCount(): Int = feedItems.size}