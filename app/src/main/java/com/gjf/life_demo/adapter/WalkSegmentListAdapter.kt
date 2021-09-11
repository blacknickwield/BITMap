package com.gjf.life_demo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.route.WalkStep
import com.gjf.life_demo.R
import com.gjf.life_demo.viewholder.WalkSegmentListViewHolder

/***
 * 展示导航路线详情的RecyclerView的适配器
 */
class WalkSegmentListAdapter(private val data: List<WalkStep>) :
    RecyclerView.Adapter<WalkSegmentListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkSegmentListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.segment_item, parent, false)
        return WalkSegmentListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: WalkSegmentListViewHolder, position: Int) {
        val item = data[position]
        /***
         * 由于需要根据当前地点是起点、终点还是其他站点设置不同的图标
         * 故在bind函数中传入额外的参数来判断
         * 0表示起点，其他表示普通站点
         * 因为在holder中无法获取路线站点的长度，故用-1来表示终点
         */
        holder.bind(item, when(position) {
            data.size-1 -> -1
            else -> position
        })
    }

}