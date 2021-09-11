package com.gjf.life_demo.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gjf.life_demo.R
import com.gjf.life_demo.app.BITApplication
import com.gjf.life_demo.data.entity.Place
import java.io.File

class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val myImageView: ImageView = itemView.findViewById(R.id.myImageView)
    private val tvMyTitle: TextView = itemView.findViewById(R.id.tvMyTitle)
    private val tvMySubtitle: TextView = itemView.findViewById(R.id.tvMySubtitle)
    //依据传入的数据对象，更新对应控件的值
    fun bind(item: Place) {
        /***
         * Place的src属性存储相对应的图片的存储地址
         * 故对其进行split即可得到存储地址
         * 再使用Glide加载图片
         */
        val nameList = item.src?.split('/')
        val fileName = nameList?.last()
        val newFile = File(BITApplication.context?.filesDir, fileName)
        Glide.with(BITApplication.context).load(newFile).asBitmap().into(myImageView)
        tvMyTitle.text = item.name
        tvMySubtitle.text = item.details
    }

    companion object {
        //使用工厂方法实例化ViewHolder对象
        fun from(parent: ViewGroup): PlaceViewHolder {
            //加载布局文件并实例化
            val layoutInflater = LayoutInflater.from(parent.context)
            val root = layoutInflater.inflate(
                R.layout.place_item,
                parent, false
            )
            return PlaceViewHolder(root)
        }
    }
}