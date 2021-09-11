package com.gjf.life_demo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gjf.life_demo.data.dao.PlaceDao
import com.gjf.life_demo.data.entity.Place

//数据库中的所有表，都要在entities数组中声明
@Database(entities = [Place::class], version = 4)
abstract class PlaceDatabase : RoomDatabase() {
    //集成Dao对象实现CRUD功能
    abstract fun placeDao(): PlaceDao

    //使用“Double Check”实现Singleton模式，
    //返回唯一的一个MyDatabase实例给外界使用
    companion object {
        @Volatile
        private var dbInstance: PlaceDatabase? = null
        fun getDatabase(context: Context): PlaceDatabase {
            if (dbInstance == null) {
                synchronized(PlaceDatabase::class.java) {
                    if (dbInstance == null) {
                        dbInstance = Room.databaseBuilder(
                            context.applicationContext,
                            PlaceDatabase::class.java,
                            "mydata.db"
                        ).fallbackToDestructiveMigration().build()
                    }
                }
            }
            return dbInstance!!
        }
    }
}