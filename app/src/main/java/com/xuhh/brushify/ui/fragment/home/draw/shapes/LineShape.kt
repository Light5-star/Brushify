package com.xuhh.brushify.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.RectF
import com.xuhh.brushify.ui.fragment.home.draw.BaseShape
import com.xuhh.brushify.ui.fragment.home.view.ShapeState
import com.xuhh.brushify.viewModel.HomeViewModel

class LineShape: BaseShape(){
    private var leftIsStart = true

    override fun calculateMovePosition(x: Float, y: Float) {
        mMoveStartX = x
        mMoveStartY = y
        leftIsStart = startX < endX

        var leftX = 0f
        var rightX =0f
        var leftY = 0f
        var rightY =0f

        if (startX < endX){
            leftX = startX
            leftY = startY
            rightX = endX
            rightY = endY
        }else{
            leftX = endX
            leftY = endY
            rightX = startX
            rightY = startY
        }


        if (x in leftX - mCornerSize .. leftX + mCornerSize && y in leftY - mCornerSize .. leftY + mCornerSize){
            mMovePosition = MovePosition.LEFT
        }else if (x in rightX - mCornerSize .. rightX + mCornerSize && y in rightY - mCornerSize .. rightY + mCornerSize){
            mMovePosition = MovePosition.RIGHT
        }else{
            mMovePosition = MovePosition.CENTER
        }

    }

    override fun setEndPoint(x: Float, y: Float) {
        when (mMovePosition) {
            MovePosition.NONE -> {
                if (mIsInMoveMode) return

                endX = x
                endY = y
                rectF.left = startX.coerceAtMost(endX)
                rectF.top = startY.coerceAtMost(endY)
                rectF.right = startX.coerceAtLeast(endX)
                rectF.bottom = startY.coerceAtLeast(endY)
            }

            MovePosition.CENTER -> {
                mMoveDx = x - mMoveStartX
                mMoveDy = y - mMoveStartY
                startX += mMoveDx
                startY += mMoveDy
                endX += mMoveDx
                endY += mMoveDy
                mMoveStartX = x
                mMoveStartY = y
            }
            MovePosition.LEFT -> {
                if (leftIsStart){
                    startX = x
                    startY = y
                }else{
                    endX = x
                    endY = y
                }
            }
            MovePosition.RIGHT -> {
                if (leftIsStart){
                    endX = x
                    endY = y
                }else{
                    startX = x
                    startY = y
                }
            }
            else -> {}
        }
        mPath.reset()
        mPath.moveTo(startX,startY)
        mPath.lineTo(endX,endY)
    }

    override fun draw(canvas: Canvas) {
        if (mShapeState == ShapeState.SELECT){
            canvas.drawBitmap(
                mCornerRectBitmap,
                startX-mCornerSize,
                startY-mCornerSize,
                null
            )
            canvas.drawBitmap(
                mCornerRectBitmap,
                endX-mCornerSize,
                endY-mCornerSize,
                null
            )
        }
        canvas.drawPath(mPath,mPaint)
    }

    override fun containsPointInPath(x: Float, y: Float): Boolean {
        val tolerance = mPaint.strokeWidth
        val d1 = distance(startX,startY,x,y)
        val d2 = distance(endX,endY,x,y)
        val lineLen = distance(startX,startY,endX,endY)
        return Math.abs(d1 + d2 - lineLen) < tolerance
    }

    override fun containsPointInRect(x:Float, y:Float):Boolean{
        val tolerance = mPaint.strokeWidth
        val d1 = distance(startX,startY,x,y)
        val d2 = distance(endX,endY,x,y)
        val lineLen = distance(startX,startY,endX,endY)
        return Math.abs(d1 + d2 - lineLen) < tolerance
    }

    private fun distance(x1: Float,y1: Float, x2: Float, y2: Float): Float {
        return Math.sqrt(((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)).toDouble()).toFloat()
    }
}