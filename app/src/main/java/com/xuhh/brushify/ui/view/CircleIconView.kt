package com.xuhh.brushify.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import com.xuhh.brushify.R
import com.xuhh.brushify.model.IconModel
import com.xuhh.brushify.ui.util.toast
import io.github.florent37.shapeofview.shapes.CircleView

class CircleIconView(
    context: Context,
    attrs: AttributeSet? = null,
): CircleView(context,attrs) {
    private var mIconTextView: IconTextView
    var clickCallback:(IconTextView)->Unit = {}
        set(value) {
            field = value
            mIconTextView.clickCallback = value
        }

    init {
        mIconTextView = IconTextView(context,attrs)
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        lp.gravity = Gravity.CENTER
        addView(mIconTextView,lp)
        //解析自定义属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CircleIconView)
        val textSize = ta.getDimension(R.styleable.CircleIconView_icon_size_sp,20f)
        ta.recycle()

        setIconSize(textSize)
    }

    fun setIconModel(model: IconModel){
        mIconTextView.setIconModel(model)
    }

    fun setIconSize(size: Float){
        mIconTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,size)
    }
}