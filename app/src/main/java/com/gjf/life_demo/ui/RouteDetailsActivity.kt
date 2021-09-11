package com.gjf.life_demo.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.route.WalkPath
import com.gjf.life_demo.R
import com.gjf.life_demo.adapter.WalkSegmentListAdapter
import com.gjf.life_demo.utils.getFriendlyLength
import com.gjf.life_demo.utils.getFriendlyTime

/***
 * RouteDetailsActivity: 展示导航详情的页面
 * 相关的导航路线在MapFragment中通过Intent传输过来
 */
class RouteDetailsActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var tvTitle: TextView
    private lateinit var tvTime: TextView
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_details)
        toolbar = findViewById(R.id.toolbar)
        tvTitle = findViewById(R.id.tv_title)
        tvTime = findViewById(R.id.tv_time)
        // 返回MapFragment
        toolbar.setNavigationOnClickListener {
            finish()
        }
        recyclerView = findViewById(R.id.detailsRecyclerView)
        // 从Intent中获取导航路线详情
        val intent = intent
        walkDetails(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun walkDetails(intent: Intent) {
        tvTitle.text = "步行路线规划";
        val walkPath: WalkPath? = intent.getParcelableExtra("path");
        val dur: String? = walkPath?.duration?.let { getFriendlyTime(it.toInt()) };
        val dis: String? = walkPath?.distance?.let { getFriendlyLength(it) };
        tvTime.text = "$dur($dis)";
        recyclerView.layoutManager = LinearLayoutManager(this);
        recyclerView.adapter = walkPath?.steps?.let { WalkSegmentListAdapter(it) }
    }

}