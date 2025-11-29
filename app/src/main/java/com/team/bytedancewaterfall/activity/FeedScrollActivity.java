package com.team.bytedancewaterfall.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.team.bytedancewaterfall.R;
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;
import com.team.bytedancewaterfall.adapter.FeedScrollAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

/**
 * FeedScrollActivity
 * 用于单栏滑动展示FeedItem的数据
 */
public class FeedScrollActivity extends AppCompatActivity {

    public static final String EXTRA_FEED_ITEMS = "extra_feed_items";
    public static final String EXTRA_START_INDEX = "extra_start_index";

    private RecyclerView recyclerView;
    private FeedScrollAdapter feedAdapter;
    private List<FeedItem> feedItems = new ArrayList<>();
    private int startIndex = 0;
    private boolean isScrolling = false; // 标记是否正在滚动
    private Handler mainHandler = new Handler(Looper.getMainLooper()); // 主线程Handler，用于延迟操作
    private List<FeedItem> originalFeedItems = new ArrayList<>(); // 保存原始数据的副本
    private int currentDataIndex = 0; // 当前数据索引，用于循环加载
    private int batchSize = 10; // 每次加载的数据量
    private boolean isLoading = false; // 标记是否正在加载数据
    private static final int VISIBLE_THRESHOLD = 5; // 预加载阈值

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
    @SuppressWarnings("unchecked")
    private void getIntentData() {
        // 接收List<FeedItem>和count参数
        Serializable serializable = getIntent().getSerializableExtra(EXTRA_FEED_ITEMS);
        if (serializable instanceof List<?>) {
            List<?> list = (List<?>) serializable;
            if (!list.isEmpty() && list.get(0) instanceof FeedItem) {
                List<FeedItem> allFeedItems = (List<FeedItem>) list;
                originalFeedItems.addAll(allFeedItems); // 保存原始数据
                
                // 获取起始索引
                startIndex = getIntent().getIntExtra(EXTRA_START_INDEX, 0);
                currentDataIndex = startIndex;
                
                // 保留完整数据列表，不再截取子列表
                feedItems = new ArrayList<>(allFeedItems);
            }
        }
    }

    /**
     * 配置RecyclerView及其相关组件：LayoutManager、Adapter及点击监听等
     */
    private void setupRecyclerView() {
        // 设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 创建适配器
        feedAdapter = new FeedScrollAdapter(feedItems);
        recyclerView.setAdapter(feedAdapter);
        
        // 添加滚动监听，用于优化视频加载和实现循环加载
        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                
                isScrolling = newState != RecyclerView.SCROLL_STATE_IDLE;
                
                if (isScrolling) {
                    // 滚动时暂停加载视频
                    feedAdapter.pauseVideoLoading();
                } else {
                    // 停止滚动后，加载当前可见区域的视频
                    feedAdapter.resumeVideoLoading(recyclerView);
                }
            }
            
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                // 只有向下滚动且不在加载中时才检查是否需要加载更多
                if (dy > 0 && !isLoading) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int totalItemCount = layoutManager.getItemCount();
                        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                        
                        // 如果最后可见项接近列表末尾，并且原始数据不为空，则加载更多数据
                        if (totalItemCount > 0 && lastVisibleItemPosition >= totalItemCount - VISIBLE_THRESHOLD && !originalFeedItems.isEmpty()) {
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

            @Override
            public void onAddToCartClick(int position, FeedItem feedItem) {
                // 预留加入购物车方法调用
                handleAddToCart(feedItem);
            }

            @Override
            public void onBuyNowClick(int position, FeedItem feedItem) {
                // 预留立即购买方法调用
                handleBuyNow(feedItem);
            }
        });

        // 滚动到指定位置，确保点击的feeditem直接显示在屏幕顶部
        if (startIndex >= 0 && startIndex < feedItems.size()) {
            // 使用scrollToPosition直接定位到起始索引位置
            recyclerView.scrollToPosition(startIndex);
            
            // 确保RecyclerView布局完成后，将指定项滚动到视图顶部
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null && startIndex < feedItems.size()) {
                        layoutManager.scrollToPositionWithOffset(startIndex, 0);
                    }
                }
            });
        }
    }

    /**
     * 处理加入购物车操作
     *
     * @param feedItem 当前被操作的商品项
     */
    private void handleAddToCart(FeedItem feedItem) {
        // 预留调用插入数据的方法
        // TODO: 实现加入购物车逻辑
    }

    /**
     * 处理立即购买操作
     *
     * @param feedItem 当前被操作的商品项
     */
    private void handleBuyNow(FeedItem feedItem) {
        // 预留调用插入数据的方法
        // TODO: 实现立即购买逻辑
    }
    
    /**
     * 加载更多数据，实现循环加载功能
     */
    private void loadMoreData() {
        if (isLoading || originalFeedItems.isEmpty()) {
            return;
        }
        
        isLoading = true;
        
        // 使用Handler模拟网络请求延迟
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<FeedItem> newItems = new ArrayList<>();
                
                // 从currentDataIndex开始，循环添加数据
                for (int i = 0; i < batchSize && !originalFeedItems.isEmpty(); i++) {
                    if (currentDataIndex >= originalFeedItems.size()) {
                        currentDataIndex = 0; // 循环从头开始
                    }
                    newItems.add(originalFeedItems.get(currentDataIndex));
                    currentDataIndex++;
                }
                
                // 添加新数据到列表
                int startPosition = feedItems.size();
                feedItems.addAll(newItems);
                
                // 通知适配器数据变化
                feedAdapter.notifyItemRangeInserted(startPosition, newItems.size());
                
                isLoading = false;
            }
        }, 500); // 模拟500ms的加载延迟
    }
}