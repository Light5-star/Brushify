package com.xuhh.brushify.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.xuhh.brushify.ui.fragment.home.draw.BaseShape
import com.xuhh.brushify.ui.fragment.home.view.ShapeState
import com.xuhh.brushify.ui.util.toast
import com.xuhh.brushify.viewModel.HomeViewModel

class RectangleShape: BaseShape(){
    override fun draw(canvas: Canvas) {
        canvas.drawRect(rectF,mPaint)
        super.draw(canvas)
    }

    override fun fillColor() {
        super.fillColor()
        mPaint.style = Paint.Style.FILL
    }

    override fun containsPointInPath(x: Float, y: Float): Boolean {
        return rectF.contains(x,y)
    }
}