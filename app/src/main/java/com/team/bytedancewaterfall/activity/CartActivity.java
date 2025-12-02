package com.team.bytedancewaterfall.activity;

import android.content.Intent;
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
    private TextView emptyCartText;
    private View bottomBar;
    
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
            // 清空购物车列表，避免数据残留
            cartList.clear();
            
            if (user != null) {
                // 用户已登录，从数据库加载购物车数据
                List<CartAndFeed> tempList = CartServiceImpl.getInstance().getListByUserIdWithFeedItem(this, user.getId());
                if (tempList != null && !tempList.isEmpty()) {
                    cartList = tempList;
                }
                // 如果购物车为空，不使用模拟数据，将在updateEmptyCartStatus中处理
            }
            // 如果用户未登录，购物车列表保持为空
            
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
            // 清空购物车列表，显示空状态
            cartList.clear();
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
        emptyCartText = findViewById(R.id.empty_cart_text);
        bottomBar = findViewById(R.id.bottom_bar);
        
        // 按钮点击事件已在updateEmptyCartStatus方法中动态设置
        
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

    /**
     * 初始化数据
     */
    private void initData() {
        try {
            // 检查登录状态
            User user = UserServiceImpl.getInstance().getCurrentUser(this);
            if (user != null) {
                // 用户已登录，从数据库获取购物车数据
                List<CartAndFeed> tempList = CartServiceImpl.getInstance().getListByUserIdWithFeedItem(this, user.getId());
                // 确保列表不为null
                if (tempList != null) {
                    cartList = tempList;
                } else {
                    cartList = new ArrayList<>();
                }
            } else {
                // 用户未登录，清空购物车列表
                cartList = new ArrayList<>();
            }
            updateCartCount();
            updateEmptyCartStatus();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showShortToast(this, "加载购物车数据失败");
            // 发生异常时，显示空购物车
            cartList = new ArrayList<>();
            updateCartCount();
            updateEmptyCartStatus();
        }
    }

    // loadCartData方法已在onResume中重写实现，此处移除冗余方法

    // 模拟数据生成方法已移除，现在完全依赖数据库获取数据


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

    /**
     * 更新空购物车状态
     */
    private void updateEmptyCartStatus() {
        // 检查是否登录
        boolean isLoggedIn = UserServiceImpl.getInstance().getCurrentUser(this) != null;
        
        // 空状态判断：未登录或购物车为空
        boolean isEmptyState = !isLoggedIn || cartList.isEmpty();
        
        // 更新视图可见性
        recyclerView.setVisibility(isEmptyState ? View.GONE : View.VISIBLE);
        bottomBar.setVisibility(isEmptyState ? View.GONE : View.VISIBLE);
        emptyCartView.setVisibility(isEmptyState ? View.VISIBLE : View.GONE);
        
        // 根据登录状态更新空状态UI
        TextView emptyCartText = findViewById(R.id.empty_cart_text);
        Button goShoppingButton = findViewById(R.id.go_shopping_button);
        
        if (emptyCartText != null && goShoppingButton != null) {
            if (!isLoggedIn) {
                // 用户未登录状态
                emptyCartText.setText("请先登录，查看您的购物车");
                goShoppingButton.setText("去登录");
                // 设置登录按钮点击事件
                goShoppingButton.setOnClickListener(v -> {
                    // 跳转到登录页面
                    Intent intent = new Intent(CartActivity.this, LoginActivity.class);
                    startActivity(intent);
                });
            } else {
                // 用户已登录但购物车为空
                emptyCartText.setText("购物车还是空的，快去添加商品吧");
                goShoppingButton.setText("去逛逛");
                // 设置去逛逛按钮点击事件
                goShoppingButton.setOnClickListener(v -> {
                    // 跳转到首页
                    Intent intent = new Intent(CartActivity.this, MainActivity.class);
                    startActivity(intent);
                });
            }
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
