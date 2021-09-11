package com.gjf.life_demo.overlay
import android.content.Context
import com.amap.api.maps.AMap
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.PolylineOptions
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.WalkPath
import com.amap.api.services.route.WalkStep
import com.gjf.life_demo.utils.AMapServicesUtil.convertArrList
import com.gjf.life_demo.utils.AMapServicesUtil.convertToLatLng


/**
 * 步行路线图层类。在高德地图API里，如果要显示步行路线规划，可以用此类来创建步行路线图层。如不满足需求，也可以自己创建自定义的步行路线图层。
 * @since V2.1.0
 */
class WalkRouteOverlay(
    context: Context?, amap: AMap, path: WalkPath,
    start: LatLonPoint?, end: LatLonPoint?
) : RouteOverlay(
    context!!
) {
    private var mPolylineOptions: PolylineOptions? = null
    private var walkStationDescriptor: BitmapDescriptor? = null
    private val walkPath: WalkPath

    /**
     * 添加步行路线到地图中。
     * @since V2.1.0
     */
    fun addToMap() {
        initPolylineOptions()
        try {
            val walkPaths = walkPath.steps
            for (i in walkPaths.indices) {
                val walkStep = walkPaths[i]
                val latLng = convertToLatLng(
                    walkStep
                        .polyline[0]
                )
                addWalkStationMarkers(walkStep, latLng)
                addWalkPolyLines(walkStep)
            }
            /***
             * 起点和终点使用原有Marker
             * 不需要新添加
             * 故注释掉下述代码
             */
            //addStartAndEndMarker()
            showPolyline()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * 检查这一步的最后一点和下一步的起始点之间是否存在空隙
     */
    private fun checkDistanceToNextStep(
        walkStep: WalkStep,
        walkStep1: WalkStep
    ) {
        val lastPoint = getLastWalkPoint(walkStep)
        val nextFirstPoint = getFirstWalkPoint(walkStep1)
        if (lastPoint != nextFirstPoint) {
            addWalkPolyLine(lastPoint, nextFirstPoint)
        }
    }

    /**
     * @param walkStep
     * @return
     */
    private fun getLastWalkPoint(walkStep: WalkStep): LatLonPoint {
        return walkStep.polyline[walkStep.polyline.size - 1]
    }

    /**
     * @param walkStep
     * @return
     */
    private fun getFirstWalkPoint(walkStep: WalkStep): LatLonPoint {
        return walkStep.polyline[0]
    }

    private fun addWalkPolyLine(pointFrom: LatLonPoint, pointTo: LatLonPoint) {
        addWalkPolyLine(convertToLatLng(pointFrom), convertToLatLng(pointTo))
    }

    private fun addWalkPolyLine(latLngFrom: LatLng, latLngTo: LatLng) {
        mPolylineOptions!!.add(latLngFrom, latLngTo)
    }

    /**
     * @param walkStep
     */
    private fun addWalkPolyLines(walkStep: WalkStep) {
        mPolylineOptions!!.addAll(convertArrList(walkStep.polyline))
    }

    /**
     * @param walkStep
     * @param position
     */
    private fun addWalkStationMarkers(walkStep: WalkStep, position: LatLng) {
        addStationMarker(
            MarkerOptions()
                .position(position)
                .title(
                    "\u65B9\u5411:" + walkStep.action
                            + "\n\u9053\u8DEF:" + walkStep.road
                )
                .snippet(walkStep.instruction).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(walkStationDescriptor)
        )
    }

    /**
     * 初始化线段属性
     */
    private fun initPolylineOptions() {
        if (walkStationDescriptor == null) {
            walkStationDescriptor = walkBitmapDescriptor
        }
        mPolylineOptions = null
        mPolylineOptions = PolylineOptions()
        mPolylineOptions!!.color(walkColor).width(routeWidth)
    }

    private fun showPolyline() {
        addPolyLine(mPolylineOptions)
    }

    /**
     * 通过此构造函数创建步行路线图层。
     * @param context 当前activity。
     * @param amap 地图对象。
     * @param path 步行路线规划的一个方案。详见搜索服务模块的路径查询包（com.amap.api.services.route）中的类 **[WalkStep](../../../../../../Search/com/amap/api/services/route/WalkStep.html)**。
     * @param start 起点。详见搜索服务模块的核心基础包（com.amap.api.services.core）中的类**[LatLonPoint](../../../../../../Search/com/amap/api/services/core/LatLonPoint.html)**。
     * @param end 终点。详见搜索服务模块的核心基础包（com.amap.api.services.core）中的类**[LatLonPoint](../../../../../../Search/com/amap/api/services/core/LatLonPoint.html)**。
     * @since V2.1.0
     */
    init {
        mAMap = amap
        walkPath = path
        startPoint = convertToLatLng((start)!!)
        endPoint = convertToLatLng((end)!!)
    }
}

