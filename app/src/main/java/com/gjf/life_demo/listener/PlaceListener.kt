package com.gjf.life_demo.listener

import com.gjf.life_demo.data.entity.Place

/***
 * MapFragment实现该接口
 * NearByFragment中获取完place列表后回调该接口的函数，在MapFragment中获取place列表
 */
interface PlaceListener {
    fun getPlaces(places: ArrayList<Place>)
}