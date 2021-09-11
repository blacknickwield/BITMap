package com.gjf.life_demo.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class BITApplication: Application() {
    // 获取全局上下文context
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}