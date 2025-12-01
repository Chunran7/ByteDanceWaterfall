package com.team.bytedancewaterfall.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.team.bytedancewaterfall.R
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem

/**
 * Feed流适配器类，用于在RecyclerView中展示瀑布流内容
 * @param feedItems 需要展示的FeedItem数据列表
 */
class FeedAdapter(private val feedItems: List<FeedItem>) :
    RecyclerView.Adapter<FeedAdapter.ProductViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int, feedItem: FeedItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.iv_cover)
        val description: TextView = view.findViewById(R.id.tv_description)
        val price: TextView = view.findViewById(R.id.tv_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = feedItems[position]

        val imageView = holder.cover
        
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
    }

    override fun getItemCount(): Int = feedItems.size
}