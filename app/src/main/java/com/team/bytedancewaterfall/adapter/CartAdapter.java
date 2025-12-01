package com.team.bytedancewaterfall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.team.bytedancewaterfall.R;
import com.team.bytedancewaterfall.data.pojo.vo.CartAndFeed;
import com.team.bytedancewaterfall.utils.MediaLoaderUtils;
import com.team.bytedancewaterfall.utils.ToastUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartAndFeed> cartList;
    private OnSelectChangeListener onSelectChangeListener;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public interface OnSelectChangeListener {
        void onSelectChanged();
    }

    public CartAdapter(Context context, List<CartAndFeed> cartList) {
        this.context = context;
        this.cartList = cartList != null ? cartList : new ArrayList<>();
    }

    public void setOnSelectChangeListener(OnSelectChangeListener listener) {
        this.onSelectChangeListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartAndFeed item = cartList.get(position);
        bindData(holder, item, position);
        setupListeners(holder, item, position);
    }

    private void bindData(CartViewHolder holder, CartAndFeed item, int position) {
        // 设置商品图片
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            // 如果图片URL是drawable资源
            if (item.getImageUrl().startsWith("drawable/")) {
                String resourceName = item.getImageUrl().substring(9);
                int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
                if (resourceId != 0) {
                    holder.productImageView.setImageResource(resourceId);
                } else {
                    holder.productImageView.setImageResource(R.drawable.test1_0);
                }
            } else {
                // 使用MediaLoaderUtils加载网络图片
                Glide.with(holder.itemView.getContext())
                        .load(item.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.productImageView);
//                MediaLoaderUtils.loadImage(context, item.getImageUrl(), holder.productImageView);
            }
        } else {
            holder.productImageView.setImageResource(R.drawable.test1_0);
        }

        // 设置商品信息
        holder.productNameTextView.setText(item.getTitle() != null ? item.getTitle() : "商品名称");
        holder.productDescTextView.setText(item.getDescription() != null ? item.getDescription() : "商品描述");
        holder.priceTextView.setText("¥" + (item.getPrice() != null ? item.getPrice() : "0.00"));
        
        // 设置原价（如果有）
        if (item.getOriginalPrice() != null && !item.getOriginalPrice().equals(item.getPrice())) {
            holder.originalPriceTextView.setText("¥" + item.getOriginalPrice());
            holder.originalPriceTextView.setVisibility(View.VISIBLE);
        } else {
            holder.originalPriceTextView.setVisibility(View.GONE);
        }

        // 设置店铺名称
        holder.shopNameTextView.setText(item.getShopName() != null ? item.getShopName() : "店铺名称");

        // 设置数量
        holder.quantityTextView.setText(String.valueOf(item.getCount()));

        // 设置选中状态
        holder.selectCheckbox.setImageResource(item.isSelected() ? 
                android.R.drawable.checkbox_on_background : 
                android.R.drawable.checkbox_off_background);

        // 设置优惠信息（如果有）
        if (item.getHasDiscount()) {
            // 这里可以添加优惠标签或信息
        }
    }

    private void setupListeners(final CartViewHolder holder, final CartAndFeed item, final int position) {
        // 选择框点击事件
        holder.selectCheckbox.setOnClickListener(v -> {
            item.setSelected(!item.isSelected());
            notifyItemChanged(position);
            if (onSelectChangeListener != null) {
                onSelectChangeListener.onSelectChanged();
            }
        });

        // 减少数量按钮
        holder.decreaseButton.setOnClickListener(v -> {
            int currentCount = item.getCount();
            if (currentCount > 1) {
                item.setCount(currentCount - 1);
                holder.quantityTextView.setText(String.valueOf(item.getCount()));
                if (onSelectChangeListener != null) {
                    onSelectChangeListener.onSelectChanged();
                }
            } else {
                // 数量为1时，提示用户是否删除
                ToastUtils.showShortToast(context, "是否删除该商品？");
                // 这里可以添加确认删除的对话框
            }
        });

        // 增加数量按钮
        holder.increaseButton.setOnClickListener(v -> {
            int currentCount = item.getCount();
            int stock = item.getStock() > 0 ? item.getStock() : Integer.MAX_VALUE;
            if (currentCount < stock) {
                item.setCount(currentCount + 1);
                holder.quantityTextView.setText(String.valueOf(item.getCount()));
                if (onSelectChangeListener != null) {
                    onSelectChangeListener.onSelectChanged();
                }
            } else {
                ToastUtils.showShortToast(context, "已达到最大库存");
            }
        });

        // 店铺更多按钮
        holder.shopMoreButton.setOnClickListener(v -> {
            ToastUtils.showShortToast(context, "店铺操作");
        });

        // 商品点击事件
        holder.itemView.setOnClickListener(v -> {
            // 跳转到商品详情页
            ToastUtils.showShortToast(context, "查看商品详情");
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    // 获取选中的商品
    public List<CartAndFeed> getSelectedItems() {
        List<CartAndFeed> selectedItems = new ArrayList<>();
        for (CartAndFeed item : cartList) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    // 全选/取消全选
    public void selectAll(boolean select) {
        for (CartAndFeed item : cartList) {
            item.setSelected(select);
        }
        notifyDataSetChanged();
        if (onSelectChangeListener != null) {
            onSelectChangeListener.onSelectChanged();
        }
    }

    // 检查是否全选
    public boolean isAllSelected() {
        if (cartList.isEmpty()) {
            return false;
        }
        for (CartAndFeed item : cartList) {
            if (!item.isSelected()) {
                return false;
            }
        }
        return true;
    }

    // 更新数据
    public void updateData(List<CartAndFeed> newData) {
        this.cartList = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    // 设置数据（与updateData功能相同，为了兼容不同调用方式）
    public void setData(List<CartAndFeed> newData) {
        updateData(newData);
    }

    // 删除商品
    public void removeItem(int position) {
        if (position >= 0 && position < cartList.size()) {
            cartList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartList.size() - position);
            if (onSelectChangeListener != null) {
                onSelectChangeListener.onSelectChanged();
            }
        }
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView selectCheckbox;
        ImageView productImageView;
        TextView productNameTextView;
        TextView productDescTextView;
        TextView priceTextView;
        TextView originalPriceTextView;
        Button decreaseButton;
        TextView quantityTextView;
        Button increaseButton;
        TextView shopNameTextView;
        ImageView shopMoreButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            selectCheckbox = itemView.findViewById(R.id.select_checkbox);
            productImageView = itemView.findViewById(R.id.product_image_view);
            productNameTextView = itemView.findViewById(R.id.product_name_text_view);
            productDescTextView = itemView.findViewById(R.id.product_desc_text_view);
            priceTextView = itemView.findViewById(R.id.price_text_view);
            originalPriceTextView = itemView.findViewById(R.id.original_price_text_view);
            decreaseButton = itemView.findViewById(R.id.decrease_button);
            quantityTextView = itemView.findViewById(R.id.quantity_text_view);
            increaseButton = itemView.findViewById(R.id.increase_button);
            shopNameTextView = itemView.findViewById(R.id.shop_name_text_view);
            shopMoreButton = itemView.findViewById(R.id.shop_more_button);
        }
    }
}
