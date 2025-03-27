package com.xuhh.brushify.ui.util

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

//dp2px
fun Context.dp2px(dp:Int): Int{
    return (resources.displayMetrics.density*dp).toInt()
}
fun Context.dp2pxF(dp:Int): Float{
    return resources.displayMetrics.density*dp
}

fun View.dp2px(dp:Int): Int{
    return (resources.displayMetrics.density*dp).toInt()
}
fun View.dp2pxF(dp:Int): Float{
    return resources.displayMetrics.density*dp
}

fun Context.toast(message:String){
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
}
fun View.toast(message:String){
    Toast.makeText(context,message,Toast.LENGTH_LONG).show()
}
fun Fragment.toast(message:String){
    Toast.makeText(context,message,Toast.LENGTH_LONG).show()
}

















