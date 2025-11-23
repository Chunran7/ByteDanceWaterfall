package com.team.bytedancewaterfall

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.team.bytedancewaterfall.adapter.FeedAdapter
import com.team.bytedancewaterfall.data.Repository
import com.team.bytedancewaterfall.data.database.FeedItemDatabaseHelper
import com.team.bytedancewaterfall.data.pojo.entity.FeedItem
import com.team.bytedancewaterfall.data.service.FeedService
import com.team.bytedancewaterfall.data.service.impl.FeedServiceImpl
import com.team.bytedancewaterfall.data.vurtualData.FeedItemData


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val adapter = FeedAdapter(Repository.getMockData())
        recyclerView.adapter = adapter
/*
        // 在 MainActivity.java 或 Application 类中
        val dbHelper = FeedItemDatabaseHelper(this)
        val db = dbHelper.getWritableDatabase() // 触发数据库创建*/
        // 初始化数据库
        FeedItemData.initDatabase(this);
        val feedService: FeedService = FeedServiceImpl()
        val feedList: List<FeedItem> = feedService.getFeedList(this)
        for (feedItem in feedList){
            print(feedItem)
        }

    }
}