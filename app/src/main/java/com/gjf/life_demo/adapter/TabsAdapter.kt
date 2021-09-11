package com.gjf.life_demo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/***
 * 卡片式布局适配器
 */
class TabsAdapter(
    fa: FragmentActivity,
    private val fragmentList: List<Fragment>, //要显示的Fragment集合
    private val titleList: List<String>  //每个Fragment对应的Tab页标题
) : FragmentStateAdapter(fa) {
    //依据当前位置取出要显示的Fragment
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    //获取要显示的选项卡
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    //提取页面标题
    fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }
}