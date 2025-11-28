package com.team.bytedancewaterfall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.team.bytedancewaterfall.R;
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;

import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.LoadControl;
import androidx.media3.exoplayer.PlayerMessage;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelection;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.ui.PlayerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    /**
     * Activity创建时调用，初始化界面、获取传入数据并设置RecyclerView
     *
     * @param savedInstanceState 保存的实例状态
     */
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
                feedItems = (List<FeedItem>) list;
            }
        }
        startIndex = getIntent().getIntExtra(EXTRA_START_INDEX, 0);
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
        
        // 添加滚动监听，用于优化视频加载
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

        // 滚动到指定位置
        if (startIndex >= 0 && startIndex < feedItems.size()) {
            recyclerView.scrollToPosition(startIndex);
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
     * FeedScrollAdapter
     * 用于单栏滑动展示FeedItem的适配器
     */
    public static class FeedScrollAdapter extends RecyclerView.Adapter<FeedScrollAdapter.FeedScrollViewHolder> {
        // 用于存储播放器实例，避免重复创建
        private Map<Integer, ExoPlayer> players = new HashMap<>();
        
        // 存储正在加载或已加载的视频位置
        private Set<Integer> visibleVideoPositions = new HashSet<>();
        
        // 延迟加载任务映射
        private Map<Integer, Runnable> delayedLoadTasks = new HashMap<>();
        
        // 视频播放配置常量
        private static final int MIN_BUFFER_DURATION = 15000; // 最小缓冲区持续时间（毫秒）
        private static final int MAX_BUFFER_DURATION = 30000; // 最大缓冲区持续时间（毫秒）
        private static final int BUFFER_FOR_PLAYBACK_DURATION = 2500; // 播放前缓冲区持续时间（毫秒）
        private static final int BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_DURATION = 5000; // 重新缓冲后播放前缓冲区持续时间（毫秒）
        
        // 延迟加载时间（毫秒）
        private static final int DELAYED_LOAD_TIME = 300;
        
        // 是否允许加载视频
        private boolean allowVideoLoading = true;
        
        // 最大同时播放的视频数量
        private static final int MAX_CONCURRENT_PLAYERS = 1;
        
        // 当前正在播放的视频位置
        private Integer currentPlayingPosition = null;
        
        // 应用是否处于前台
        private boolean isAppInForeground = true;

        private List<FeedItem> feedItems;
        private OnItemClickListener onItemClickListener;

        /**
         * 定义外部点击事件回调接口
         */
        public interface OnItemClickListener {
            /**
             * 条目整体点击事件
             *
             * @param position 点击的位置
             * @param feedItem 对应的数据对象
             */
            void onItemClick(int position, FeedItem feedItem);

            /**
             * “加入购物车”按钮点击事件
             *
             * @param position 点击的位置
             * @param feedItem 对应的数据对象
             */
            void onAddToCartClick(int position, FeedItem feedItem);

            /**
             * “立即购买”按钮点击事件
             *
             * @param position 点击的位置
             * @param feedItem 对应的数据对象
             */
            void onBuyNowClick(int position, FeedItem feedItem);
        }

        /**
         * 设置点击监听器
         *
         * @param listener 监听器实现类
         */
        public void setOnItemClickListener(OnItemClickListener listener) {
            this.onItemClickListener = listener;
        }

        /**
         * 构造函数，初始化FeedItem列表
         *
         * @param feedItems 数据源列表
         */
        public FeedScrollAdapter(List<FeedItem> feedItems) {
            this.feedItems = feedItems != null ? feedItems : new ArrayList<>();
        }

        /**
         * ViewHolder类，持有每个条目的视图元素
         */
        public static class FeedScrollViewHolder extends RecyclerView.ViewHolder {
            FrameLayout container;
            ImageView imageView;
            TextView titleView;
            TextView priceView;
            Button addToCartButton;
            Button buyNowButton;
            PlayerView playerView;
            ExoPlayer player;

            /**
             * 构造函数，绑定各个视图组件
             *
             * @param view 条目根视图
             */
            public FeedScrollViewHolder(View view) {
                super(view);
                container = view.findViewById(R.id.item_container);
                imageView = view.findViewById(R.id.item_image);
                titleView = view.findViewById(R.id.item_title);
                priceView = view.findViewById(R.id.item_price);
                addToCartButton = view.findViewById(R.id.btn_add_to_cart);
                buyNowButton = view.findViewById(R.id.btn_buy_now);
                
                // 查找或创建PlayerView
                playerView = view.findViewById(R.id.item_player_view);
            }
            
            /**
             * 释放播放器资源
             */
            public void releasePlayer() {
                if (player != null) {
                    player.release();
                    player = null;
                }
            }
        }

        /**
         * 创建新的ViewHolder实例
         *
         * @param parent   父容器
         * @param viewType 视图类型（本例未使用）
         * @return 新创建的ViewHolder
         */
        @Override
        public FeedScrollViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_scroll_item, parent, false);
            return new FeedScrollViewHolder(view);
        }

        /**
         * 绑定数据到ViewHolder，并设置各组件的交互行为
         *
         * @param holder   ViewHolder实例
         * @param position 当前绑定的数据在列表中的位置
         */
        @Override
        public void onBindViewHolder(FeedScrollViewHolder holder, final int position) {
            final FeedItem item = feedItems.get(position);
            
            // 重置视图状态
            holder.imageView.setVisibility(View.VISIBLE);
            if (holder.playerView != null) {
                holder.playerView.setVisibility(View.GONE);
            }
            
            // 释放之前的播放器资源
            holder.releasePlayer();
            
            if (item.getType() == 2 && item.getVideoUrl() != null) {
                // 视频类型，设置占位符，等待延迟加载
                holder.imageView.setVisibility(View.VISIBLE); // 保持图片可见，作为视频加载前的占位符
                if (holder.playerView != null) {
                    holder.playerView.setVisibility(View.GONE);
                }
                
                // 释放之前的播放器资源
                holder.releasePlayer();
                
                // 移除该位置的播放器引用
                players.remove(position);
                
                // 只有当允许加载且不在快速滑动时，才安排延迟加载
                if (allowVideoLoading) {
                    scheduleDelayedVideoLoad(holder, position, item);
                }
            } else if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                // 图片类型，加载图片
                Glide.with(holder.itemView.getContext())
                        .load(item.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.imageView);
            }else {
                // 设置默认图片
                holder.imageView.setImageResource(R.drawable.ic_launcher_background);
            }

            // 设置标题
            holder.titleView.setText(item.getTitle() != null ? item.getTitle() : "无标题");

            // 设置价格
            if (item.getPrice() != null && !item.getPrice().isEmpty()) {
                holder.priceView.setVisibility(View.VISIBLE);
                holder.priceView.setText("¥" + item.getPrice());
            } else {
                holder.priceView.setVisibility(View.GONE);
            }

            // 设置点击事件
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, item);
                    }
                }
            });

            holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onAddToCartClick(position, item);
                    }
                }
            });

            holder.buyNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onBuyNowClick(position, item);
                    }
                }
            });
        }

        /**
         * 返回数据集合大小
         *
         * @return 列表项数量
         */
        @Override
        public int getItemCount() {
            return feedItems.size();
        }
        
        /**
         * 当视图被回收时调用，释放资源
         */
        @Override
        public void onViewRecycled(FeedScrollViewHolder holder) {
            super.onViewRecycled(holder);
            
            // 获取ViewHolder的位置
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                // 从可见视频位置集合中移除
                visibleVideoPositions.remove(position);
                
                // 移除该位置的延迟加载任务
                if (delayedLoadTasks.containsKey(position)) {
                    new Handler(Looper.getMainLooper()).removeCallbacks(delayedLoadTasks.get(position));
                    delayedLoadTasks.remove(position);
                }
                
                // 如果是当前播放位置，清除标记
                if (currentPlayingPosition != null && currentPlayingPosition == position) {
                    currentPlayingPosition = null;
                }
            }
            
            // 释放播放器资源
            holder.releasePlayer();
            
            // 从播放器映射中移除
            if (position != RecyclerView.NO_POSITION) {
                players.remove(position);
            }
        }
        
        /**
         * 暂停视频加载
         */
        public void pauseVideoLoading() {
            allowVideoLoading = false;
            
            // 取消所有延迟加载任务
            for (Runnable task : delayedLoadTasks.values()) {
                new Handler(Looper.getMainLooper()).removeCallbacks(task);
            }
            delayedLoadTasks.clear();
            
            // 暂停所有不在可见区域的视频
            for (Map.Entry<Integer, ExoPlayer> entry : players.entrySet()) {
                int position = entry.getKey();
                ExoPlayer player = entry.getValue();
                if (player != null && !visibleVideoPositions.contains(position)) {
                    player.pause();
                }
            }
        }
        
        /**
         * 暂停所有视频播放
         */
        public void pauseAllVideos() {
            isAppInForeground = false;
            for (ExoPlayer player : players.values()) {
                if (player != null) {
                    player.pause();
                }
            }
        }
        
        /**
         * 恢复所有视频播放
         */
        public void resumeAllVideos() {
            isAppInForeground = true;
            // 只恢复可见区域和当前正在播放的视频
            for (Map.Entry<Integer, ExoPlayer> entry : players.entrySet()) {
                int position = entry.getKey();
                ExoPlayer player = entry.getValue();
                if (player != null && (visibleVideoPositions.contains(position) || 
                        (currentPlayingPosition != null && currentPlayingPosition == position))) {
                    player.play();
                }
            }
        }
        
        /**
         * 恢复视频加载
         */
        public void resumeVideoLoading(RecyclerView recyclerView) {
            allowVideoLoading = true;
            
            // 获取当前可见区域的位置
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null) {
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                
                // 计算当前可见区域的中间位置（最可能被用户关注的视频）
                int middlePosition = (firstVisiblePosition + lastVisiblePosition) / 2;
                
                // 更新可见视频位置集合
                visibleVideoPositions.clear();
                for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                    visibleVideoPositions.add(i);
                }
                
                // 管理播放器实例数量，限制同时播放的视频
                managePlayerInstances(middlePosition);
                
                // 恢复可见区域视频的播放，暂停不可见区域的视频
                for (Map.Entry<Integer, ExoPlayer> entry : players.entrySet()) {
                    int position = entry.getKey();
                    ExoPlayer player = entry.getValue();
                    if (player != null) {
                        if (visibleVideoPositions.contains(position)) {
                            player.play();
                        } else {
                            player.pause();
                        }
                    }
                }
                
                // 重新加载可见区域的视频，优先加载中间位置
                // 1. 先加载中间位置（最可能被观看的）
                FeedScrollViewHolder middleHolder = (FeedScrollViewHolder) recyclerView.findViewHolderForAdapterPosition(middlePosition);
                if (middleHolder != null && middlePosition < getItemCount()) {
                    final FeedItem middleItem = feedItems.get(middlePosition);
                    if (middleItem.getType() == 2 && middleItem.getVideoUrl() != null && middleHolder.player == null) {
                        scheduleDelayedVideoLoad(middleHolder, middlePosition, middleItem);
                    }
                }
                
                // 2. 再加载其他可见位置
                for (int position = firstVisiblePosition; position <= lastVisiblePosition; position++) {
                    if (position != middlePosition) { // 跳过已经处理的中间位置
                        FeedScrollViewHolder holder = (FeedScrollViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                        if (holder != null && position < getItemCount()) {
                            final FeedItem item = feedItems.get(position);
                            if (item.getType() == 2 && item.getVideoUrl() != null && holder.player == null) {
                                // 延迟加载视频，避免快速滚动时频繁创建播放器
                                scheduleDelayedVideoLoad(holder, position, item);
                            }
                        }
                    }
                }
            }
        }
        
        /**
         * 管理播放器实例数量，确保不超过最大限制
         * @param priorityPosition 优先级最高的位置（通常是可见区域的中间位置）
         */
        private void managePlayerInstances(int priorityPosition) {
            // 如果播放器数量超过限制，释放优先级较低的播放器
            while (players.size() > MAX_CONCURRENT_PLAYERS) {
                // 找出需要释放的播放器位置
                Integer positionToRelease = null;
                for (Integer position : players.keySet()) {
                    // 优先保留可见区域内的播放器，特别是优先位置
                    if (position != priorityPosition && !visibleVideoPositions.contains(position)) {
                        positionToRelease = position;
                        break;
                    }
                }
                
                // 如果找不到非可见区域的播放器，释放非优先位置的播放器
                if (positionToRelease == null && players.size() > 0) {
                    for (Integer position : players.keySet()) {
                        if (position != priorityPosition) {
                            positionToRelease = position;
                            break;
                        }
                    }
                }
                
                // 释放选中的播放器
                if (positionToRelease != null) {
                    ExoPlayer player = players.get(positionToRelease);
                    if (player != null) {
                        player.release();
                    }
                    players.remove(positionToRelease);
                    
                    // 如果释放的是当前播放位置，清除标记
                    if (currentPlayingPosition != null && currentPlayingPosition == positionToRelease) {
                        currentPlayingPosition = null;
                    }
                }
            }
            
            // 更新当前播放位置为优先级最高的位置
            currentPlayingPosition = priorityPosition;
        }
        
        /**
         * 安排延迟加载视频
         */
        private void scheduleDelayedVideoLoad(final FeedScrollViewHolder holder, final int position, final FeedItem item) {
            // 取消该位置之前的延迟任务
            if (delayedLoadTasks.containsKey(position)) {
                new Handler(Looper.getMainLooper()).removeCallbacks(delayedLoadTasks.get(position));
            }
            
            // 创建新的延迟加载任务
            Runnable loadTask = new Runnable() {
                @Override
                public void run() {
                    delayedLoadTasks.remove(position);
                    if (allowVideoLoading && holder.getAdapterPosition() == position) {
                        loadVideo(holder, position, item);
                    }
                }
            };
            
            delayedLoadTasks.put(position, loadTask);
            new Handler(Looper.getMainLooper()).postDelayed(loadTask, DELAYED_LOAD_TIME);
        }
        
        /**
         * 加载视频
         */
        private void loadVideo(FeedScrollViewHolder holder, int position, FeedItem item) {
            // 检查是否应该加载视频（应用在前台且播放器数量未超限）
            if (!isAppInForeground) {
                return;
            }
            
            // 检查播放器数量限制，如果已达到限制且当前位置不是优先位置，则不加载
            if (players.size() >= MAX_CONCURRENT_PLAYERS && 
                (currentPlayingPosition == null || position != currentPlayingPosition)) {
                // 如果当前位置不是优先位置，且播放器数量已达上限，则不加载
                return;
            }
            
            if (holder.playerView != null) {
                holder.imageView.setVisibility(View.GONE);
                holder.playerView.setVisibility(View.VISIBLE);
                
                // 配置自适应轨道选择器
                AdaptiveTrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
                TrackSelector trackSelector = new DefaultTrackSelector(holder.itemView.getContext(), adaptiveTrackSelectionFactory);
                
                // 配置加载控制策略
                LoadControl loadControl = new DefaultLoadControl.Builder()
                        .setBufferDurationsMs(
                                MIN_BUFFER_DURATION,  // 最小缓冲区持续时间
                                MAX_BUFFER_DURATION,  // 最大缓冲区持续时间
                                BUFFER_FOR_PLAYBACK_DURATION,  // 播放前缓冲区持续时间
                                BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_DURATION)  // 重新缓冲后播放前缓冲区持续时间
                        .setPrioritizeTimeOverSizeThresholds(true) // 优先考虑时间阈值而非大小阈值
                        .build();
                
                // 优化渲染器配置
                DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(holder.itemView.getContext())
                        .setEnableDecoderFallback(true) // 启用解码器回退
                        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON); // 使用扩展渲染器
                
                // 创建ExoPlayer实例，应用优化配置
                holder.player = new ExoPlayer.Builder(holder.itemView.getContext())
                        .setTrackSelector(trackSelector)
                        .setLoadControl(loadControl)
                        .setRenderersFactory(renderersFactory)
                        .setMediaSourceFactory(new DefaultMediaSourceFactory(holder.itemView.getContext()))
                        .build();
                
                holder.playerView.setPlayer(holder.player);
                holder.playerView.setUseController(false); // 禁用控制器，保持自动播放
                
                // 设置循环播放
                holder.player.setRepeatMode(Player.REPEAT_MODE_ONE); // 优化为单个视频循环
                holder.player.setPlayWhenReady(isAppInForeground); // 只有在应用前台时才自动播放
                
                // 添加播放状态监听，优化用户体验
                holder.player.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        // 可以在这里处理不同的播放状态
                        if (playbackState == Player.STATE_BUFFERING) {
                            // 缓冲中，可以显示加载指示器
                        } else if (playbackState == Player.STATE_READY) {
                            // 准备就绪
                        }
                    }
                    
                    @Override
                    public void onPlayerError(PlaybackException error) {
                        // 处理播放错误
                        // 可以尝试重新播放或降级播放质量
                    }
                });
                
                // 创建媒体项
                MediaItem mediaItem = new MediaItem.Builder()
                        .setUri(item.getVideoUrl())
                        .setLiveConfiguration(new MediaItem.LiveConfiguration.Builder()
                                .setMaxPlaybackSpeed(1.0f)
                                .build())
                        .build();
                
                holder.player.setMediaItem(mediaItem);
                
                // 准备并开始播放
                holder.player.prepare();
                
                // 保存播放器实例引用
                players.put(position, holder.player);
            }
        }
        
        /**
         * 清理所有播放器资源
         */
        public void releaseAllPlayers() {
            // 取消所有延迟加载任务
            for (Runnable task : delayedLoadTasks.values()) {
                new Handler(Looper.getMainLooper()).removeCallbacks(task);
            }
            delayedLoadTasks.clear();
            visibleVideoPositions.clear();
            
            // 释放所有播放器资源
            for (ExoPlayer player : players.values()) {
                if (player != null) {
                    player.release();
                }
            }
            players.clear();
        }
    }
}