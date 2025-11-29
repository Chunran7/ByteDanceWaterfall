package com.team.bytedancewaterfall.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.team.bytedancewaterfall.R
import com.team.bytedancewaterfall.adapter.FeedAdapter
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem
import com.team.bytedancewaterfall.data.service.FeedService
import com.team.bytedancewaterfall.data.service.impl.FeedServiceImpl
import com.team.bytedancewaterfall.data.vurtualData.FeedItemData

/**
 * 主界面Activity
 * 负责展示瀑布流内容，处理异构布局和用户交互
 */
class MainActivity : AppCompatActivity() {

    // UI组件
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar

    // 数据与适配器
    private lateinit var feedService: FeedService
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private val feedList = mutableListOf<FeedItem>()

    // 状态控制
    private val handler = Handler(Looper.getMainLooper())
    private var isLoading = false
    private var hasMoreData = true

    companion object {
        private const val TAG = "MainActivity"
        private const val SPAN_COUNT = 2 // 默认双列布局
        private const val PRELOAD_THRESHOLD = 5 // 预加载阈值：滑动到倒数第5个时加载更多
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        setupSystemUI()
        initViews()
        initDatabase()
        setupRecyclerView()
        setupSwipeRefresh()
        fetchData(isRefresh = true, isInitialLoad = true) // 初始加载
    }

    private fun setupSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun initDatabase() {
        try {
            FeedItemData.initDatabase(this)
            Log.d(TAG, "数据库初始化成功")
        } catch (e: Exception) {
            Log.e(TAG, "数据库初始化失败", e)
            Toast.makeText(this, "数据库初始化失败", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        feedService = FeedServiceImpl.getInstance()
        layoutManager = StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        recyclerView.layoutManager = layoutManager

        // 使用 Lambda 表达式设置点击监听，代码更简洁
        feedAdapter = FeedAdapter(feedList)
        feedAdapter.setOnItemClickListener(object : FeedAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, feedItem: FeedItem) {
                handleItemClick(feedItem)
            }
        })
        recyclerView.adapter = feedAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0) return // 只在向上滑动时检查

                val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                val lastVisibleItem = lastVisibleItemPositions.maxOrNull() ?: 0
                val totalItemCount = layoutManager.itemCount

                // 专业的预加载逻辑
                if (!isLoading && hasMoreData && totalItemCount <= lastVisibleItem + PRELOAD_THRESHOLD) {
                    fetchData(isRefresh = false)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 停止滚动时，重新计算布局，防止卡片位置发生变化
                    layoutManager.invalidateSpanAssignments()
                }
            }
        })
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        swipeRefreshLayout.setOnRefreshListener {
            fetchData(isRefresh = true) // 下拉刷新
        }
    }

    /**
     * 统一的数据获取方法，整合了初始加载、刷新和加载更多
     * @param isRefresh 是否为刷新操作 (清空列表)
     * @param isInitialLoad 是否为首次加载 (用于控制中央加载条)
     */
    private fun fetchData(isRefresh: Boolean, isInitialLoad: Boolean = false) {
        if (isLoading) return
        isLoading = true

        if (isInitialLoad) {
            progressBar.visibility = View.VISIBLE
        }
        if (isRefresh) {
            hasMoreData = true // 刷新时重置状态
        }

        Log.d(TAG, "开始获取数据, isRefresh: $isRefresh, isInitialLoad: $isInitialLoad")

        // 模拟网络延迟
        handler.postDelayed({
            try {
                val newData = feedService.getFeedList(this)
                if (newData.isNotEmpty()) {
                    if (isRefresh) {
                        val oldSize = feedList.size
                        feedList.clear()
                        feedAdapter.notifyItemRangeRemoved(0, oldSize)
                        feedList.addAll(newData)
                        feedAdapter.notifyItemRangeInserted(0, newData.size)
                        recyclerView.scrollToPosition(0) // 刷新后滚动到顶部
                        Log.d(TAG, "刷新完成，共 ${newData.size} 条")
                    } else {
                        val oldSize = feedList.size
                        feedList.addAll(newData)
                        feedAdapter.notifyItemRangeInserted(oldSize, newData.size)
                        Log.d(TAG, "加载更多完成, 新增 ${newData.size} 条")
                    }
                } else {
                    if (!isRefresh) {
                        hasMoreData = false
                        Toast.makeText(this, "已经到底了", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "没有更多数据了")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "获取数据失败", e)
                Toast.makeText(this, "获取数据失败", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
                if (isInitialLoad) {
                    progressBar.visibility = View.GONE
                }
                if (swipeRefreshLayout.isRefreshing) {
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }, 1200)
    }

    private fun handleItemClick(feedItem: FeedItem) {
        try {
            Log.d(TAG, "点击了卡片: ${feedItem.title}")
            // 进入到单栏布局
            val intent = Intent(this, FeedScrollActivity::class.java)
            
            // 安全地获取当前点击项在列表中的索引
            val currentIndex = feedList.indexOf(feedItem)
            if (currentIndex >= 0) {
                // 创建一个新的ArrayList来存储从当前索引开始的数据
                val remainingItems = ArrayList<FeedItem>()
                
                // 手动添加从当前索引开始的所有元素，避免subList可能带来的问题
                for (i in 0 until feedList.size) {
                    remainingItems.add(feedList[i])
                }
                
                // 传递从当前点击项开始的子列表数据
                intent.putExtra(FeedScrollActivity.EXTRA_FEED_ITEMS, remainingItems)
                // 传递起始索引
                intent.putExtra(FeedScrollActivity.EXTRA_START_INDEX, currentIndex)
                
                // 启动Activity
                startActivity(intent)
            } else {
                Log.e(TAG, "未找到FeedItem在列表中的位置")
                Toast.makeText(this, "加载失败，请重试", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // 捕获所有可能的异常，避免应用闪退
            Log.e(TAG, "点击卡片时发生错误: ${e.message}", e)
            Toast.makeText(this, "加载失败，请重试", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
