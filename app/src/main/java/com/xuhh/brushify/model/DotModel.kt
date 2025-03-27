package com.xuhh.brushify.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import androidx.annotation.DrawableRes
import com.xuhh.brushify.R
import com.xuhh.brushify.ui.util.DotState
import java.lang.ref.WeakReference

data class DotModel(
    val num:Int,
    val cx:Float,
    val cy:Float,
    val radius:Float,
    val context:WeakReference<Context>,
    var state: DotState = DotState.NORMAL,
    @DrawableRes val noramlRes:Int = R.drawable.dot_normal_selected,
    @DrawableRes val errorRes:Int = R.drawable.dot_error_selected
){
    val normalBitmap:Bitmap by lazy {
        BitmapFactory.decodeResource(context.get()?.resources, noramlRes)
    }
    val errorBitmap:Bitmap by lazy {
        BitmapFactory.decodeResource(context.get()?.resources, errorRes)
    }
    val rectF = RectF(cx-radius,cy-radius,cx+radius,cy+radius)
    //判断是否包含一个点
    fun containPoint(x:Float, y:Float):Boolean{
        return rectF.contains(x,y)
    }
}