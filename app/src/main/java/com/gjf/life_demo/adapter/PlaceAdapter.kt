package com.gjf.life_demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gjf.life_demo.R
import com.gjf.life_demo.data.entity.Place
import com.gjf.life_demo.ui.AllPlaceFragment
import com.gjf.life_demo.viewholder.PlaceViewHolder

/**
 * 展示所有地点的RecyclerView的适配器
 * 包容要显示的数据集合，并且实现数据的提取与显示工作
 * 由于需要在此页面上进行删除和添加等操作，故与TypePlaceAdaptor不同
 */
class PlaceAdapter(private val data: List<Place>, allPlaceFragment: AllPlaceFragment) :
    RecyclerView.Adapter<PlaceViewHolder>() {
    private val fragment = allPlaceFragment
    private val STATE_DEFAULT = 0 // 默认状态
    private val STATE_EDIT = 1 // 编辑状态
    var count = 0
    var editMode = STATE_DEFAULT
    var editorStatus = false //是否为编辑状态
    lateinit var tvDelete: TextView
    //调用工厂方法实例化ViewHolder对象
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        return PlaceViewHolder(view)
    }
    //获取要显示的数据集行数
    override fun getItemCount(): Int {
        return data.size
    }
    //从数据集合中提取指定位置的数据对象，传给ViewHolder对象显示
    override fun onBindViewHolder(
        holder: PlaceViewHolder,
        position: Int
    ) {
        val item = data[position]
        holder.bind(item)
        if (editMode == STATE_DEFAULT) {
            holder.itemView.findViewById<ImageView>(R.id.iv_check).visibility = View.GONE
        } else {
            //显示编辑按钮，显示之后再做点击之后的判断
            holder.itemView.findViewById<ImageView>(R.id.iv_check).visibility = View.VISIBLE
            if (item.isSelected) { //点击时，true 选中
                holder.itemView.findViewById<ImageView>(R.id.iv_check).setBackgroundResource(R.mipmap.icon_choose_selected)
            } else { //false 取消选中
                holder.itemView.findViewById<ImageView>(R.id.iv_check).setBackgroundResource(R.mipmap.icon_choose_default)
            }
        }

        holder.itemView.setOnClickListener {
            /***
             * 处于可编辑状态
             * 可对item进行选择
             */
            if (editorStatus) {
                if (item.isSelected) {
                    count--;
                    data[position].isSelected = false
                } else {
                    count++
                    data[position].isSelected = true
                }
                if (count == 0) {
                    tvDelete.text = "删除"
                } else {
                    tvDelete.text = "删除:${count}"
                }
                notifyDataSetChanged()
            }
        }

        // 设置长按进入可编辑状态
        holder.itemView.setOnLongClickListener {
            fragment.updateEditState()
            true
        }
    }

}