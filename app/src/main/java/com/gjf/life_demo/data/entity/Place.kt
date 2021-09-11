package com.gjf.life_demo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class Place(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,       // 地点名称
    val details: String,    // 地点地址
    val type: Int,  // 地点类型
    val src: String,        // 地点相关图片的存储位置
    val latitude: Double,   // 地点纬度
    val longitude: Double,  // 地点经度
    var isSelected: Boolean // 表示地点是否被选中(供显示时使用，在数据库中存储的一律为false)
)