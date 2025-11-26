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

    // 数据相关
    private lateinit var feedService: FeedService
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private val feedList = mutableListOf<FeedItem>()

    // 用于模拟异步加载的Handler
    private val handler = Handler(Looper.getMainLooper())

    // 加载状态标志
    private var isLoadingMore = false

    // 分页相关
    private var currentPage = 1
    private val pageSize = 4  // 每页4条数据
    private var allData = listOf<FeedItem>()  // 存储所有数据
    private var hasMoreData = true  // 是否还有更多数据

    companion object {
        private const val TAG = "MainActivity"
        private const val SPAN_COUNT = 2 // 默认双列布局
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 处理系统栏边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 初始化视图
        initViews()

        // 初始化数据库
        initDatabase()

        // 初始化RecyclerView
        setupRecyclerView()

        // 初始化下拉刷新
        setupSwipeRefresh()

        // 加载数据
        loadData()
    }

    /**
     * 初始化所有视图组件
     */
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        progressBar = findViewById(R.id.progressBar)
    }

    /**
     * 初始化数据库
     */
    private fun initDatabase() {
        try {
            FeedItemData.initDatabase(this)
            Log.d(TAG, "数据库初始化成功")
        } catch (e: Exception) {
            Log.e(TAG, "数据库初始化失败", e)
            Toast.makeText(this, "数据库初始化失败", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 设置RecyclerView
     * 包括LayoutManager、Adapter和异构布局处理
     */
    private fun setupRecyclerView() {
        // 初始化FeedService
        feedService = FeedServiceImpl.getInstance()

        // 创建StaggeredGridLayoutManager（瀑布流布局管理器）
        layoutManager = StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL)

        // 设置LayoutManager
        recyclerView.layoutManager = layoutManager

        // 创建Adapter并设置点击事件监听
        feedAdapter = FeedAdapter(feedList)
        feedAdapter.setOnItemClickListener(object : FeedAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, feedItem: FeedItem) {
                handleItemClick(feedItem)
            }
        })

        // 设置Adapter
        recyclerView.adapter = feedAdapter

        // 添加滚动监听，实现上拉加载更多
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // 检查是否滚动到底部
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    // 滚动到底部，加载更多数据
                    loadMoreData()
                }
            }
        })
    }

    /**
     * 设置下拉刷新
     */
    private fun setupSwipeRefresh() {
        // 设置刷新时的颜色
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        // 设置下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    /**
     * 加载数据（首次加载）
     */
    private fun loadData() {
        // 显示加载指示器
        showLoading(true)

        // 模拟异步加载数据
        handler.postDelayed({
            try {
                // 从数据库获取所有数据
                allData = feedService.getFeedList(this)

                // 重置分页状态
                currentPage = 1
                feedList.clear()

                // 加载第一页数据（前4条）
                loadPageData()

                // 打印日志
                Log.d(TAG, "首次加载，总数据: ${allData.size} 条，当前显示: ${feedList.size} 条")

            } catch (e: Exception) {
                Log.e(TAG, "加载数据失败", e)
                Toast.makeText(this, "加载数据失败", Toast.LENGTH_SHORT).show()
            } finally {
                // 隐藏加载指示器
                showLoading(false)
            }
        }, 1000) // 延迟1秒模拟网络请求
    }

    /**
     * 刷新数据（下拉刷新）
     */
    private fun refreshData() {
        Log.d(TAG, "开始刷新数据")

        // 模拟异步刷新
        handler.postDelayed({
            try {
                // 重新获取所有数据
                allData = feedService.getFeedList(this)

                // 重置分页状态
                currentPage = 1
                feedList.clear()
                hasMoreData = true

                // 加载第一页数据
                loadPageData()

                Toast.makeText(this, "刷新成功", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "刷新完成，当前显示 ${feedList.size} 条数据")

            } catch (e: Exception) {
                Log.e(TAG, "刷新数据失败", e)
                Toast.makeText(this, "刷新失败", Toast.LENGTH_SHORT).show()
            } finally {
                // 停止刷新动画
                swipeRefreshLayout.isRefreshing = false
            }
        }, 1500) // 延迟1.5秒模拟网络请求
    }

    /**
     * 加载更多数据（上拉加载）
     */
    private fun loadMoreData() {
        // 如果正在加载或没有更多数据，则不加载
        if (isLoadingMore || !hasMoreData) {
            if (!hasMoreData) {
                Toast.makeText(this, "已经到底了", Toast.LENGTH_SHORT).show()
            }
            return
        }

        isLoadingMore = true
        Log.d(TAG, "加载更多数据，当前页: $currentPage")

        // 显示正在加载提示
        Toast.makeText(this, "正在加载", Toast.LENGTH_SHORT).show()

        // 模拟异步加载更多数据
        handler.postDelayed({
            try {
                // 页码+1
                currentPage++

                // 加载下一页数据
                loadPageData()

                Log.d(TAG, "加载更多完成，当前显示 ${feedList.size} 条数据")
            } catch (e: Exception) {
                Log.e(TAG, "加载更多失败", e)
                Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show()
                currentPage-- // 失败时回退页码
            } finally {
                isLoadingMore = false
            }
        }, 1000) // 延迟1秒模拟网络请求
    }

    /**
     * 加载指定页的数据
     */
    private fun loadPageData() {
        // 计算起始和结束索引
        val startIndex = (currentPage - 1) * pageSize
        val endIndex = minOf(startIndex + pageSize, allData.size)

        // 检查是否还有更多数据
        hasMoreData = endIndex < allData.size

        if (startIndex < allData.size) {
            // 获取当前页的数据
            val pageData = allData.subList(startIndex, endIndex)

            // 添加到显示列表
            val oldSize = feedList.size
            feedList.addAll(pageData)

            // 通知适配器数据变化
            if (currentPage == 1) {
                // 首次加载，刷新全部
                feedAdapter.notifyDataSetChanged()
            } else {
                // 加载更多，只刷新新增部分
                feedAdapter.notifyItemRangeInserted(oldSize, pageData.size)
            }

            // 注释掉异构布局处理，实现纯双列瀑布流
            // handleHeterogeneousLayout()

            Log.d(TAG, "加载第 $currentPage 页，本页 ${pageData.size} 条，总共 ${feedList.size} 条")
        } else {
            hasMoreData = false
            Toast.makeText(this, "已经到底了", Toast.LENGTH_SHORT).show()
        }
    }



    /**
     * 处理卡片点击事件
     */
    private fun handleItemClick(feedItem: FeedItem) {
        Log.d(TAG, "点击了卡片: ${feedItem.title}, type=${feedItem.type}")

        // 跳转到详情页
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_FEED_ITEM, feedItem)
        startActivity(intent)

        // 可以根据type做不同的处理
        when (feedItem.type) {
            0 -> {
                // 商品卡片
                Log.d(TAG, "打开商品详情")
            }
            1 -> {
                // 活动卡片
                Log.d(TAG, "打开活动详情")
            }
            2 -> {
                // 视频卡片
                Log.d(TAG, "打开视频详情")
            }
        }
    }

    /**
     * 显示/隐藏加载指示器
     */
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        // 移除所有待处理的消息
        handler.removeCallbacksAndMessages(null)
    }
}
