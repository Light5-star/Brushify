package com.xuhh.brushify.application

import android.app.Application
import com.xuhh.brushify.ui.fragment.home.file.FileManager

/**
 * 配置程序所需的Context
 */
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        FileManager.init(this)
    }

}