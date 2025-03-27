package com.xuhh.brushify.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.xuhh.brushify.model.IconModel
import com.xuhh.brushify.ui.util.IconState

class IconTextView(
    context: Context,
    attrs :AttributeSet? = null
) :AppCompatTextView(context,attrs){
    var mIconModel:IconModel? = null
    var clickCallback:(IconTextView)->Unit = {}

    init{
        typeface = Typeface.createFromAsset(context.assets,"iconfont.ttf")
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)
        //设置切换
        setOnClickListener{
            clickCallback(this)
        }

    }

    // 设置图标
    fun setIconModel(model: IconModel){
        mIconModel = model
        text = resources.getString(model.iconString)
        setBackgroundColor(resources.getColor(model.normalColor,null))
    }

    fun updateIconState(state:IconState){
        if(state == IconState.NORMAL){
            setBackgroundColor(resources.getColor(mIconModel?.normalColor?:0,null))
        }else{
            setBackgroundColor(resources.getColor(mIconModel?.selectedColor?:0,null))
        }
        mIconModel?.state = state
    }
}