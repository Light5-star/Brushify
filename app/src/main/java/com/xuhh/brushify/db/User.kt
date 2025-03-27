package com.xuhh.brushify.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    @ColumnInfo(name = "pin_password")
    val pinPassword: String,
    @ColumnInfo(name = "pic_password")
    val picPassword: String,
    var isLogin:Boolean = false,
    var loginDate:Date = Date(), //登录时间
    val validate:Long = 60*60*1000, //有效时间1个小时
    var passwordType:Int = 0 //登录类型 0:PIN  1:PIC
):Parcelable