package com.gjf.life_demo.listener

import com.gjf.life_demo.viewmodels.PlaceViewModel

/***
 * PlaceFragment实现该接口
 * 数据库中place数据发生变化后，在NearByFragment中回调该接口中的函数
 * 更新TypeFragment中的place信息
 */
interface PlaceViewModelListener {
    fun getPlaceViewModel(placeViewModel: PlaceViewModel)
}