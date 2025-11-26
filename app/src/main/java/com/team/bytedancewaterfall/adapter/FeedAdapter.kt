package com.team.bytedancewaterfall.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.bytedancewaterfall.R
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem

/**
 * Feed流适配器类，用于在RecyclerView中展示瀑布流内容
 * @param feedItems 需要展示的FeedItem数据列表
 */
class FeedAdapter(private val feedItems: List<FeedItem>) :
    RecyclerView.Adapter<FeedAdapter.ProductViewHolder>() {

    // 点击事件监听器
    private var onItemClickListener: OnItemClickListener? = null

    /**
     * 点击事件监听接口
     */
    interface OnItemClickListener {
        fun onItemClick(position: Int, feedItem: FeedItem)
    }

    /**
     * 设置点击事件监听器
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    /**
     * 产品卡片类型的ViewHolder内部类
     * 它持有将被填充数据的视图组件引用
     */
    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 封面图片控件
        val cover: ImageView = view.findViewById(R.id.iv_cover)
        // 描述文本控件
        val description: TextView = view.findViewById(R.id.tv_description)
        // 价格文本控件
        val price: TextView = view.findViewById(R.id.tv_price)
    }

    /**
     * 创建ViewHolder实例的方法
     * 当RecyclerView需要新的ViewHolder时会被调用
     *
     * @param parent 父容器视图组
     * @param viewType 视图类型（当有多种不同类型item时使用）
     * @return 返回新创建的ProductViewHolder实例
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        // 使用LayoutInflater从XML布局文件中inflate视图
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_item_product, parent, false)
        return ProductViewHolder(view)
    }

    /**
     * 绑定数据到ViewHolder的方法
     * 当RecyclerView需要更新特定位置的item时会被调用
     *
     * @param holder 需要绑定数据的ViewHolder实例
     * @param position 当前item在数据列表中的位置
     */
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        // 获取当前位置的数据项
        val item = feedItems[position]

        // 将描述信息绑定到对应的TextView控件
        holder.description.text = item.description

        // 使用Glide库从网络URL加载图片并显示到ImageView控件中
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.cover)

        // 根据价格是否为空来决定是否显示价格控件
        if (item.price != null) {
            // 如果价格不为空，则显示价格控件并设置价格文本
            holder.price.visibility = View.VISIBLE
            holder.price.text = "¥${item.price}"
        } else {
            // 如果价格为空，则隐藏价格控件
            holder.price.visibility = View.GONE
        }

        // 设置点击事件
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position, item)
        }
    }

    /**
     * 获取数据列表大小的方法
     * RecyclerView通过此方法确定总共有多少个item需要显示
     *
     * @return 返回数据列表的大小
     */
    override fun getItemCount(): Int = feedItems.size

    // 注意事项: 当你有更多不同类型的卡片时，你需要重写getItemViewType()方法
    // 并创建更多的ViewHolder类。目前这样实现就足够了。
}