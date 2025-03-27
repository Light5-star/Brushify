package com.xuhh.brushify.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Region
import com.xuhh.brushify.ui.fragment.home.draw.BaseShape

class TriangleShape: BaseShape(){

    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
    }

    override fun fillColor() {
        super.fillColor()
        mPaint.style = Paint.Style.FILL
    }

    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        if (mMovePosition == MovePosition.CENTER){
            mPath.offset(mMoveDx,mMoveDy)
        }else {
            //清空路径
            mPath.reset()
            //起点
            mPath.moveTo(rectF.left,rectF.bottom)
            mPath.lineTo(rectF.right,rectF.bottom)
            mPath.lineTo((rectF.left+rectF.right)/2,rectF.top)
            mPath.close()
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(mPath,mPaint)
        super.draw(canvas)
    }
}