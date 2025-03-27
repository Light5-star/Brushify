package com.xuhh.brushify.ui.fragment.home.view.strocksize

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.xuhh.brushify.databinding.StrokeBarViewLayoutBinding
import com.xuhh.brushify.ui.util.dp2pxF
import com.xuhh.brushify.viewModel.HomeViewModel

class StrockBarView(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context,attrs) {
    private var mBinding: StrokeBarViewLayoutBinding? = null
    private val mDistance: Float by lazy {
        (mBinding!!.dotFill.top - mBinding!!.dotEmpty.top).toFloat()
    }
    private val mDownAnimation: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(mBinding?.indicatorView, "translationY",mDistance).apply {
            duration = 200
        }
    }
    private val mUpAnimation: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(mBinding?.indicatorView, "translationY",0f).apply {
            duration = 200
        }
    }
    private var mIsEmptyStyle = true

    init {
        val inflater = LayoutInflater.from(context)
        mBinding = StrokeBarViewLayoutBinding.inflate(inflater)
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        addView(mBinding!!.root,lp)

        mBinding!!.barView.addProgressChangeListener = { progress ->
            mBinding!!.tvSize.text = "$progress"
            HomeViewModel.instance().mStrokeWidth = dp2pxF(progress)
        }

        mBinding!!.dotEmpty.setOnClickListener {
            if (!mUpAnimation.isRunning && !mIsEmptyStyle){
                mUpAnimation.start()
                mIsEmptyStyle = true
                HomeViewModel.instance().mStrokeStyle = Paint.Style.STROKE
            }
        }

        mBinding!!.dotFill.setOnClickListener {
            if (!mDownAnimation.isRunning && mIsEmptyStyle) {
                mDownAnimation.start()
                mIsEmptyStyle = false
                HomeViewModel.instance().mStrokeStyle = Paint.Style.FILL
            }
        }
        mBinding!!.barView.addTouchStateListener = { isTouch ->
            if (isTouch){
                mBinding!!.tvSize.visibility = View.VISIBLE
            }else{
                mBinding!!.tvSize.visibility = View.INVISIBLE
            }
        }
    }
}