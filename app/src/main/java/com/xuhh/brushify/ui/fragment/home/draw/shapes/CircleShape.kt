package com.xuhh.brushify.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.xuhh.brushify.ui.fragment.home.draw.BaseShape

class CircleShape: BaseShape(){
    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        mPath.reset()
        mPath.addOval(rectF, Path.Direction.CW)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(mPath,mPaint)
        super.draw(canvas)
    }

    override fun fillColor() {
        super.fillColor()
        mPaint.style = Paint.Style.FILL
    }
}