package com.xuhh.brushify.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.xuhh.brushify.model.IconModel
import com.xuhh.brushify.ui.util.IconState
import com.xuhh.brushify.ui.util.OperationType
import com.xuhh.brushify.ui.util.dp2px
import com.xuhh.brushify.ui.util.toast

/**
 * 横向或者纵向的图标菜单
 */
class IconMenuView(
    context: Context,
    attrs: AttributeSet? = null,
) :LinearLayout(context, attrs) {
    private var iconList:List<IconModel> = emptyList()
    private val defaultWidth = dp2px(40)
    private val defaultHeight = dp2px(40)
    private var mWidth = defaultWidth
    private var mHeight = defaultHeight

    private var mCurrentSelectedView:IconTextView? = null
    var iconClickListener:(OperationType,IconState) ->Unit = {_,_ ->}


    init {
    }

    //设置模型
    fun setIcons(icons: List<IconModel>){
        iconList = icons
        weightSum = icons.size.toFloat()
        gravity = Gravity.CENTER

        icons.forEach { model ->
            val circleIconView = CircleIconView(context)
            circleIconView.setIconModel(model)
            circleIconView.clickCallback = { iconTextView ->
                dealWithCallBack(iconTextView)
            }
            val lp = LayoutParams(mWidth, mHeight)
            lp.weight = 1f
            if (orientation == VERTICAL) {
                lp.topMargin = dp2px(5)
            }else{
                lp.leftMargin = dp2px(5)
            }
            addView(circleIconView,lp)
        }
    }

    private fun dealWithCallBack(iconTextView: IconTextView){
        if (mCurrentSelectedView == null){
            //选中
            iconTextView.updateIconState(IconState.SELECTED)
            mCurrentSelectedView = iconTextView
            iconClickListener(iconTextView.mIconModel!!.type,IconState.SELECTED)
        }else{
            if (mCurrentSelectedView != iconTextView){
                mCurrentSelectedView!!.updateIconState(IconState.NORMAL)
                iconTextView.updateIconState(IconState.SELECTED)
                mCurrentSelectedView = iconTextView
                iconClickListener(iconTextView.mIconModel!!.type,IconState.SELECTED)
            }else{
                //是同一个 取消之前的选中状态
                iconTextView.updateIconState(IconState.NORMAL)
                mCurrentSelectedView!!.updateIconState(IconState.NORMAL)
                mCurrentSelectedView = null
                iconClickListener(iconTextView.mIconModel!!.type, IconState.NORMAL)
            }
        }
    }

    fun resetIconState(){
        if(mCurrentSelectedView != null){
            mCurrentSelectedView!!.updateIconState(IconState.NORMAL)
            mCurrentSelectedView = null
        }
    }
}