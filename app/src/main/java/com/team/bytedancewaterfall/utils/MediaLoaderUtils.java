package com.team.bytedancewaterfall.utils;

import android.content.Context;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.LoadControl;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelection;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.ui.PlayerView;

/**
 * MediaLoaderUtils
 * 媒体资源加载工具类，封装视频播放相关的配置和初始化逻辑
 */
public class MediaLoaderUtils {
    
    // 视频播放配置常量
    public static final int MIN_BUFFER_DURATION = 15000; // 最小缓冲区持续时间（毫秒）
    public static final int MAX_BUFFER_DURATION = 30000; // 最大缓冲区持续时间（毫秒）
    public static final int BUFFER_FOR_PLAYBACK_DURATION = 2500; // 播放前缓冲区持续时间（毫秒）
    public static final int BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_DURATION = 5000; // 重新缓冲后播放前缓冲区持续时间（毫秒）
    
    /**
     * 创建并配置ExoPlayer实例
     * 
     * @param context 上下文
     * @param playerView 播放器视图
     * @param videoUrl 视频URL
     * @param playWhenReady 是否准备好就播放
     * @param playerListener 播放状态监听器
     * @return 配置好的ExoPlayer实例
     */
    @OptIn(markerClass = UnstableApi.class)
    public static ExoPlayer createExoPlayer(Context context, PlayerView playerView, String videoUrl, 
                                          boolean playWhenReady, Player.Listener playerListener) {
        // 配置自适应轨道选择器
        AdaptiveTrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        TrackSelector trackSelector = new DefaultTrackSelector(context, adaptiveTrackSelectionFactory);
        
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
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(context)
                .setEnableDecoderFallback(true) // 启用解码器回退
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON); // 使用扩展渲染器
        
        // 创建ExoPlayer实例，应用优化配置
        ExoPlayer player = new ExoPlayer.Builder(context)
                .setTrackSelector(trackSelector)
                .setLoadControl(loadControl)
                .setRenderersFactory(renderersFactory)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(context))
                .build();
        
        if (playerView != null) {
            playerView.setPlayer(player);
            playerView.setUseController(false); // 禁用控制器，保持自动播放
        }
        
        // 设置循环播放
        player.setRepeatMode(Player.REPEAT_MODE_ONE); // 单个视频循环
        player.setPlayWhenReady(playWhenReady); // 设置是否自动播放
        
        // 添加播放状态监听
        if (playerListener != null) {
            player.addListener(playerListener);
        }
        
        // 创建媒体项
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(videoUrl)
                .setLiveConfiguration(new MediaItem.LiveConfiguration.Builder()
                        .setMaxPlaybackSpeed(1.0f)
                        .build())
                .build();
        
        player.setMediaItem(mediaItem);
        
        // 准备播放器
        player.prepare();
        
        return player;
    }
    
    /**
     * 释放播放器资源
     * 
     * @param player 要释放的ExoPlayer实例
     */
    public static void releasePlayer(ExoPlayer player) {
        if (player != null) {
            player.release();
        }
    }
    
    /**
     * 检查是否达到播放器数量限制
     * 
     * @param currentCount 当前播放器数量
     * @param maxCount 最大播放器数量
     * @param isPriorityPosition 是否为优先位置
     * @return 是否允许创建新播放器
     */
    public static boolean shouldCreatePlayer(int currentCount, int maxCount, boolean isPriorityPosition) {
        // 如果当前播放器数量小于最大限制，允许创建
        if (currentCount < maxCount) {
            return true;
        }
        
        // 如果已达到限制，但当前位置是优先位置，也允许创建
        return isPriorityPosition;
    }
}