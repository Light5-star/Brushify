package com.xuhh.brushify.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.xuhh.brushify.ui.fragment.home.draw.BaseShape

class BezelShape: BaseShape(){
    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        mPaint.style = Paint.Style.STROKE
    }

    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        mPath.reset()
        //起始点
        mPath.moveTo(startX,(startY + endY)/2)
        //
        val space = Math.abs(startX-endX)*0.382f
        val sx = Math.min(startX,endX)
        val ex = Math.max(startX,endX)
        val height = Math.abs(startY-endY)
        mPath.cubicTo(
            sx + space,
            Math.min(startY,endY)-height,
            ex - space,
            Math.max(startY,endY)+height,
            Math.max(startX,endX),
            (startY + endY)/2
        )
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(mPath,mPaint)
        super.draw(canvas)
    }
}