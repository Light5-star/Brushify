package com.xuhh.brushify.ui.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtil {
    fun getTimeName():String{
        val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA)
        return formatter.format(Date())+".png"
    }
}