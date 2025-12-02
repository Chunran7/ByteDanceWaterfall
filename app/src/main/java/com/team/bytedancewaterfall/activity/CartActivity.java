package com.team.bytedancewaterfall.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.team.bytedancewaterfall.R;
import com.team.bytedancewaterfall.adapter.CartAdapter;
import com.team.bytedancewaterfall.data.pojo.vo.CartAndFeed;
import com.team.bytedancewaterfall.data.service.impl.CartServiceImpl;
import com.team.bytedancewaterfall.data.service.impl.UserServiceImpl;
import com.team.bytedancewaterfall.data.pojo.entity.User;
import com.team.bytedancewaterfall.utils.ToastUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BaseBottomNavActivity implements CartAdapter.OnSelectChangeListener {

    private RecyclerView recyclerView;
    private TextView totalPriceTextView;
    private Button checkoutButton;
    private TextView selectAllTextView;
    private View emptyCartView;
    private TextView cartCountTextView;
    
    private CartAdapter cartAdapter;
    private List<CartAndFeed> cartList;
    private boolean isAllSelected = false;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private TextView itemCountTextView;
    private TextView settleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // 初始化视图
        initView();
        
        // 初始化数据
        initData();
        
        // 设置适配器
        setupAdapter();
        
        // 设置监听器
        setupListeners();
        
        // 初始化底部导航栏
        initBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在Activity恢复时重新加载购物车数据，确保数据最新
        try {
            User user = UserServiceImpl.getInstance().getCurrentUser(this);
            if (user != null) {
                List<CartAndFeed> tempList = CartServiceImpl.getInstance().getListByUserIdWithFeedItem(this, user.getId());
                if (tempList != null && !tempList.isEmpty()) {
                    cartList = tempList;
                } else {
                    // 使用模拟数据作为备份
                    cartList = generateMockData();
                }
            } else {
                // 使用模拟数据作为备份
                cartList = generateMockData();
            }
            
            // 重新设置适配器数据
            if (cartAdapter != null) {
                cartAdapter.setData(cartList);
            }
            
            updateCartCount();
            updateEmptyCartStatus();
            updateTotalPrice();
            
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showShortToast(this, "加载购物车数据失败");
            // 使用模拟数据作为备份
            cartList = generateMockData();
            if (cartAdapter != null) {
                cartAdapter.setData(cartList);
            }
            updateCartCount();
            updateEmptyCartStatus();
            updateTotalPrice();
        }
    }

    private void initView() {
        recyclerView = findViewById(R.id.cart_recycler_view);
        totalPriceTextView = findViewById(R.id.total_price_text_view);
        checkoutButton = findViewById(R.id.settle_button);
        selectAllTextView = findViewById(R.id.select_all_text_view);
        emptyCartView = findViewById(R.id.empty_cart_view);
        
        // 添加新的视图引用
        itemCountTextView = findViewById(R.id.item_count_text_view);
        cartCountTextView = findViewById(R.id.cart_count_text_view);
        
        // 设置标题
        TextView titleTextView = findViewById(R.id.title_text_view);
        titleTextView.setText("购物车");
        
        // 设置返回按钮
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    private void initData() {
        cartList = new ArrayList<>();
        
        try {
            // 从数据库加载购物车数据
            User user = UserServiceImpl.getInstance().getCurrentUser(this);
            if (user != null) {
                List<CartAndFeed> tempList = CartServiceImpl.getInstance().getListByUserIdWithFeedItem(this, user.getId());
                if (tempList != null && !tempList.isEmpty()) {
                    cartList = tempList;
                }
            }
            
            if (cartList.isEmpty()) {
                // 使用模拟数据作为备份
                cartList = generateMockData();
            }
            
            updateCartCount();
            updateEmptyCartStatus();
            
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showShortToast(this, "加载购物车数据失败");
            // 使用模拟数据作为备份
            cartList = generateMockData();
            updateCartCount();
            updateEmptyCartStatus();
        }
    }

    // loadCartData方法已在onResume中重写实现，此处移除冗余方法

    private List<CartAndFeed> generateMockData() {
        List<CartAndFeed> mockList = new ArrayList<>();
        
        // 模拟数据
        CartAndFeed item1 = new CartAndFeed();
        item1.setProductId("1");
        item1.setProductName("模拟数据超级立减鸭鸭时尚连帽中长款羽绒服");
        item1.setProductDesc("燕麦色XL[135-150斤]");
        item1.setPrice(399.0);
        item1.setOriginalPrice(599.0);
        item1.setCount(1);
        item1.setImageUrl("drawable/test1_0");
        item1.setShopName("天猫 鸭鸭官方旗舰店");
        item1.setSelected(false);
        mockList.add(item1);

        CartAndFeed item2 = new CartAndFeed();
        item2.setProductId("2");
        item2.setProductName("鸭鸭羽绒服男连帽中长款外套");
        item2.setProductDesc("正黑色/正黑色5522");
        item2.setPrice(299.0);
        item2.setOriginalPrice(399.0);
        item2.setCount(1);
        item2.setImageUrl("drawable/test1_1");
        item2.setShopName("天猫 鸭鸭官方旗舰店");
        item2.setSelected(false);
        mockList.add(item2);

        CartAndFeed item3 = new CartAndFeed();
        item3.setProductId("3");
        item3.setProductName("伊利早餐奶麦香核桃味");
        item3.setProductDesc("11月产-麦香味250ml*24盒");
        item3.setPrice(62.4);
        item3.setOriginalPrice(67.4);
        item3.setCount(1);
        item3.setImageUrl("drawable/test2_1");
        item3.setShopName("天猫 伊利利航专卖店");
        item3.setSelected(false);
        mockList.add(item3);

        // 添加更多模拟数据
        CartAndFeed item4 = new CartAndFeed();
        item4.setProductId("4");
        item4.setProductName("安踏毒刺6代跑步鞋");
        item4.setProductDesc("正黑色/仓黑色5522");
        item4.setPrice(147.13);
        item4.setOriginalPrice(187.0);
        item4.setCount(1);
        item4.setImageUrl("drawable/test1_3");
        item4.setShopName("天猫 隽维运动专营店");
        item4.setSelected(false);
        mockList.add(item4);
        
        // 也更新成员变量cartList
        cartList.clear();
        cartList.addAll(mockList);
        
        return mockList;
    }

    private void setupAdapter() {
        cartAdapter = new CartAdapter(this, cartList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);
        
        // 设置商品选择状态变化的监听器
        cartAdapter.setOnSelectChangeListener(this);
    }

    private void setupListeners() {
        // 全选/取消全选
        selectAllTextView.setOnClickListener(v -> {
            isAllSelected = !isAllSelected;
            cartAdapter.selectAll(isAllSelected);
            updateTotalPrice();
        });

        // 结算按钮
        checkoutButton.setOnClickListener(v -> {
            // 获取选中的商品
            List<CartAndFeed> selectedItems = cartAdapter.getSelectedItems();
            if (selectedItems.isEmpty()) {
                // 提示用户请选择商品
                ToastUtils.showShortToast(this, "请选择要结算的商品");
            } else {
                // 显示选中的商品信息
                StringBuilder sb = new StringBuilder();
                sb.append("结算商品：\n");
                for (CartAndFeed item : selectedItems) {
                    sb.append(item.getProductName()).append(" x").append(item.getCount()).append("\n");
                }
                sb.append("总价：¥").append(totalPriceTextView.getText().toString().substring(3)); // 去掉"合计:"前缀
                
                ToastUtils.showShortToast(this, sb.toString());
                // 这里可以跳转到结算页面
            }
        });

        // 搜索按钮
        findViewById(R.id.search_button).setOnClickListener(v -> {
            ToastUtils.showShortToast(this, "搜索功能");
        });

        // 筛选按钮
        findViewById(R.id.filter_button).setOnClickListener(v -> {
            ToastUtils.showShortToast(this, "筛选功能");
        });
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        int selectedCount = 0;
        List<CartAndFeed> selectedItems = cartAdapter.getSelectedItems();
        for (CartAndFeed item : selectedItems) {
            // 直接计算总价：单价 * 数量
            Double price = item.getPriceDouble();
            if (price != null && item.getCount() != null) {
                totalPrice += price * item.getCount();
            }
            selectedCount += item.getCount();
        }
        
        String formattedPrice = decimalFormat.format(totalPrice);
        totalPriceTextView.setText("合计:¥" + formattedPrice);
        
        // 更新数量显示
        if (itemCountTextView != null) {
            itemCountTextView.setText("合计(" + selectedCount + ")：");
        }
        
        checkoutButton.setText("结算(" + selectedCount + ")");
    }

    private void checkSelectAll() {
        boolean allSelected = cartAdapter.isAllSelected();
        if (allSelected != isAllSelected) {
            isAllSelected = allSelected;
            // 更新全选按钮状态
        }
    }

    private void updateEmptyCartStatus() {
        if (cartList.isEmpty()) {
            emptyCartView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            findViewById(R.id.bottom_bar).setVisibility(View.GONE);
        } else {
            emptyCartView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
        }
    }

    private void updateCartCount() {
        int totalCount = 0;
        for (CartAndFeed item : cartList) {
            totalCount += item.getCount();
        }
        if (cartCountTextView != null) {
            cartCountTextView.setText("(" + totalCount + ")");
        }
    }

    @Override
    public void onSelectChanged() {
        // 当选择状态改变时更新价格
        updateTotalPrice();
        checkSelectAll();
    }
}
