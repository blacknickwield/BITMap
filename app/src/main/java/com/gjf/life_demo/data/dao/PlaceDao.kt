package com.gjf.life_demo.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gjf.life_demo.data.entity.Place

@Dao
interface PlaceDao {
    //如果插入时发生冲突，则“后来者居上”
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: Place)

    @Delete
    fun delete(entity: Place)
    @Update
    fun update(entity: Place)

    @Query("DELETE FROM places WHERE id in (:placeIds)")
    fun deletePlacesByIds(placeIds: List<Int>)
    //提取函数参数为SQL命令中的查询参数
    @Query("SELECT * FROM places WHERE name=:name")
    fun getPlaces(name: String):List<Place>

    @Query("SELECT * FROM places WHERE name=:type")
    fun getPlaces(type: Int):List<Place>

    @Query("SELECT * FROM places")
    fun getAllPlaces(): List<Place>

    @Query("SELECT * FROM places where id = (SELECT MAX(id) FROM places)")
    fun getNewAddedPlace(): Place

    @Query("SELECT COUNT(*) FROM places")
    fun getNumOfPlaces(): Int
    //当数据表有变化时，自动更新LiveData实例
    @get:Query("SELECT * FROM places")
    val allEntity: LiveData<List<Place>>
}