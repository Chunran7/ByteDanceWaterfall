package com.team.bytedancewaterfall.activity;



import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.team.bytedancewaterfall.R;
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;
import com.team.bytedancewaterfall.adapter.FeedScrollAdapter;
import com.team.bytedancewaterfall.data.pojo.entity.User;
import com.team.bytedancewaterfall.data.service.impl.CartServiceImpl;
import com.team.bytedancewaterfall.data.service.impl.FeedServiceImpl;
import com.team.bytedancewaterfall.data.service.impl.UserServiceImpl;
import com.team.bytedancewaterfall.utils.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;

import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

/**
 * FeedScrollActivity
 * 用于单栏滑动展示FeedItem的数据
 */
public class FeedScrollActivity extends AppCompatActivity {

    public static final String EXTRA_FEED_ITEM = "extra_feed_item";
    public static final String EXTRA_START_INDEX = "extra_start_index";

    private RecyclerView recyclerView;
    private FeedScrollAdapter feedAdapter;
    private final List<FeedItem> feedItems = new ArrayList<>();
    private int startIndex = 0;
    private boolean isScrolling = false; // 标记是否正在滚动
    private Handler mainHandler = new Handler(Looper.getMainLooper()); // 主线程Handler，用于延迟操作
    private final int batchSize = 10; // 每次加载的数据量
    private boolean isLoading = false; // 标记是否正在加载数据
    private static final int VISIBLE_THRESHOLD = 5; // 预加载阈值
    
    // 添加currentPage变量用于分页
    private int currentPage = 1; 

    private static FeedItem currentOperatingFeedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_scroll_activity);

        // 初始化视图
        initViews();

        // 获取传递的数据
        getIntentData();

        // 设置适配器
        setupRecyclerView();

        // 加载初始数据
        loadInitialData();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        // Activity可见时，恢复视频播放
        if (feedAdapter != null) {
            feedAdapter.resumeAllVideos();
        }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        // Activity不可见时，暂停所有视频播放
        if (feedAdapter != null) {
            feedAdapter.pauseAllVideos();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理所有播放器资源
        if (feedAdapter != null) {
            feedAdapter.releaseAllPlayers();
            feedAdapter = null;
        }
        
        // 清理Handler
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
            mainHandler = null;
        }
    }

    /**
     * 初始化页面中的控件引用
     */
    private void initViews() {
        recyclerView = findViewById(R.id.feed_scroll_recycler_view);
    }

    /**
     * 从Intent中提取传递过来的数据（FeedItem列表和起始索引）
     */
    private void getIntentData() {
        // 接收单个FeedItem参数
        Serializable serializable = getIntent().getSerializableExtra(EXTRA_FEED_ITEM);
        if (serializable instanceof FeedItem) {
            FeedItem feedItem = (FeedItem) serializable;
            
            // 将点击的项目添加到列表开头
            feedItems.add(feedItem);
            
            // 获取起始索引
            startIndex = getIntent().getIntExtra(EXTRA_START_INDEX, 0);
        }
    }
    
    /**
     * 加载初始数据
     */
    private void loadInitialData() {
        // 加载第一页数据
        loadMoreData();
    }

    /**
     * 配置RecyclerView及其相关组件：LayoutManager、Adapter及点击监听等
     */
    private void setupRecyclerView() {
        // 使用垂直LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // 使用PagerSnapHelper实现每页一个卡片（单列、卡片占满整个屏幕）
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        // 创建适配器
        feedAdapter = new FeedScrollAdapter(feedItems);
        recyclerView.setAdapter(feedAdapter);
        
        // 确保每个子项的高度等于RecyclerView的高度，从而卡片能占满整个屏幕
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                int height = recyclerView.getHeight();
                if (height > 0) {
                    ViewGroup.LayoutParams lp = view.getLayoutParams();
                    if (lp != null) {
                        lp.height = height;
                        view.setLayoutParams(lp);
                    }
                } else {
                    // 如果RecyclerView还没测量好，高度为0，则延后设置
                    recyclerView.post(() -> {
                        int h = recyclerView.getHeight();
                        if (h > 0) {
                            ViewGroup.LayoutParams lp = view.getLayoutParams();
                            if (lp != null) {
                                lp.height = h;
                                view.setLayoutParams(lp);
                            }
                        }
                    });
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                // no-op
            }
        });

        // 添加滚动监听，用于优化视频加载和实现循环加载
        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                
                isScrolling = newState != RecyclerView.SCROLL_STATE_IDLE;
                
                if (isScrolling) {
                    // 滚动时暂停加载视频并暂停所有播放
                    feedAdapter.pauseVideoLoading();
                    feedAdapter.pauseAllVideos();
                } else {
                    // 停止滚动后，加载当前可见页面的视频并恢复播放
                    feedAdapter.resumeVideoLoading(recyclerView);
                    feedAdapter.resumeAllVideos();
                }
            }
            
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                // 只有向下滚动且不在加载中时才检查是否需要加载更多
                if (dy > 0 && !isLoading) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int totalItemCount = layoutManager.getItemCount();
                        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                        
                        // 如果最后可见项接近列表末尾，则加载更多数据
                        if (totalItemCount > 0 && lastVisibleItemPosition >= totalItemCount - VISIBLE_THRESHOLD) {
                            loadMoreData();
                        }
                    }
                }
            }
        });

        // 设置点击监听器
        feedAdapter.setOnItemClickListener(new FeedScrollAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, FeedItem feedItem) {
                // 跳转到DetailActivity
                Intent intent = new Intent(FeedScrollActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_FEED_ITEM, feedItem);
                startActivity(intent);
            }
            private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            // 登录成功，重新获取当前用户并继续购物车操作
                            User user = UserServiceImpl.getInstance().getCurrentUser(FeedScrollActivity.this);
                            if (user != null) {
                                // 可以在这里执行添加到购物车的逻辑
                                user = UserServiceImpl.getInstance().getCurrentUser(FeedScrollActivity.this);
                                if (user == null) {
                                    ToastUtils.showShortToast(FeedScrollActivity.this, "请先登录");
                                    return;
                                }
                                // 预留加入购物车方法调用
                                CartServiceImpl.getInstance().addCart(FeedScrollActivity.this, currentOperatingFeedItem, user.getId());
                            }
                        }
                    }
            );
            @Override
            public void onAddToCartClick(int position, FeedItem feedItem) {
//                SPUtils.getInstance(FeedScrollActivity.this).remove(USER_TOKEN);
                // 获取当前用户
                User user = UserServiceImpl.getInstance().getCurrentUser(FeedScrollActivity.this);
                if (user == null) {
                    // 保存当前要操作的商品信息
                    currentOperatingFeedItem = feedItem;
                    // 跳转到登录页面等待结果
                    Intent intent = new Intent(FeedScrollActivity.this, LoginActivity.class);
                    loginLauncher.launch(intent);
                    return;
                }
                // 加入购物车方法调用
                CartServiceImpl.getInstance().addCart(FeedScrollActivity.this, feedItem, user.getId());
            }

            @Override
            public void onBuyNowClick(int position, FeedItem feedItem) {
                // TODO 预留立即购买方法调用
                ToastUtils.showShortToast(FeedScrollActivity.this, "购买方法开发中");
            }
        });
        // 滚动到指定位置，确保点击的feeditem直接显示在屏幕顶部（现在是整页显示）
        if (startIndex >= 0 && startIndex < feedItems.size()) {
            // 使用scrollToPosition直接定位到起始索引位置
            recyclerView.scrollToPosition(startIndex);
            
            // 确保RecyclerView布局完成后，将指定项滚动到视图顶部
            recyclerView.post(() -> {
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (lm != null && startIndex < feedItems.size()) {
                    lm.scrollToPositionWithOffset(startIndex, 0);
                }
            });
        }
    }
    /**
     * 加载更多数据，实现分页加载功能
     */
    private void loadMoreData() {
        if (isLoading) {
            return;
        }
        
        isLoading = true;
        
        // 使用Handler模拟网络请求延迟
        mainHandler.postDelayed(() -> {
            // 从服务中获取下一页数据
            List<FeedItem> newItems = FeedServiceImpl.getInstance().pageQueryFeedList(FeedScrollActivity.this, currentPage, batchSize);

            // 如果没有更多数据，则停止加载
            if (newItems.isEmpty()) {
                isLoading = false;
                return;
            }

            // 增加页码，以便下次加载下一页
            currentPage++;

            // 添加新数据到列表
            int startPosition = feedItems.size();
            feedItems.addAll(newItems);

            // 通知适配器数据变化
            feedAdapter.notifyItemRangeInserted(startPosition, newItems.size());

            isLoading = false;
        }, 500); // 模拟500ms的加载延迟
    }
}
