package com.gjf.life_demo.listener

/***
 * PlaceFragment实现该接口
 * 在NearByFragment中改变编辑状态时回调该接口的函数
 */
interface NotifyTextChangeListener {
    fun textVisibilityChange(visibility: Int)
    fun textContentChange(contents: String)
    fun dataSetChange()
}