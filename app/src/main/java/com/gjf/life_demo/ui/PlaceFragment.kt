package com.gjf.life_demo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.gjf.life_demo.R
import com.gjf.life_demo.adapter.TabsAdapter
import com.gjf.life_demo.listener.FragmentCallBackListener
import com.gjf.life_demo.listener.NotifyTextChangeListener
import com.gjf.life_demo.listener.PlaceViewModelListener
import com.gjf.life_demo.viewmodels.PlaceViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/***
 * NearByFragment与几个TypePlaceFragment的父Fragment，用于实现ViewPager2
 * NotifyTextChangeListener: 信息变更接口，在NearByFragment中要改变PlaceFragment中样式以及数据变更时回调相关函数
 * PlaceViewModelListener: 在NearByFragment中PlaceViewModel中place数据后回调该接口中的函数，
 * 传回最新的PlaceViewModel，更新TypePlaceFragment的PlaceViewModel
 */
class PlaceFragment : Fragment(), NotifyTextChangeListener, PlaceViewModelListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    private val fragmentList: ArrayList<Fragment> = arrayListOf()
    private val titleList: List<String> = mutableListOf("全部", "学习", "生活", "美食", "其他")

    lateinit var viewPager: ViewPager2
    lateinit var tabLayout: TabLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_place, container, false)
        viewPager = root.findViewById(R.id.viewPager2)
        tabLayout = root.findViewById(R.id.tabLayout)
        tvEditTest = root.findViewById(R.id.tvEditTest)
        return root
    }

    lateinit var tvEditTest: TextView
    lateinit var fragmemtCallBackListener: FragmentCallBackListener
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nearByFragment = AllPlaceFragment.newInstance(this, "", "")
        fragmentList.add(nearByFragment)
        fragmemtCallBackListener = nearByFragment as FragmentCallBackListener
        val studyPlaceFragment = TypePlaceFragment.newInstance(0, "")
        val lifePlaceFragment = TypePlaceFragment.newInstance(1, "")
        val eatPlaceFragment = TypePlaceFragment.newInstance(2, "")
        val otherPlaceFragment = TypePlaceFragment.newInstance(3, "")
        fragmentList.add(studyPlaceFragment)
        fragmentList.add(lifePlaceFragment)
        fragmentList.add(eatPlaceFragment)
        fragmentList.add(otherPlaceFragment)


        val adapter = activity?.let { TabsAdapter(it, fragmentList, titleList) }
        viewPager.adapter = adapter
        val pageChangeCallBack = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                tvEditTest.visibility = when (position) {
                    0 -> View.VISIBLE
                    else -> View.GONE
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        }
        viewPager.registerOnPageChangeCallback(pageChangeCallBack)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if (adapter != null) {
                tab.text = adapter.getPageTitle(position)
                if (position == 0)
                    tvEditTest.visibility = View.VISIBLE
                else
                    tvEditTest.visibility = View.GONE
            }
        }.attach()

        /***
         * 点击编辑按钮后调用该函数
         * 在NearByFragment中改变item的形式
         */
        tvEditTest.setOnClickListener {
            fragmemtCallBackListener.onClickListener()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlaceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlaceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun textVisibilityChange(visibility: Int) {
        tvEditTest.visibility = visibility
    }

    override fun textContentChange(contents: String) {
        tvEditTest.text = contents
    }

    override fun dataSetChange() {
        for (index in (1..4)) {
            (fragmentList[index] as TypePlaceFragment).notifyDataSetChanged()
        }

    }


    override fun getPlaceViewModel(placeViewModel: PlaceViewModel) {
        for (index in (1..4))
            (fragmentList[index] as TypePlaceFragment).placeViewModel = placeViewModel
    }
}