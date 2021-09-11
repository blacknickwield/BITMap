package com.gjf.life_demo.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.gjf.life_demo.R
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.listener.OnBannerListener
import com.youth.banner.loader.ImageLoader

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/***
 * AboutFragment: 展示关于页面
 * OnBannerListener: Banner轮播控件接口
 */
class AboutFragment : Fragment(), OnBannerListener {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var banner: Banner
    private lateinit var btnAbout: Button

    // 轮播图片列表
    private val imageList: ArrayList<Int> = arrayListOf(
        R.drawable.ic_goose,
        R.drawable.ic_north_lake,
        R.drawable.bridge,
        R.drawable.summer,
        R.drawable.snow
    )

    // 轮播图片标题
    private val titleList: ArrayList<String> =
        arrayListOf("大白鹅", "北湖风光", "北理桥", "盛夏", "图书馆 初雪")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_about, container, false)

        btnAbout = root.findViewById(R.id.btnAbout)
        btnAbout.setOnClickListener {
            AlertDialog.Builder(activity).setTitle("关于").setMessage("此APP为金旭亮老师安卓基础开发技术课程结课设计作品")
                .show()
        }
        banner = root.findViewById(R.id.banner)
        banner.setImageLoader(object : ImageLoader() {
            override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) {
                Glide.with(activity).load<Any>(path).into(imageView)
            }
        })
        banner.setBannerAnimation(Transformer.Accordion)    // 设置轮播动画
        banner.setImages(imageList)    // 设置图片资源
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)   // 设置banner显示样式（带标题的样式）
        banner.setBannerTitles(titleList)    // 设置标题集合（当banner样式有显示title时）
        banner.setIndicatorGravity(BannerConfig.CENTER)    // 设置指示器位置（即图片下面的那个小圆点）
        banner.setDelayTime(3000)   // 设置轮播时间3秒切换下一图
        banner.setOnBannerListener(this)    // 设置监听
        banner.start()  // 开始进行banner渲染
        return root
    }

    override fun onStart() {
        super.onStart()
        banner.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        banner.stopAutoPlay()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AboutFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AboutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    /**
     * 点击轮播图片后回调的函数（未使用）
     */
    override fun OnBannerClick(position: Int) {
    }
}