package com.team.bytedancewaterfall.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.team.bytedancewaterfall.R;
import com.team.bytedancewaterfall.activity.DetailActivity;
import com.team.bytedancewaterfall.data.pojo.entity.Cart;
import com.team.bytedancewaterfall.data.pojo.entity.User;
import com.team.bytedancewaterfall.data.pojo.vo.CartAndFeed;
import com.team.bytedancewaterfall.data.service.impl.CartServiceImpl;
import com.team.bytedancewaterfall.data.service.impl.UserServiceImpl;
import com.team.bytedancewaterfall.utils.TimeUtil;
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

        // 初始化并设置 TextWatcher
        holder.quantityTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // 留空由失去焦点处理
            }
        };
        holder.quantityTextView.addTextChangedListener(holder.quantityTextWatcher);
    }
    private void validateAndUpdateQuantity(CartViewHolder holder, CartAndFeed item, int position) {
        try {
            String input = holder.quantityTextView.getText().toString();
            if (!input.isEmpty()) {
                int newCount = Integer.parseInt(input);
                if (newCount > 0) {
                    // 更新购物车项
                    Cart cart = new Cart();
                    cart.setId(item.getId());
                    cart.setFeedItemId(item.getProductId());
                    cart.setCount(newCount);
                    cart.setUpdateTime(TimeUtil.getCurTime());
                    cart.setUserId(UserServiceImpl.getInstance().getCurrentUser(context).getId());
                    if (CartServiceImpl.getInstance().updateCartItem(context, cart)) {
                        item.setCount(newCount);
                        // 注意：避免触发 TextWatcher
                        holder.quantityTextView.removeTextChangedListener(holder.quantityTextWatcher);
                        holder.quantityTextView.setText(String.valueOf(newCount));
                        holder.quantityTextView.addTextChangedListener(holder.quantityTextWatcher);

                        if (onSelectChangeListener != null) {
                            onSelectChangeListener.onSelectChanged();
                        }
                    }
                } else {
                    // 恢复原来的值
                    holder.quantityTextView.setText(String.valueOf(item.getCount()));
                    ToastUtils.showShortToast(context, "数量不能小于1");
                }
            }
        } catch (NumberFormatException e) {
            // 恢复原来的值
            holder.quantityTextView.setText(String.valueOf(item.getCount()));
            ToastUtils.showShortToast(context, "请输入有效的数字");
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
            // 直接输入 数量
          holder.quantityTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) { // 失去焦点时验证并保存
                        validateAndUpdateQuantity(holder, item, position);
                    }
                }
          });
        // 增加数量按钮
        holder.increaseButton.setOnClickListener(v -> {
            int currentCount = item.getCount();
            Cart cart = new Cart();
            cart.setId(item.getId());
            cart.setFeedItemId(item.getProductId());
            cart.setCount(currentCount + 1);
            cart.setUpdateTime(TimeUtil.getCurTime());
            cart.setUserId(UserServiceImpl.getInstance().getCurrentUser(context).getId());
            if (CartServiceImpl.getInstance().updateCartItem(context, cart)) {
                item.setCount(currentCount + 1);
                holder.quantityTextView.setText(String.valueOf(item.getCount()));
                if (onSelectChangeListener != null) {
                    onSelectChangeListener.onSelectChanged();
                }
            }
        });

        // 店铺更多按钮
        holder.shopMoreButton.setOnClickListener(v -> {
            // 创建选项弹窗
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("商品操作")
                    .setItems(new CharSequence[]{"删除商品"}, (dialog, which) -> {
                        switch (which) {
                            case 0: // 删除商品选项
                                showDeleteConfirmDialog(item, position);
                                break;
                        }
                    })
                    .show();
        });

        // 商品点击事件
        holder.itemView.setOnClickListener(v -> {
            // 跳转到商品详情页
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_FEED_ITEM, item);
            context.startActivity(intent);
        });
        // 减少购物车商品数量
        holder.decreaseButton.setOnClickListener(v -> {
            if (item.getCount() > 1) {
                item.setCount(item.getCount() - 1);
                holder.quantityTextView.setText(String.valueOf(item.getCount()));
                Cart cart = new Cart();
                cart.setId(item.getId());
                cart.setFeedItemId(item.getProductId());
                cart.setCount(item.getCount());
                cart.setUpdateTime(TimeUtil.getCurTime());
                cart.setUserId(UserServiceImpl.getInstance().getCurrentUser(context).getId());
                // 更新数据库
                CartServiceImpl.getInstance().updateCartItem(context, cart);
                if (onSelectChangeListener != null) {
                    onSelectChangeListener.onSelectChanged();
                }
                notifyDataSetChanged();
            }else {
                // 数量为1时，删除
                // 确认删除提示
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("确认删除")
                        .setMessage("确定要删除该商品吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CartServiceImpl.getInstance().deleteCartItem(context, item.getId());
                                cartList.remove(item);
                                notifyDataSetChanged();
                                ToastUtils.showShortToast(context, "已删除该商品");
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
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
    private void showDeleteConfirmDialog(CartAndFeed item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("确认删除")
                .setMessage("确定要删除该商品吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 执行删除操作
                    CartServiceImpl.getInstance().deleteCartItem(context, item.getId());
                    cartList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, cartList.size() - position);
                    if (onSelectChangeListener != null) {
                        onSelectChangeListener.onSelectChanged();
                    }
                    ToastUtils.showShortToast(context, "已在购物车中移除该商品");
                })
                .setNegativeButton("取消", null)
                .show();
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
        EditText quantityTextView;
        Button increaseButton;
        TextView shopNameTextView;
        ImageView shopMoreButton;

        // 添加 TextWatcher 成员变量
        TextWatcher quantityTextWatcher;
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
