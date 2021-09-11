package com.gjf.life_demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.gjf.life_demo.R
import com.gjf.life_demo.data.entity.Place
import com.gjf.life_demo.ui.TypePlaceFragment
import com.gjf.life_demo.viewholder.PlaceViewHolder

/**
 * 展示学习、生活、美食和其他类型地点的RecyclerView的适配器
 * 包容要显示的数据集合，并且实现数据的提取与显示工作
 * 由于在这些页面上不能进行删除和添加等操作，故与PlaceAdaptor不同
 */
class TypePlaceAdapter(private val data: List<Place>, typePlaceFragment: TypePlaceFragment)  :
    RecyclerView.Adapter<PlaceViewHolder>() {
    private val fragment = typePlaceFragment
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
        holder.itemView.findViewById<ImageView>(R.id.iv_check).visibility = View.GONE
    }

}