package com.xuhh.brushify.ui.util

import androidx.room.TypeConverter
import java.util.Date

/**
 * database 类型转换器
 * 特殊类型无法写入数据库
 * 数据库只支持基本类型 int float string long double boolean Blob
 *
 * 如果需要将特殊类型写入数据库 必须对这个类型进行转化，转化为数据库支持的类型
 */
class TypeConverter {
    //Date - String
    @TypeConverter
    fun dateToLong(date: Date):Long{
        return date.time
    }
    @TypeConverter
    fun longToDate(time: Long): Date{
        return Date(time)
    }
}
