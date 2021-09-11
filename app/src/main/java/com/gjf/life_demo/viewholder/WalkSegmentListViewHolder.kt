package com.gjf.life_demo.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.route.WalkStep
import com.gjf.life_demo.R
import com.gjf.life_demo.utils.getWalkActionID

class WalkSegmentListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val lineName: TextView = itemView.findViewById(R.id.bus_line_name)
    private val dirIcon: ImageView = itemView.findViewById(R.id.bus_dir_icon)
    private val dirUp: ImageView = itemView.findViewById(R.id.bus_dir_icon_up)
    private val dirDown: ImageView = itemView.findViewById(R.id.bus_dir_icon_down)
    private val splitLine: ImageView = itemView.findViewById(R.id.bus_seg_split_line)

    /***
     * 根据flag参数来判断站点的类型
     * 0表示起点，-1表示终点，其他表示普通站点
     */
    fun bind(item: WalkStep, flag: Int) {
        when (flag) {
            // 起点
            0 -> {
                dirIcon.setImageResource(R.drawable.dir_start);
                lineName.text = "出发"
                dirUp.visibility = View.INVISIBLE
                dirDown.visibility = View.VISIBLE
                splitLine.visibility = View.INVISIBLE
            }
            // 终点
            -1 -> {
                dirIcon.setImageResource(R.drawable.dir_end)
                lineName.text = "到达终点"
                dirUp.visibility = View.VISIBLE
                dirDown.visibility = View.INVISIBLE
            }
            // 普通站点
            else -> {
                splitLine.visibility = View.VISIBLE
                dirUp.visibility = View.VISIBLE
                dirDown.visibility = View.VISIBLE
                val actionName = item.action
                val resID = getWalkActionID(actionName)
                dirIcon.setImageResource(resID)
                lineName.text = item.instruction
            }
        }
    }

    companion object {
        //使用工厂方法实例化ViewHolder对象
        fun from(parent: ViewGroup): PlaceViewHolder {
            //加载布局文件并实例化
            val layoutInflater = LayoutInflater.from(parent.context)
            val root = layoutInflater.inflate(
                R.layout.segment_item,
                parent, false
            )
            return PlaceViewHolder(root)
        }
    }
}