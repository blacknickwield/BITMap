package com.gjf.life_demo.utils

import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.gjf.life_demo.R

/**
 * RecyclerView动画
 * RecyclerView展示数据时的动画效果
 */
object RecyclerViewAnimation {
    //数据变化时显示动画
    fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter!!.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }
}