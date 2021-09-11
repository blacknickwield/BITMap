package com.gjf.life_demo.utils

import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.LatLonPoint
import com.gjf.life_demo.R
import java.text.DecimalFormat

/***
 * LatLng转换为LatLonPoint
 */
fun convertToLatLonPoint(latlon: LatLng): LatLonPoint {
    return LatLonPoint(latlon.latitude, latlon.longitude)
}

/***
 * LatLonPoint转换为LatLng
 */
fun convertToLatLng(latLonPoint: LatLonPoint?): LatLng? {
    return if (latLonPoint == null) {
        null
    } else LatLng(latLonPoint.latitude, latLonPoint.longitude)
}

fun getFriendlyTime(second: Int): String {
    if (second > 3600) {
        val hour = second / 3600;
        val minute = (second % 3600) / 60;
        return hour.toString() + "小时" + minute.toString() + "分钟";
    }
    if (second >= 60) {
        val minute = second / 60;
        return minute.toString() + "分钟";
    }
    return second.toString() + "秒";
}

fun getFriendlyLength(lenMeter: Float): String {
    if (lenMeter > 10000) // 10 km
    {
        val dis = lenMeter / 1000;
        return dis.toString() + ChString.Kilometer;
    }

    if (lenMeter > 1000) {
        val dis: Float = lenMeter.toFloat() / 1000
        val fnum: DecimalFormat = DecimalFormat("##0.0");
        val dstr: String = fnum.format(dis);
        return dstr + ChString.Kilometer;
    }

    if (lenMeter > 100) {
        val dis = lenMeter / 50 * 50;
        return dis.toString() + ChString.Meter;
    }

    var dis = lenMeter / 10 * 10;
    if (dis == 0F) {
        dis = 10F;
    }

    return dis.toString() + ChString.Meter;
}

/***
 * 在导航路线图层上根据不同action显示不同图标
 */
fun getWalkActionID(actionName: String?): Int {
    if (actionName == null || actionName == "") {
        return R.drawable.dir13
    }
    if ("左转" == actionName) {
        return R.drawable.dir2
    }
    if ("右转" == actionName) {
        return R.drawable.dir1
    }
    if ("向左前方" == actionName || "靠左" == actionName || actionName.contains("向左前方")) {
        return R.drawable.dir6
    }
    if ("向右前方" == actionName || "靠右" == actionName || actionName.contains("向右前方")) {
        return R.drawable.dir5
    }
    if ("向左后方" == actionName || actionName.contains("向左后方")) {
        return R.drawable.dir7
    }
    if ("向右后方" == actionName || actionName.contains("向右后方")) {
        return R.drawable.dir8
    }
    if ("直行" == actionName) {
        return R.drawable.dir3
    }
    if ("通过人行横道" == actionName) {
        return R.drawable.dir9
    }
    if ("通过过街天桥" == actionName) {
        return R.drawable.dir11
    }
    return if ("通过地下通道" == actionName) {
        R.drawable.dir10
    } else R.drawable.dir13
}



