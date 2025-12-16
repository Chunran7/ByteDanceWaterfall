package com.team.bytedancewaterfall.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team.bytedancewaterfall.R;
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;
import com.team.bytedancewaterfall.utils.MediaLoaderUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// 引入高斯模糊库 (确保 build.gradle 已添加依赖)
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * FeedScrollAdapter
 * 用于单栏滑动展示FeedItem的适配器 (支持高斯模糊背景)
 */
public class FeedScrollAdapter extends RecyclerView.Adapter<FeedScrollAdapter.FeedScrollViewHolder> {
    // 用于存储播放器实例，避免重复创建
    private Map<Integer, ExoPlayer> players = new HashMap<>();

    // 存储正在加载或已加载的视频位置
    private Set<Integer> visibleVideoPositions = new HashSet<>();

    // 延迟加载任务映射
    private Map<Integer, Runnable> delayedLoadTasks = new HashMap<>();

    // 最大同时播放的视频数量
    private static final int MAX_CONCURRENT_PLAYERS = 1;

    // 延迟加载时间（毫秒）
    private static final int DELAYED_LOAD_TIME = 300;

    // 是否允许加载视频
    private boolean allowVideoLoading = true;

    // 当前正在播放的视频位置
    private Integer currentPlayingPosition = null;

    // 应用是否处于前台
    private boolean isAppInForeground = true;

    private List<FeedItem> feedItems;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, FeedItem feedItem);
        void onAddToCartClick(int position, FeedItem feedItem);
        void onBuyNowClick(int position, FeedItem feedItem);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public FeedScrollAdapter(List<FeedItem> feedItems) {
        this.feedItems = feedItems != null ? feedItems : new ArrayList<>();
    }

    /**
     * ViewHolder类，持有每个条目的视图元素
     */
    public static class FeedScrollViewHolder extends RecyclerView.ViewHolder {
        View container;
        ImageView imageView; // 前景清晰图
        ImageView blurBg;    // 【新增】背景模糊图
        TextView titleView;
        TextView priceView;
        Button addToCartButton;
        Button buyNowButton;
        PlayerView playerView;
        ExoPlayer player;

        public FeedScrollViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.item_container);

            // 绑定前景图 (ID保持不变)
            imageView = view.findViewById(R.id.item_image);

            // 【新增】绑定背景图 (对应XML中的新ID)
            blurBg = view.findViewById(R.id.iv_blur_bg);

            titleView = view.findViewById(R.id.item_title);
            priceView = view.findViewById(R.id.item_price);
            addToCartButton = view.findViewById(R.id.btn_add_to_cart);
            buyNowButton = view.findViewById(R.id.btn_buy_now);
            playerView = view.findViewById(R.id.item_player_view);
        }
    }

    @Override
    public FeedScrollViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_scroll_item, parent, false); // 确保这里引用的是你修改过的新 XML 文件名
        return new FeedScrollViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedScrollViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final FeedItem item = feedItems.get(position);
        Context context = holder.itemView.getContext();

        // === 添加这行日志 ===
        android.util.Log.e("FeedDebug", "绑定数据: Position=" + position
                + ", Type=" + item.getType()
                + ", VideoUrl=" + item.getVideoUrl()
                + ", Title=" + item.getTitle());

        // --- 视图重置逻辑 ---
        holder.imageView.setVisibility(View.VISIBLE);
        if (holder.blurBg != null) {
            holder.blurBg.setVisibility(View.VISIBLE);
        }
        if (holder.playerView != null) {
            holder.playerView.setVisibility(View.GONE);
        }
        MediaLoaderUtils.releasePlayer(holder.player);

        // --- 图片/封面加载逻辑 (封装复用) ---
        String imageUrlToLoad = null;

        // 判断是视频还是图片
        boolean hasVideo = item.getVideoUrl() != null && !item.getVideoUrl().isEmpty();
        if (hasVideo) {
            // 视频类型：先显示封面图作为占位
            // 尝试获取视频封面，如果没有专门的封面字段，暂时用 imageUrl 代替，或者用视频首帧
            // 这里假设 item.getImageUrl() 就是视频封面
            imageUrlToLoad = item.getImageUrl();

            // 视频逻辑保持不变
            players.remove(position);
            if (allowVideoLoading) {
                scheduleDelayedVideoLoad(holder, position, item);
            }
        } else if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            // 图片类型
            imageUrlToLoad = item.getImageUrl();
        }

        // --- 执行双重图片加载 (高斯模糊效果) ---
        if (imageUrlToLoad != null && !imageUrlToLoad.isEmpty()) {
            // 1. 加载背景：高斯模糊
            // 使用 RequestOptions 应用 BlurTransformation
            if (holder.blurBg != null) {
                Glide.with(context)
                        .load(imageUrlToLoad)
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))) // 模糊半径25，采样3
                        .into(holder.blurBg);
            }

            // 2. 加载前景：清晰完整
            Glide.with(context)
                    .load(imageUrlToLoad)
                    .fitCenter() // 确保完整显示
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imageView);
        } else {
            // 没有图片时的默认处理
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
            if (holder.blurBg != null) {
                // 背景也设个默认色或默认图
                holder.blurBg.setImageResource(android.R.color.black);
            }
        }

        // --- 文本和按钮逻辑 (保持不变) ---
        holder.titleView.setText(item.getTitle() != null ? item.getTitle() : "无标题");

        if (item.getPrice() != null && !item.getPrice().isEmpty()) {
            holder.priceView.setVisibility(View.VISIBLE);
            holder.priceView.setText("¥" + item.getPrice());
        } else {
            holder.priceView.setVisibility(View.GONE);
        }

        // 点击事件
        holder.container.setOnClickListener(v -> {
            if (onItemClickListener != null) onItemClickListener.onItemClick(position, item);
        });
        holder.addToCartButton.setOnClickListener(v -> {
            if (onItemClickListener != null) onItemClickListener.onAddToCartClick(position, item);
        });
        holder.buyNowButton.setOnClickListener(v -> {
            if (onItemClickListener != null) onItemClickListener.onBuyNowClick(position, item);
        });
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    // --- 以下是视频播放相关逻辑 (保持不变) ---

    @Override
    public void onViewRecycled(@NonNull FeedScrollViewHolder holder) {
        super.onViewRecycled(holder);
        int position = holder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            visibleVideoPositions.remove(position);
            if (delayedLoadTasks.containsKey(position)) {
                new Handler(Looper.getMainLooper()).removeCallbacks(delayedLoadTasks.get(position));
                delayedLoadTasks.remove(position);
            }
            if (currentPlayingPosition != null && currentPlayingPosition == position) {
                currentPlayingPosition = null;
            }
        }
        MediaLoaderUtils.releasePlayer(holder.player);
        holder.player = null;
        if (position != RecyclerView.NO_POSITION) {
            players.remove(position);
        }
    }

    public void pauseVideoLoading() {
        allowVideoLoading = false;
        for (Runnable task : delayedLoadTasks.values()) {
            new Handler(Looper.getMainLooper()).removeCallbacks(task);
        }
        delayedLoadTasks.clear();
        for (Map.Entry<Integer, ExoPlayer> entry : players.entrySet()) {
            int position = entry.getKey();
            ExoPlayer player = entry.getValue();
            if (player != null && !visibleVideoPositions.contains(position)) {
                player.pause();
            }
        }
    }

    public void pauseAllVideos() {
        isAppInForeground = false;
        for (ExoPlayer player : players.values()) {
            if (player != null) player.pause();
        }
    }

    public void resumeAllVideos() {
        isAppInForeground = true;
        for (Map.Entry<Integer, ExoPlayer> entry : players.entrySet()) {
            int position = entry.getKey();
            ExoPlayer player = entry.getValue();
            if (player != null && (visibleVideoPositions.contains(position) ||
                    (currentPlayingPosition != null && currentPlayingPosition == position))) {
                player.play();
            }
        }
    }

    public void resumeVideoLoading(RecyclerView recyclerView) {
        allowVideoLoading = true;
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
            int middlePosition = (firstVisiblePosition + lastVisiblePosition) / 2;

            visibleVideoPositions.clear();
            for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                visibleVideoPositions.add(i);
            }
            managePlayerInstances(middlePosition);

            for (Map.Entry<Integer, ExoPlayer> entry : players.entrySet()) {
                int position = entry.getKey();
                ExoPlayer player = entry.getValue();
                if (player != null) {
                    if (visibleVideoPositions.contains(position)) player.play();
                    else player.pause();
                }
            }

            FeedScrollViewHolder middleHolder = (FeedScrollViewHolder) recyclerView.findViewHolderForAdapterPosition(middlePosition);
            if (middleHolder != null && middlePosition < getItemCount()) {
                final FeedItem middleItem = feedItems.get(middlePosition);
                
                // 新代码（修复）：只判断 URL 是否存在
                boolean hasVideo = middleItem.getVideoUrl() != null && !middleItem.getVideoUrl().isEmpty();
                if (hasVideo && middleHolder.player == null) {
                    scheduleDelayedVideoLoad(middleHolder, middlePosition, middleItem);
                }
            }

            for (int position = firstVisiblePosition; position <= lastVisiblePosition; position++) {
                if (position != middlePosition) {
                    FeedScrollViewHolder holder = (FeedScrollViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                    if (holder != null && position < getItemCount()) {
                        final FeedItem item = feedItems.get(position);

                        
                        // 新代码（修复）：只判断 URL 是否存在
                        boolean hasVideo = item.getVideoUrl() != null && !item.getVideoUrl().isEmpty();
                        if (hasVideo && holder.player == null) {
                            scheduleDelayedVideoLoad(holder, position, item);
                        }
                    }
                }
            }
        }
    }

    private void managePlayerInstances(int priorityPosition) {
        while (players.size() > MAX_CONCURRENT_PLAYERS) {
            Integer positionToRelease = null;
            for (Integer position : players.keySet()) {
                if (position != priorityPosition && !visibleVideoPositions.contains(position)) {
                    positionToRelease = position;
                    break;
                }
            }
            if (positionToRelease == null && players.size() > 0) {
                for (Integer position : players.keySet()) {
                    if (position != priorityPosition) {
                        positionToRelease = position;
                        break;
                    }
                }
            }
            if (positionToRelease != null) {
                MediaLoaderUtils.releasePlayer(players.get(positionToRelease));
                players.remove(positionToRelease);
                if (currentPlayingPosition != null && currentPlayingPosition == positionToRelease) {
                    currentPlayingPosition = null;
                }
            }
        }
        currentPlayingPosition = priorityPosition;
    }

    private void scheduleDelayedVideoLoad(final FeedScrollViewHolder holder, final int position, final FeedItem item) {
        if (delayedLoadTasks.containsKey(position)) {
            new Handler(Looper.getMainLooper()).removeCallbacks(delayedLoadTasks.get(position));
        }
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

    private void loadVideo(FeedScrollViewHolder holder, int position, FeedItem item) {
        Log.d("FeedDebug", "尝试加载视频: pos=" + position + ", url=" + item.getVideoUrl());
        if (!isAppInForeground) return;
        boolean isPriorityPosition = currentPlayingPosition != null && position == currentPlayingPosition;
        if (!MediaLoaderUtils.shouldCreatePlayer(players.size(), MAX_CONCURRENT_PLAYERS, isPriorityPosition)) {
            return;
        }

        if (holder.playerView != null) {
            holder.imageView.setVisibility(View.GONE);
            // 播放视频时，也可以选择隐藏模糊背景，或者留着当底图
            // holder.blurBg.setVisibility(View.GONE);

            holder.playerView.setVisibility(View.VISIBLE);

            holder.player = MediaLoaderUtils.createExoPlayer(
                    holder.itemView.getContext(),
                    holder.playerView,
                    item.getVideoUrl(),
                    isAppInForeground,
                    new Player.Listener() {
                        @Override
                        public void onPlaybackStateChanged(int playbackState) {}
                        @Override
                        public void onPlayerError(PlaybackException error) {}
                    }
            );
            players.put(position, holder.player);
        }
    }

    public void releaseAllPlayers() {
        for (Runnable task : delayedLoadTasks.values()) {
            new Handler(Looper.getMainLooper()).removeCallbacks(task);
        }
        delayedLoadTasks.clear();
        visibleVideoPositions.clear();
        for (ExoPlayer player : players.values()) {
            MediaLoaderUtils.releasePlayer(player);
        }
        players.clear();
    }
}