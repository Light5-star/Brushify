package com.xuhh.brushify.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import com.xuhh.brushify.ui.fragment.home.draw.BaseShape
import com.xuhh.brushify.ui.fragment.home.view.ShapeState
import com.xuhh.brushify.ui.util.dp2pxF
import com.xuhh.brushify.viewModel.HomeViewModel

class EraserCurveShape: BaseShape(){
    private val mEraserCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#80ADADAD")
        style = Paint.Style.FILL
    }

    private var mCircleCenterX = 0f
    private var mCircleCenterY = 0f

    private var mEraserSize = HomeViewModel.instance().getContext().dp2pxF(12)

    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        mCircleCenterX = x
        mCircleCenterY = y
        //混合模式
        mPath.moveTo(x,y)
        mPaint.style = Paint.Style.STROKE
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        mPaint.strokeWidth = HomeViewModel.instance().getContext().dp2pxF(10)
    }

    override fun setEndPoint(x: Float, y: Float) {
        //是不是在Move
        if (mIsInMoveMode) return
        endX = x
        endY = y
        rectF.left = startX.coerceAtMost(endX)
        rectF.top = startY.coerceAtMost(endY)
        rectF.right = startX.coerceAtLeast(endX)
        rectF.bottom = startY.coerceAtLeast(endY)
        mPath.lineTo(x,y)
        mCircleCenterX = x
        mCircleCenterY = y
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(mPath,mPaint)
        //绘制圆
        if (mShapeState == ShapeState.DRAWING) {
            canvas.drawCircle(mCircleCenterX, mCircleCenterY, mEraserSize / 2f, mEraserCirclePaint)
        }
    }
}