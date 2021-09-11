package com.gjf.life_demo.overlay

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.gjf.life_demo.R
import java.util.*


/**
 * 路线图层叠加
 */
open class RouteOverlay(private val mContext: Context) {
    protected var stationMarkers: MutableList<Marker>? = ArrayList()
    protected var allPolyLines: MutableList<Polyline> = ArrayList()
    protected var startMarker: Marker? = null
    protected var endMarker: Marker? = null
    protected var startPoint: LatLng? = null
    protected var endPoint: LatLng? = null
    protected var mAMap: AMap? = null
    private var startBit: Bitmap? = null
    private var endBit: Bitmap? = null
    private var busBit: Bitmap? = null
    private var walkBit: Bitmap? = null
    private var driveBit: Bitmap? = null
    protected var nodeIconVisible = true

    /**
     * 去掉BusRouteOverlay上所有的Marker。
     * @since V2.1.0
     */
    fun removeFromMap() {
        /***
         * 清除路线图层时不需要清除起点和终点
         * 只需清除起点和终点之间的路线
         * 故注释掉下述代码
         */
//        if (startMarker != null) {
//            startMarker!!.remove()
//        }
//        if (endMarker != null) {
//            endMarker!!.remove()
//        }
        for (marker in stationMarkers!!) {
            marker.remove()
        }
        for (line in allPolyLines) {
            line.remove()
        }
        destroyBit()
    }

    private fun destroyBit() {
        if (startBit != null) {
            startBit!!.recycle()
            startBit = null
        }
        if (endBit != null) {
            endBit!!.recycle()
            endBit = null
        }
        if (busBit != null) {
            busBit!!.recycle()
            busBit = null
        }
        if (walkBit != null) {
            walkBit!!.recycle()
            walkBit = null
        }
        if (driveBit != null) {
            driveBit!!.recycle()
            driveBit = null
        }
    }

    /**
     * 给起点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected val startBitmapDescriptor: BitmapDescriptor
        protected get() = BitmapDescriptorFactory.fromResource(R.drawable.amap_start)

    /**
     * 给终点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected val endBitmapDescriptor: BitmapDescriptor
        protected get() = BitmapDescriptorFactory.fromResource(R.drawable.amap_end)

    /**
     * 给公交Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected val busBitmapDescriptor: BitmapDescriptor
        protected get() = BitmapDescriptorFactory.fromResource(R.drawable.cute)

    /**
     * 给步行Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected val walkBitmapDescriptor: BitmapDescriptor
        protected get() = BitmapDescriptorFactory.fromResource(R.drawable.amap_walk)
    protected val driveBitmapDescriptor: BitmapDescriptor
        protected get() = BitmapDescriptorFactory.fromResource(R.drawable.cute)

    protected fun addStartAndEndMarker() {
        startMarker = mAMap!!.addMarker(
            MarkerOptions()
                .position(startPoint).icon(startBitmapDescriptor)
                .title("\u8D77\u70B9")
        )
        // startMarker.showInfoWindow();
        endMarker = mAMap!!.addMarker(
            MarkerOptions().position(endPoint)
                .icon(endBitmapDescriptor).title("\u7EC8\u70B9")
        )
        // mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint,
        // getShowRouteZoom()));
    }

    /**
     * 移动镜头到当前的视角。
     * @since V2.1.0
     */
    fun zoomToSpan() {
        if (startPoint != null) {
            if (mAMap == null) {
                return
            }
            try {
                val bounds = latLngBounds
                mAMap!!.animateCamera(
                    CameraUpdateFactory
                        .newLatLngBounds(bounds, 100)
                )
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    protected val latLngBounds: LatLngBounds
        protected get() {
            val b = LatLngBounds.builder()
            b.include(LatLng(startPoint!!.latitude, startPoint!!.longitude))
            b.include(LatLng(endPoint!!.latitude, endPoint!!.longitude))
            return b.build()
        }

    /**
     * 路段节点图标控制显示接口。
     * @param visible true为显示节点图标，false为不显示。
     * @since V2.3.1
     */
    fun setNodeIconVisibility(visible: Boolean) {
        try {
            nodeIconVisible = visible
            if (stationMarkers != null && stationMarkers!!.size > 0) {
                for (i in stationMarkers!!.indices) {
                    stationMarkers!![i].isVisible = visible
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    protected fun addStationMarker(options: MarkerOptions?) {
        if (options == null) {
            return
        }
        val marker = mAMap!!.addMarker(options)
        if (marker != null) {
            stationMarkers!!.add(marker)
        }
    }

    protected fun addPolyLine(options: PolylineOptions?) {
        if (options == null) {
            return
        }
        val polyline = mAMap!!.addPolyline(options)
        if (polyline != null) {
            allPolyLines.add(polyline)
        }
    }

    protected val routeWidth: Float
        protected get() = 18f
    protected val walkColor: Int
        protected get() = Color.parseColor("#6db74d")

    /**
     * 自定义路线颜色。
     * return 自定义路线颜色。
     * @since V2.2.1
     */
    protected val busColor: Int
        protected get() = Color.parseColor("#537edc")

    protected val driveColor: Int
        protected get() = Color.parseColor("#537edc")

}

