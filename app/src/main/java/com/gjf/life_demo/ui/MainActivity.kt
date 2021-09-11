package com.gjf.life_demo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.gjf.life_demo.R
import com.google.android.material.bottomnavigation.BottomNavigationView

//请求权限码
const val REQUEST_PERMISSIONS = 9527

class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 设置底部导航栏的导航图
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        val navController = findNavController(R.id.nav_host)
        bottomNavigationView.setupWithNavController(navController)
    }

}