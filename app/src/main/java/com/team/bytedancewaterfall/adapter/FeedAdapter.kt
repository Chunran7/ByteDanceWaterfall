package com.team.bytedancewaterfall.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.team.bytedancewaterfall.R
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem

class FeedAdapter(private val items: List<FeedItem>) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.price.text = item.price
        // TODO: 使用图像加载库 (例如 Glide 或 Coil) 加载图片
        // holder.imageView.load(item.imageUrl)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val title: TextView = itemView.findViewById(R.id.titleTextView)
        val price: TextView = itemView.findViewById(R.id.priceTextView)
    }
}