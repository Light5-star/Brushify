package com.xuhh.brushify.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.xuhh.brushify.ui.fragment.home.draw.ArrowPath
import com.xuhh.brushify.ui.fragment.home.draw.BaseShape
import com.xuhh.brushify.ui.util.dp2px
import com.xuhh.brushify.viewModel.HomeViewModel

class LocationShape: BaseShape(){
    private val mArrowLength = HomeViewModel.instance().getContext().dp2px(10).toFloat()

    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        mPaint.style = Paint.Style.STROKE
    }

    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        //清空路径
        mPath.reset()
        //x
        ArrowPath.addArrowToPath(
            mPath,
            rectF.left,
            (rectF.top + rectF.bottom)/2,
            rectF.right,
            (rectF.top + rectF.bottom)/2,
            mArrowLength
        )

        ArrowPath.addArrowToPath(
            mPath,
            (rectF.left + rectF.right)/2,
            rectF.bottom,
            (rectF.left + rectF.right)/2,
            rectF.top,
            mArrowLength
        )
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(mPath,mPaint)
        super.draw(canvas)
    }
}