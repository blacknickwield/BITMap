package com.gjf.life_demo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gjf.life_demo.R
import com.gjf.life_demo.adapter.TypePlaceAdapter
import com.gjf.life_demo.utils.RecyclerViewAnimation
import com.gjf.life_demo.utils.SpacingItemDecoration
import com.gjf.life_demo.viewmodels.PlaceViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/***
 * TypeFragment: 展示学习、生活、美食、其他类型place的页面
 */
class TypePlaceFragment : Fragment() {
    /***
     * 类型参数，判断该fragment显示何种类型的place
     * 0：学习 1：生活 2：美食 3：其他
     */
    private var type: Int? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    var placeViewModel: PlaceViewModel? = null
    lateinit var placeAdapter: TypePlaceAdapter
    lateinit var placeRecyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_type_place, container, false)
        placeRecyclerView = root.findViewById(R.id.recyclerView)
        placeRecyclerView.layoutManager = LinearLayoutManager(activity)
        return root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /***
         * 根据不同类型参数获取不同类型的place数据
         * 0：学习 1：生活 2：美食 3：其他
         */
        placeAdapter = TypePlaceAdapter(
            when (type) {
                0 -> placeViewModel!!.studyPlaces
                1 -> placeViewModel!!.lifeplaces
                2 -> placeViewModel!!.eatPlaces
                3 -> placeViewModel!!.otherPlaces
                else -> placeViewModel!!.places
            }, this
        )

        placeRecyclerView.adapter = placeAdapter
        RecyclerViewAnimation.runLayoutAnimation(placeRecyclerView)
        // 修饰器
        val topSpacingDecorator = SpacingItemDecoration(30)
        placeRecyclerView.addItemDecoration(topSpacingDecorator)
    }

    /***
     * 回调函数获取place列表
     * 数据库中place信息发生变动后在NearByFragment中回调该函数
     * 获取到最新的place数据
     * 更新recyclerview
     */
    fun notifyDataSetChanged() {
        if (this::placeAdapter.isInitialized) {
            placeAdapter = TypePlaceAdapter(
                when (type) {
                    0 -> placeViewModel!!.studyPlaces
                    1 -> placeViewModel!!.lifeplaces
                    2 -> placeViewModel!!.eatPlaces
                    3 -> placeViewModel!!.otherPlaces
                    else -> placeViewModel!!.places
                }, this
            )
            placeRecyclerView.adapter = placeAdapter
            placeAdapter.notifyDataSetChanged()
            RecyclerViewAnimation.runLayoutAnimation(placeRecyclerView)
        }

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TypePlaceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(type: Int, param2: String) =
            TypePlaceFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, type)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}