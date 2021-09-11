package com.gjf.life_demo.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.LocationSource
import com.amap.api.services.core.LatLonPoint
import com.gjf.life_demo.R
import com.gjf.life_demo.adapter.PlaceAdapter
import com.gjf.life_demo.app.BITApplication
import com.gjf.life_demo.data.repository.PlaceRepository
import com.gjf.life_demo.data.database.PlaceDatabase
import com.gjf.life_demo.data.entity.Place
import com.gjf.life_demo.listener.FragmentCallBackListener
import com.gjf.life_demo.listener.NotifyTextChangeListener
import com.gjf.life_demo.listener.PlaceListener
import com.gjf.life_demo.listener.PlaceViewModelListener
import com.gjf.life_demo.utils.RecyclerViewAnimation
import com.gjf.life_demo.utils.SpacingItemDecoration
import com.gjf.life_demo.viewmodels.PlaceViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val STATE_DEFAULT = 0 //默认状态
private const val STATE_EDIT = 1 //编辑状态


// 请求相机
const val REQUEST_IMAGE_CAPTURE = 1

/***
 * NearByFragment：展示所有地点的页面
 * AMapLocationListener, LocationSource: 地图定位接口，定位后回调相关函数处理定位结果
 * FragmentCallBackListener：编辑按钮接口，用户点击编辑按钮后在PlaceFragment中回调该函数，改变item的外观
 */
class AllPlaceFragment(notifyListener: NotifyTextChangeListener?) : Fragment(), AMapLocationListener,
    LocationSource, FragmentCallBackListener {
    private var param1: String? = null
    private var param2: String? = null
    var placeListener: PlaceListener? = null
    var notifyTextChangeListener: NotifyTextChangeListener? = notifyListener
    private var editMode = STATE_DEFAULT
    private var editorStatus = false //是否为编辑状态
    lateinit var placeViewModel: PlaceViewModel

    lateinit var placeRecyclerView: RecyclerView
    lateinit var floatingActionButton: FloatingActionButton
    lateinit var layBottom: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    /**
     * 用于第一次使用时预先录入数据的函数
     */
    lateinit var handler: Handler
    private fun createSamples(
        db: PlaceDatabase,
        imageId: Int,
        name: String,
        address: String,
        type: Int,
        latitude: Double,
        longitude: Double
    ) {
        thread {
            val fileName = name + System.currentTimeMillis().toString() + ".jpg"
            val saveFile = File(activity?.filesDir, fileName)
            // 图片保存
            var saveSrc: String = ""
            try {
                val bitmap: Bitmap = BitmapFactory.decodeResource(resources, imageId)
                val saveImgOut = FileOutputStream(saveFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, saveImgOut)
                saveImgOut.flush()
                saveImgOut.close()
                saveSrc = activity?.filesDir.toString() + "/" + fileName
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //将信息存入数据库
            db.placeDao().insert(Place(0, name, address, type, saveSrc, latitude, longitude, false))
        }

    }

    /***
     * 数据库初始化
     */
    private fun initDB() {
        val db = PlaceDatabase.getDatabase(BITApplication.context)
        val prefs = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
        val isFirstUse: Boolean = prefs.getBoolean("isFirstUse", true)
        // 第一次使用则预先加入一些图片
        if (isFirstUse) {
            handler = Handler()
            createSamples(
                db,
                R.drawable.jingyuan,
                "静园",
                "北校区最西侧的一排楼",
                1,
                39.733691,
                116.167982
            )
            createSamples(
                db,
                R.drawable.nanshitang,
                "南食堂",
                "南校区南部靠近南操场",
                2,
                39.727183,
                116.167971
            )
            createSamples(
                db,
                R.drawable.nancaochang,
                "南操场",
                "南校区中部的足球草坪和跑道",
                3,
                39.729505,
                116.16923
            )
            createSamples(
                db,
                R.drawable.lijiaolou,
                "理科教学楼",
                "南校区一进门东部",
                0,
                39.730172,
                116.171354
            )
            createSamples(
                db,
                R.drawable.beiliqiao,
                "北理桥",
                "连接南北校区，在两校区之间",
                3,
                39.731109,
                116.169193
            )
            createSamples(
                db,
                R.drawable.zongjiaolou,
                "综合教学楼",
                "北校区中部东侧",
                0,
                39.733269,
                116.170961
            )
            createSamples(
                db,
                R.drawable.muqiao,
                "北湖木桥",
                "北校区北湖上的木桥",
                3,
                39.734912,
                116.169907
            )
            // 之后就不为第一次启动，不会再预先添加
            val editor = prefs.edit()
            editor.putBoolean("isFirstUse", false)
            editor.apply()
        }

        placeViewModel = PlaceViewModel(PlaceRepository(db.placeDao()))
        /***
         * 监听数据库中的数据变化，做到实时同步
         */
        placeViewModel.getAllPlaces().observe(viewLifecycleOwner, Observer {
            // 将原来的数据清空
            if (placeViewModel.places.isNotEmpty()) {
                placeViewModel.places.clear()
            }
            if (placeViewModel.studyPlaces.isNotEmpty()) {
                placeViewModel.studyPlaces.clear()
            }
            if (placeViewModel.lifeplaces.isNotEmpty()) {
                placeViewModel.lifeplaces.clear()
            }
            if (placeViewModel.eatPlaces.isNotEmpty()) {
                placeViewModel.eatPlaces.clear()
            }
            if (placeViewModel.otherPlaces.isNotEmpty()) {
                placeViewModel.otherPlaces.clear()
            }
            if (it.isNotEmpty()) {
                for (item in it) {
                    placeViewModel.places.add(item)
                    when (item.type) {
                        0 -> placeViewModel.studyPlaces.add(item)
                        1 -> placeViewModel.lifeplaces.add(item)
                        2 -> placeViewModel.eatPlaces.add(item)
                        3 -> placeViewModel.otherPlaces.add(item)
                    }
                }

            }

            /***
             * 回调各TypeFragment中的函数
             * 通知其更新数据
             */
            notifyTextChangeListener?.dataSetChange()

            /***
             * 在此设立到MapFragment的回调
             * 在MapFragment中获取到place列表
             */
            placeListener?.getPlaces(placeViewModel.places)

            /***
             * 数据库中数据发生变化后要通知RecyclerView
             * 若没有place了则要隐藏编辑按钮
             */
            activity?.runOnUiThread {
                placeAdapter.notifyDataSetChanged()
                RecyclerViewAnimation.runLayoutAnimation(placeRecyclerView)
                if (placeViewModel.places.isEmpty())
                    notifyTextChangeListener?.textVisibilityChange(View.GONE)
                else
                    notifyTextChangeListener?.textVisibilityChange(View.VISIBLE)
            }
        })
    }

    private lateinit var placeAdapter: PlaceAdapter
    private lateinit var btnNewPlaceConfirm: Button
    private lateinit var btnNewPlaceCancel: Button
    private lateinit var edtNewPlaceName: EditText
    private lateinit var edtNewPlaceAddress: EditText
    private lateinit var spinner: Spinner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_all_place, container, false)
        floatingActionButton = root.findViewById(R.id.floatingbtnClear)
        placeRecyclerView = root.findViewById(R.id.placeRecyclerView)
        placeRecyclerView.layoutManager = LinearLayoutManager(activity)
        tvCheckAll = root.findViewById(R.id.tv_check_all)
        tvDelete = root.findViewById(R.id.tv_delete)
        layBottom = root.findViewById(R.id.lay_bottom)
        return root
    }

    private lateinit var imageBitmap: Bitmap
    private lateinit var checkBox: CheckBox
    private lateinit var tvHintInfo: TextView
    private var allCheckedHasBeenChecked = false

    private var hasPhoto = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initDB()

        /***
         * 回调PlaceFra中的getPlaceViewModel函数
         * 将PlaceViewModel返回到PlaceFragment中
         * 作为各TypePlaceFragment的数据源
         */
        if (notifyTextChangeListener != null)
            (notifyTextChangeListener as PlaceViewModelListener).getPlaceViewModel(placeViewModel)

        /***
         * 对所有place进行全选或全不选
         */
        tvCheckAll.setOnClickListener {
            if (placeAdapter.count == placeViewModel.places.size)
                setAllItemsUnChecked()
            else
                setAllItemsChecked()
            allCheckedHasBeenChecked = !allCheckedHasBeenChecked
        }

        /***
         * 删除所选place
         */
        tvDelete.setOnClickListener {
            deleteCheckedItems()
        }


        placeAdapter = PlaceAdapter(placeViewModel.places, this)
        placeAdapter.tvDelete = tvDelete
        placeRecyclerView.adapter = placeAdapter
        RecyclerViewAnimation.runLayoutAnimation(placeRecyclerView)
        // 修饰器
        val topSpacingDecorator = SpacingItemDecoration(30)
        placeRecyclerView.addItemDecoration(topSpacingDecorator)

        /***
         * 点击悬浮按钮弹出添加地点的弹窗页面
         */
        floatingActionButton.setOnClickListener {
            val dialogView = View.inflate(activity, R.layout.dialog_newplace, null)
            dialog = AlertDialog.Builder(activity).create()
            dialog.setView(dialogView)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            tvHintInfo = dialogView.findViewById(R.id.tvHintInfo)
            tvHintInfo.text = "当前还未拍照\n点击图片即可拍摄"
            imgNewPlace = dialogView.findViewById(R.id.imgNewPlace)
            edtNewPlaceName = dialogView.findViewById(R.id.edtNewPlaceName)
            edtNewPlaceAddress = dialogView.findViewById(R.id.edtNewPlaceAddress)
            edtInputLatitude = dialogView.findViewById(R.id.edtInputLatitude)
            edtInputLongitude = dialogView.findViewById(R.id.edtInputLongitude)
            checkBox = dialogView.findViewById(R.id.checkBox)
            spinner = dialogView.findViewById(R.id.spinner)
            imgNewPlace.setImageResource(R.drawable.ic_upload_picture)

            /***
             * 点击图片调用相机进行图片拍摄
             */
            imgNewPlace.setOnClickListener {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    activity?.let { it1 ->
                        takePictureIntent.resolveActivity(it1.packageManager)?.also {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
                }
            }

            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                // 选中
                if (isChecked) {
                    //开始定位
                    initLocation()
                } else {
                    edtInputLatitude.setText("")
                    edtInputLatitude.isEnabled = true
                    edtInputLongitude.setText("")
                    edtInputLongitude.isEnabled = true
                    buttonView.text = "使用当前位置信息"
                }
            }

            /***
             * 根据用户在下拉框中的选择设置地点类型
             */
            var newPlaceType = 0
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    newPlaceType = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    newPlaceType = 3
                }
            }

            /***
             * 点击确定按钮对地点信息进行保存
             */
            btnNewPlaceConfirm = dialogView.findViewById(R.id.btnGotoNewPlaceConfirm)
            btnNewPlaceConfirm.setOnClickListener {
                val newPlaceName = edtNewPlaceName.text.toString()
                val newPlaceAddress = edtNewPlaceAddress.text.toString()
                /***
                 * 如果用户有未填项则给出提示
                 * 否则则将地点信息存入数据库
                 */
                if (newPlaceName.isEmpty() || newPlaceAddress.isEmpty() || edtInputLatitude.text.toString()
                        .isEmpty() || edtInputLongitude.text.toString().isEmpty() || !hasPhoto
                ) {
                    Toast.makeText(BITApplication.context, "您有一些信息未填哦\n请再仔细检查一下", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val newPlaceLatitude: Double = edtInputLatitude.text.toString().toDouble()
                val newPlaceLongitude: Double = edtInputLongitude.text.toString().toDouble()
                // 将信息存入数据库
                thread {
                    placeViewModel.addPlace(
                        Place(
                            0,
                            newPlaceName,
                            newPlaceAddress,
                            newPlaceType,
                            bitmapSrc,
                            newPlaceLatitude,
                            newPlaceLongitude,
                            false
                        )
                    )

                }
                dialog.dismiss()
            }

            btnNewPlaceCancel = dialogView.findViewById(R.id.btnGotoNewPlaceCancel)
            btnNewPlaceCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private lateinit var dialog: AlertDialog
    private lateinit var imgNewPlace: ImageView
    private lateinit var bitmapSrc: String

    /***
     * 用户完成拍摄后回调该函数
     * 将用户拍摄的图片保存，同时保存图片的保存地址
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            hasPhoto = true
            imageBitmap = data?.extras?.get("data") as Bitmap
            imgNewPlace.setImageBitmap(imageBitmap)
            tvHintInfo.text = "图片已经拍好啦\n不满意还可再拍哦"
            val fileName = System.currentTimeMillis().toString() + ".jpg"
            val saveFile = File(activity?.filesDir, fileName)
            // 图片保存
            try {
                val saveImgOut = FileOutputStream(saveFile)
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, saveImgOut)
                saveImgOut.flush()
                saveImgOut.close()
                bitmapSrc = activity?.filesDir.toString() + "/" + fileName
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NearByFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(notifyListener: NotifyTextChangeListener?, param1: String, param2: String) =
            AllPlaceFragment(notifyListener).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        // 用户可选的地点类型列表
        val spinnerLists: Array<String> =
            BITApplication.context.resources.getStringArray(R.array.place_types)
    }

    private lateinit var tvCheckAll: TextView
    private lateinit var tvDelete: TextView

    /***
     * 改变编辑状态
     */
    fun updateEditState() {
        editMode =
            if (editMode == STATE_DEFAULT) STATE_EDIT else STATE_DEFAULT
        if (editMode == STATE_EDIT) {
            notifyTextChangeListener?.textContentChange("取消")
            layBottom.visibility = View.VISIBLE
            editorStatus = true
            placeAdapter.editorStatus = true
        } else {
            notifyTextChangeListener?.textContentChange("编辑")
            layBottom.visibility = View.GONE
            editorStatus = false
            placeAdapter.editorStatus = false
            setAllItemsUnChecked() //取消全选
        }
        placeAdapter.editMode = editMode
        placeAdapter.notifyDataSetChanged()
    }

    /***
     * 设置全部place为选中状态
     */
    private fun setAllItemsChecked() {
        for (item in placeViewModel.places)
            item.isSelected = true
        placeAdapter.notifyDataSetChanged()
        placeAdapter.count = placeViewModel.places.size
        tvDelete.text = "删除:${placeAdapter.count}"
    }

    /***
     * 取消所有place的选中状态
     */
    private fun setAllItemsUnChecked() {
        for (item in placeViewModel.places)
            item.isSelected = false
        placeAdapter.notifyDataSetChanged()
        placeAdapter.count = 0
        tvDelete.text = "删除"
    }

    /***
     * 删除选中的place
     */
    private fun deleteCheckedItems() {
        if (placeAdapter == null)
            return
        val deleteLists: ArrayList<Int> = arrayListOf()
        for (item in placeViewModel.places)
            if (item.isSelected)
                deleteLists.add(item.id)
        thread {
            placeViewModel.deletePlacesByIds(deleteLists)
            // 计数清零
            placeAdapter.count = 0
            activity?.runOnUiThread {
                updateEditState()
                // 若没有place了则取消编辑模式
                if (placeViewModel.places.size == 0) {
                    notifyTextChangeListener?.textVisibilityChange(View.GONE)
                }
            }

        }
    }

    // 初始化定位
    private fun initLocation() {
        // 避免重复初始化
        if (!this::myLocationClient.isInitialized) {
            myLocationClient = AMapLocationClient(BITApplication.context)
            myLocationClient.setLocationListener(this)
            myLocationOption = AMapLocationClientOption()
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            myLocationOption.locationMode =
                AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;
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
        // 开始定位
        myLocationClient.startLocation()
    }

    private lateinit var edtInputLatitude: EditText // 输入纬度
    private lateinit var edtInputLongitude: EditText    // 输入经度
    private lateinit var myLocationClient: AMapLocationClient   // 定位
    private lateinit var myLocationOption: AMapLocationClientOption // 定位模式
    private lateinit var myOnLocationChangedListener: LocationSource.OnLocationChangedListener
    private lateinit var nowPosition: LatLonPoint   //当前位置


    override fun onLocationChanged(p0: AMapLocation?) {
        when (p0?.errorCode) {
            // 定位成功
            0 -> {
                //停止定位
                myLocationClient.stopLocation()
                nowPosition = LatLonPoint(p0.latitude, p0.longitude)
                edtInputLatitude.setText(nowPosition.latitude.toString())
                edtInputLatitude.isEnabled = false
                edtInputLongitude.setText(nowPosition.longitude.toString())
                edtInputLongitude.isEnabled = false
                checkBox.text = "您已使用当前位置信息"
            }
            // 定位失败
            else -> {
                Toast.makeText(requireActivity(), "定位失败，请检查网络后再次进行尝试", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun activate(p0: LocationSource.OnLocationChangedListener?) {
        if (p0 != null) {
            myOnLocationChangedListener = p0
        }
        myLocationClient.startLocation()
    }

    override fun deactivate() {
        myLocationClient.stopLocation()
        myLocationClient.onDestroy()
    }

    override fun onClickListener() {
        updateEditState()
    }
}