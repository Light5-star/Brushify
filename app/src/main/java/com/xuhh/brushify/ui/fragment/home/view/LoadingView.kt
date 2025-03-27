package com.xuhh.brushify.ui.fragment.home.view

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.app.ActionBar.LayoutParams
import com.bumptech.glide.Glide
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.xuhh.brushify.R
import com.xuhh.brushify.databinding.LayerItemLayoutBinding
import com.xuhh.brushify.databinding.LoadingLayoutBinding
import com.xuhh.brushify.databinding.PickImagePopupViewLayoutBinding
import com.xuhh.brushify.ui.fragment.home.layer.LayerState

class LoadingView(val context: Context) {
    private var mBinding: LoadingLayoutBinding? = null
    var addImageSelectListener: (Int) -> Unit = {}
    private val popupWindow: PopupWindow by lazy {
        val inflater = LayoutInflater.from(context)
        mBinding = LoadingLayoutBinding.inflate(inflater)
        PopupWindow(context).apply {
            contentView = mBinding!!.root
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun show(parent: View){
        popupWindow.showAtLocation(parent,Gravity.CENTER,0,0)
    }
    fun hide(onAnimatorEnd:()->Unit = {}){
        mBinding!!.loadingView.visibility = View.INVISIBLE
        mBinding!!.finishView.visibility = View.VISIBLE
        mBinding!!.finishView.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                popupWindow.dismiss()
                onAnimatorEnd()
                mBinding!!.loadingView.visibility = View.VISIBLE
                mBinding!!.finishView.visibility = View.INVISIBLE
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

    }

}