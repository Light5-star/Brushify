package com.xuhh.brushify.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.xuhh.brushify.ui.fragment.home.draw.BaseShape

class CurveShape: BaseShape(){

    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        rectF.left = x
        rectF.top = y
        rectF.right = x
        rectF.bottom = y
        mPath.moveTo(x,y)
        mPaint.style = Paint.Style.STROKE
    }

    override fun setEndPoint(x: Float, y: Float) {
        when (mMovePosition) {
            MovePosition.NONE -> {
                //是不是在Move
                if (mIsInMoveMode) return
                rectF.left = Math.min(rectF.left,x)
                rectF.top = Math.min(rectF.top,y)
                rectF.right = Math.max(rectF.right,x)
                rectF.bottom = Math.max(rectF.bottom,y)
            }

            MovePosition.CENTER -> {
                mMoveDx = x - mMoveStartX
                mMoveDy = y - mMoveStartY

                rectF.offset(mMoveDx, mMoveDy)
                mPath.offset(mMoveDx, mMoveDy)

                mMoveStartX = x
                mMoveStartY = y
            }
            else -> {}

        }

        if (mIsInMoveMode) return
        mPath.lineTo(x,y)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(mPath,mPaint)
        super.draw(canvas)
    }
}