package com.xuhh.brushify.ui.fragment.home.draw

import android.graphics.Path

object ArrowPath {
    //箭头path
    fun addArrowToPath(path: Path,startX:Float,startY:Float, endX: Float, endY:Float,arrowLength:Float){
        path.moveTo(startX, startY)
        path.lineTo(endX,endY)
        val angle = Math.atan2((endY - startY).toDouble(),(endX - startX).toDouble())
        val arrowX1 = endX - arrowLength * Math.cos(angle - Math.PI/6).toFloat()
        val arrowY1 = endY - arrowLength * Math.sin(angle - Math.PI/6).toFloat()

        val arrowX2 = endX - arrowLength * Math.cos(angle + Math.PI/6).toFloat()
        val arrowY2 = endY - arrowLength * Math.sin(angle + Math.PI/6).toFloat()

        path.lineTo(arrowX1,arrowY1)
        path.moveTo(endX,endY)
        path.lineTo(arrowX2,arrowY2)
    }
}