package com.gjf.life_demo.viewmodels

import androidx.lifecycle.ViewModel
import com.gjf.life_demo.data.repository.PlaceRepository
import com.gjf.life_demo.data.entity.Place

class PlaceViewModel(private val placeRepository: PlaceRepository) : ViewModel() {

    // 展示所有place的RecyclerView的数据源
    var places: ArrayList<Place> =
        if (placeRepository.getAllEntities().value == null) arrayListOf()
        else placeRepository.getAllEntities().value as ArrayList<Place>
    // 展示学习类型place的RecyclerView的数据源
    var studyPlaces: ArrayList<Place> = arrayListOf()
    // 展示生活类型place的RecyclerView的数据源
    var lifeplaces: ArrayList<Place> = arrayListOf()
    // 展示美食类型place的RecyclerView的数据源
    var eatPlaces: ArrayList<Place> = arrayListOf()
    // 展示其他类型place的RecyclerView的数据源
    var otherPlaces: ArrayList<Place> = arrayListOf()

    // 暴露给NearByFragment进行数据库操作的方法
    fun getAllPlaces() = placeRepository.getAllEntities()
    fun addPlace(place: Place) = placeRepository.addPlace(place)
    fun deletePlacesByIds(placeIds: List<Int>) = placeRepository.deletePlacesByIds(placeIds)
}