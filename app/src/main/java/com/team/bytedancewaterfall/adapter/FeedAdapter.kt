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
        // 使用Glide加载图片，并通过CustomTarget获取图片原始尺寸
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    // 图片加载成功，计算宽高比
                    val aspectRatio = resource.intrinsicHeight.toFloat() / resource.intrinsicWidth.toFloat()

                    // 获取ImageView的布局参数
                    val layoutParams = imageView.layoutParams
                    // 根据ImageView的当前宽度（由StaggeredGridLayoutManager确定）和宽高比，计算出目标高度
                    layoutParams.height = (imageView.width * aspectRatio).toInt()
                    // 将新的高度应用到布局参数
                    imageView.layoutParams = layoutParams

                    // 将加载好的图片设置到ImageView中
                    imageView.setImageDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // 可选：在图片被清除时进行处理
                    imageView.setImageDrawable(placeholder)
                }
            })

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
