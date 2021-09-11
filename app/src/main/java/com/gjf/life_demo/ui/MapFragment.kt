package com.gjf.life_demo.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.*
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.bumptech.glide.Glide
import com.gjf.life_demo.R
import com.gjf.life_demo.app.BITApplication
import com.gjf.life_demo.data.entity.Place
import com.gjf.life_demo.overlay.WalkRouteOverlay
import com.gjf.life_demo.listener.PlaceListener
import com.gjf.life_demo.utils.convertToLatLng
import com.gjf.life_demo.utils.getFriendlyLength
import com.gjf.life_demo.utils.getFriendlyTime
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import kotlin.concurrent.thread

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/***
 * MapFragment：展示地图的页面
 * AMapLocationListener, LocationSource: 地图定位接口，定位后回调相关函数处理定位结果
 * RouteSearch.OnRouteSearchListener: 导航接口，导航后回调相关函数处理导航结果
 * PlaceListener: 地点列表接口，NearByFragment中获取完place后调用相关函数，在MapFragment中获取place列表
 */
class MapFragment : Fragment(), AMapLocationListener, LocationSource,
    PlaceListener, RouteSearch.OnRouteSearchListener {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mapView: MapView
    lateinit var aMap: AMap
    lateinit var nowPosition: LatLonPoint
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var myLocationClient: AMapLocationClient   // 定位
    lateinit var myLocationOption: AMapLocationClientOption // 定位模式
    lateinit var myOnLocationChangedListener: LocationSource.OnLocationChangedListener

    /***
     * 定位初始化
     */
    private fun initLocation() {
        myLocationClient = AMapLocationClient(BITApplication.context)
        myLocationClient.setLocationListener(this)
        myLocationOption = AMapLocationClientOption()
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        myLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        myLocationOption.isOnceLocationLatest = true;
        //设置是否返回地址信息（默认返回地址信息）
        myLocationOption.isNeedAddress = true;
        //设置定位请求超时时间，单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        myLocationOption.httpTimeOut = 20000;
        //关闭缓存机制，高精度定位会产生缓存。
        myLocationOption.isLocationCacheEnable = false;
        //给定位客户端对象设置定位参数
        myLocationClient.setLocationOption(myLocationOption)
    }

    lateinit var dialog: AlertDialog
    lateinit var tvNewPlaceName: TextView
    lateinit var tvNewPlaceAddress: TextView
    lateinit var imgNewPlace: ImageView
    lateinit var btnGotoNewPlaceConfirm: Button
    lateinit var btnGotoNewPlaceCancel: Button

    /***
     * 地图初始化
     */
    private fun initMap(root: View, savedInstanceState: Bundle?) {
        mapView = root.findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)
        aMap = mapView.map;
        val settings = aMap.uiSettings
        settings.isCompassEnabled = true;// 设置指南针是否显示
        settings.isZoomControlsEnabled = true;// 设置缩放按钮是否显示
        settings.isScaleControlsEnabled = true;// 设置比例尺是否显示
        settings.isRotateGesturesEnabled = true;// 设置地图旋转是否可用
        settings.isTiltGesturesEnabled = true;// 设置地图倾斜是否可用
        aMap.setOnMarkerClickListener {
            // alpha=0表示当前marker被隐藏，不可点击
            if (it.alpha == 0F) {
                Toast.makeText(activity, "alpha==0", Toast.LENGTH_SHORT).show()
                return@setOnMarkerClickListener true
            }
            Toast.makeText(
                activity,
                "${it.position.latitude} ${it.position.longitude}",
                Toast.LENGTH_SHORT
            ).show()
            val dialogView = View.inflate(activity, R.layout.dialog_goto_newplace, null)
            tvNewPlaceName = dialogView.findViewById(R.id.tvNewPlaceName)
            tvNewPlaceAddress = dialogView.findViewById(R.id.tvNewPlaceAddress)
            imgNewPlace = dialogView.findViewById(R.id.imgNewPlace)
            btnGotoNewPlaceConfirm = dialogView.findViewById(R.id.btnGotoNewPlaceConfirm)
            //TODO 导航到此处
            val destinationLatitude = it.position.latitude
            val destinationLongitude = it.position.longitude
            btnGotoNewPlaceConfirm.setOnClickListener {
                fromAndTo = RouteSearch.FromAndTo(
                    nowPosition,
                    LatLonPoint(destinationLatitude, destinationLongitude)
                )
                routeSearch = RouteSearch(BITApplication.context)
                walkRouteQuery = RouteSearch.WalkRouteQuery(fromAndTo)
                routeSearch.setRouteSearchListener(this)
                routeSearch.calculateWalkRouteAsyn(walkRouteQuery);
                dialog.dismiss()
            }
            btnGotoNewPlaceCancel = dialogView.findViewById(R.id.btnGotoNewPlaceCancel)
            btnGotoNewPlaceCancel.setOnClickListener {
                dialog.dismiss()
            }
            tvNewPlaceName.text = it.title
            tvNewPlaceAddress.text = it.snippet
            imgNewPlace.setImageBitmap(it.options.icon.bitmap)
            dialog = AlertDialog.Builder(activity).create()
            dialog.setView(dialogView)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            true
        }
        aMap.setLocationSource(this)
        // 是否显示定位按钮
        aMap.isMyLocationEnabled = true;//显示定位层并且可以触发定位,默认是flase
        aMap.minZoomLevel = 17F
        val myLocationStyle = MyLocationStyle()
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
        myLocationStyle.showMyLocation(true)
        aMap.uiSettings.isMyLocationButtonEnabled = true
        aMap.isMyLocationEnabled = true
        myLocationStyle.strokeColor(Color.argb(50, 30, 150, 180));
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(50, 30, 150, 180));
        // 设置圆形的边框粗细
        myLocationStyle.strokeWidth(1.0f);
        // 定位、且将视角移动到地图中心点,定位点依照设备方向旋转,并且会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        //设置定位蓝点的Style
        aMap.myLocationStyle = myLocationStyle;
    }

    /***
     * 请求定位权限
     */
    @AfterPermissionGranted(REQUEST_PERMISSIONS)
    private fun requestPermission() {
        if (EasyPermissions.hasPermissions(BITApplication.context, *Companion.permissions)) {
            showMsg("已获得权限，开始定位")
            myLocationClient?.startLocation()
        } else {
            //false 无权限
            EasyPermissions.requestPermissions(
                this,
                "需要权限",
                REQUEST_PERMISSIONS,
                *Companion.permissions
            );
        }

    }


    //城市码
    lateinit var cityCode: String

    lateinit var allPlaceFragment: AllPlaceFragment

    lateinit var routeSearch: RouteSearch
    lateinit var fromAndTo: RouteSearch.FromAndTo
    lateinit var walkRouteQuery: RouteSearch.WalkRouteQuery

    lateinit var bottomLayout: RelativeLayout
    lateinit var tvRouteInfo: TextView

    lateinit var btnStudy: Button   // 展示学习场所Marker
    lateinit var btnLife: Button    // 展示生活场所Marker
    lateinit var btnEat: Button     // 展示美食场所Marker
    lateinit var btnOther: Button   // 展示其他场所Marker

    lateinit var floatingbtnShow: FloatingActionButton  // 展示所有Marker
    lateinit var floatingbtnClear: FloatingActionButton // 清除所有Marker

    lateinit var searchView: SearchView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        bottomLayout = root.findViewById(R.id.bottomLayout)
        bottomLayout.visibility = View.GONE
        tvRouteInfo = root.findViewById(R.id.tvRouteInfo)
        btnStudy = root.findViewById(R.id.btnStudy)
        btnLife = root.findViewById(R.id.btnLive)
        btnEat = root.findViewById(R.id.btnEat)
        btnOther = root.findViewById(R.id.btnOther)

        /***
         * “学在北理”按钮
         * 点击展示所有学习类型场所的Marker
         */
        btnStudy.setOnClickListener {
            if (markerLists.size > 0) {
                for (marker in markerLists)
                    if (marker.`object` != 0)
                        marker.alpha = 0F
                    else
                        marker.alpha = 1F
            }
            walkRouteOverlay?.removeFromMap()
            bottomLayout.visibility = View.GONE
        }

        /***
         * “住在北理”按钮
         * 点击展示所有生活类型场所的Marker
         */
        btnLife.setOnClickListener {
            if (markerLists.size > 0) {
                for (marker in markerLists)
                    if (marker.`object` != 1)
                        marker.alpha = 0F
                    else
                        marker.alpha = 1F
            }
            walkRouteOverlay?.removeFromMap()
            bottomLayout.visibility = View.GONE
        }

        /***
         * “吃在北理”按钮
         * 点击展示所有美食类型场所的Marker
         */
        btnEat.setOnClickListener {
            if (markerLists.size > 0) {
                for (marker in markerLists)
                    if (marker.`object` != 2)
                        marker.alpha = 0F
                    else
                        marker.alpha = 1F
            }
            walkRouteOverlay?.removeFromMap()
            bottomLayout.visibility = View.GONE
        }

        /***
         * “游在北理”按钮
         * 点击展示所有其他类型场所的Marker
         */
        btnOther.setOnClickListener {
            if (markerLists.size > 0) {
                for (marker in markerLists)
                    if (marker.`object` != 3)
                        marker.alpha = 0F
                    else
                        marker.alpha = 1F
            }
            walkRouteOverlay?.removeFromMap()
            bottomLayout.visibility = View.GONE
        }
        floatingbtnShow = root.findViewById(R.id.floatingbtnShow)
        floatingbtnClear = root.findViewById(R.id.floatingbtnClear)
        /***
         * 展示地图上所有Marker
         */
        floatingbtnShow.setOnClickListener {
            if (markerLists.size > 0) {
                for (marker in markerLists)
                    marker.alpha = 1F
            }
            bottomLayout.visibility = View.GONE
        }
        /***
         * 清除地图上所有Marker
         */
        floatingbtnClear.setOnClickListener {
            if (markerLists.size > 0) {
                for (marker in markerLists)
                    marker.alpha = 0F
            }
            walkRouteOverlay?.removeFromMap()
            bottomLayout.visibility = View.GONE
        }
        /***
         * 搜索框
         * 根据名称在已有的Marker中进行匹配
         * 搜索成功会将地图焦点移到搜索的目的地上
         * 失败则会给出提示
         */
        searchView = root.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                var searchResult: Boolean = false
                var destinationMarker: Marker? = null
                for (marker in markerLists) {
                    if (marker.title.equals(query)) {
                        searchResult = true
                        destinationMarker = marker
                        break
                    }
                }

                if (searchResult) { // 搜索成功，移动地图焦点
                    aMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                destinationMarker!!.position.latitude,
                                destinationMarker!!.position.longitude
                            ),
                            11F
                        )
                    )
                } else {  // 搜索失败，给出提示
                    showMsg("抱歉，没有找到该地点")
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        /***
         * 初始化定位和地图
         * 请求所需权限
         */
        initLocation()
        initMap(root, savedInstanceState)
        requestPermission()

        /***
         * 创建不需要显示的nearByFragment，从而利用回调获取places对象信息，将其Marker显示在地图上
         */
        allPlaceFragment = AllPlaceFragment.newInstance(null, "", "")
        allPlaceFragment.placeListener = this // 设置MapFragment为回调接口
        activity?.supportFragmentManager?.beginTransaction()?.add(allPlaceFragment, "nearByFragment")
            ?.commit()
        return root
    }

    private fun showMsg(msg: String) {
        Toast.makeText(BITApplication.context, msg, Toast.LENGTH_SHORT).show();
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        // 所需权限列表
        val permissions: Array<String> = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    /***
     * 定位启动
     */
    override fun activate(p0: LocationSource.OnLocationChangedListener?) {
        if (p0 != null) {
            myOnLocationChangedListener = p0
        }
        myLocationClient.startLocation()
    }

    /***
     * 定位结束
     */
    override fun deactivate() {
        myLocationClient.stopLocation()
        myLocationClient.onDestroy()
    }

    /***
     * 处理定位结果
     */
    override fun onLocationChanged(p0: AMapLocation?) {
        when (p0?.errorCode) {
            // 定位成功
            0 -> {
                myLocationClient.stopLocation()
                myOnLocationChangedListener?.onLocationChanged(p0)
                // 将地图焦点移到当前所在位置
                aMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(p0.latitude, p0.longitude),
                        11F
                    )
                )
                showMsg("已定位到您当前所在位置\n经度:${p0.latitude} 纬度:${p0.longitude}")
                // 获取经纬度信息
                nowPosition = LatLonPoint(p0.latitude, p0.longitude)
                cityCode = p0.cityCode
            }
            // 定位失败
            else -> {
                showMsg("抱歉，未能定位到您所在的位置\n请检查您的网络后再次进行尝试\n")
            }
        }
    }


    lateinit var mapPlaces: ArrayList<Place>    // 目前已有的地点列表
    lateinit var handler: Handler
    private val markerLists: ArrayList<Marker> = arrayListOf() // Marker列表

    /***
     * 根据地点信息读取所对应的图片
     * 创建该地点的Marker
     * 由于Glide加载图片不能在主线程中进行，而添加Marker要在UI线程中进行
     * 故采用handler多线程，在子线程中加载图片，设置Marker，然后将添加Marker的操作推送到主线程上
     */
    private fun addMarkerToMap(handler: Handler, place: Place) {
        thread {
            /***
             * 使用Glide框架加载图片
             * place的src属性为其图片的存储位置
             * 对其分割即可得到文件名
             */
            val nameList = place.src?.split('/')
            val fileName = nameList?.last()
            val newFile = File(BITApplication.context?.filesDir, fileName)
            val bitmap = Glide.with(BITApplication.context).load(newFile)
                .asBitmap()
                .into(100, 100)
                .get()
            val view = View.inflate(BITApplication.context, R.layout.circle_item, null)
            view.findViewById<CircleImageView>(R.id.photoView).setImageBitmap(bitmap)
            // 设置Marker信息和样式
            var markerOptions = MarkerOptions()
            markerOptions.icon(BitmapDescriptorFactory.fromView(view)).title(place.name)
                .snippet(place.details)
            markerOptions.isFlat = true;//设置marker平贴地图效果
            markerOptions.position(
                convertToLatLng(
                    LatLonPoint(
                        place.latitude,
                        place.longitude
                    )
                )
            )
            /***
             * 在主线程中将该地点的Marker添加到地图上
             */
            handler.post {
                val marker = aMap.addMarker(markerOptions)
                marker.`object` = place.type
                markerLists.add(marker)
            }
        }

    }

    /***
     * 回调函数获取place列表
     * MapFragment为回调接口，在NearByFragment中从数据库获取完place后调用该函数
     * 从而在MapFragment中得到place列表
     */
    override fun getPlaces(places: ArrayList<Place>) {
        handler = Handler()
        // 得到places信息
        mapPlaces = places
        for (place in mapPlaces) {
            addMarkerToMap(handler, place)
        }
    }


    var walkRouteOverlay: WalkRouteOverlay? = null  // 步行导航结果图层

    /***
     * 步行导航结果
     * 若存在到达的路线就在地图上绘制出来
     * 否则给出提示
     */
    override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {
        // 得到路线 下一步要将路线在地图上绘制出来
        when (p1) {
            // 导航成功
            1000 -> {
                if (p0?.paths != null) {
                    if (p0.paths.size > 0) {
                        val walkPath = p0.paths[0] ?: return;
                        walkRouteOverlay?.removeFromMap()
                        //绘制路线
                        walkRouteOverlay = WalkRouteOverlay(
                            BITApplication.context, aMap, walkPath,
                            p0.startPos,
                            p0.targetPos
                        );
                        walkRouteOverlay!!.removeFromMap();
                        walkRouteOverlay!!.addToMap();
                        walkRouteOverlay!!.zoomToSpan();

                        val dis = walkPath.distance;
                        val dur = walkPath.duration;
                        tvRouteInfo.text =
                            getFriendlyTime(dur.toInt()) + "(" + getFriendlyLength(dis) + ")"
                        /***
                         * 导航路线详情页面
                         * 点击后启动RouteDetailsActivity，展示导航路线详情
                         */
                        bottomLayout.visibility = View.VISIBLE
                        bottomLayout.setOnClickListener {
                            val intent =
                                Intent(BITApplication.context, RouteDetailsActivity::class.java)
                            // 传入导航路线
                            intent.putExtra("path", walkPath)
                            startActivity(intent)
                        }
                    } else if (p0.paths == null) {
                        showMsg("对不起，没有搜索到相关数据！");
                    }
                } else {
                    showMsg("对不起，没有搜索到相关数据！");
                }
            }
            // 导航失败
            else -> {
                showMsg("搜索失败，请检查相关配置")
            }
        }

    }

    /***
     * 未使用到骑行、公交和驾车导航
     * 故未实现接口中与之有关的函数
     */
    override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {

    }

    override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {

    }

    override fun onDriveRouteSearched(p0: DriveRouteResult?, p1: Int) {

    }
}