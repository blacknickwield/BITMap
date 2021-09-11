package com.gjf.life_demo.data.repository

import com.gjf.life_demo.data.dao.PlaceDao
import com.gjf.life_demo.data.entity.Place

/***
 * 暴露给程序的数据库操作接口
 * 程序运行时PlaceViewModel从PlaceRepository中获取数据
 */
class PlaceRepository(private val placeDao: PlaceDao) {
    fun getAllEntities() = placeDao.allEntity
    fun addPlace(place: Place) = placeDao.insert(place)
    fun deletePlacesByIds(placeIds: List<Int>) = placeDao.deletePlacesByIds(placeIds)
}