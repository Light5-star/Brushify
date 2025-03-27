package com.xuhh.brushify.ui.fragment.home.draw.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.text.TextPaint
import android.util.Log
import android.util.TypedValue
import com.xuhh.brushify.R
import com.xuhh.brushify.ui.fragment.home.draw.BaseShape
import com.xuhh.brushify.ui.fragment.home.view.ShapeState
import com.xuhh.brushify.ui.util.dp2pxF
import com.xuhh.brushify.viewModel.HomeViewModel

class TextShape: BaseShape(){
    private val mBorderPath = Path()
    private val mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = HomeViewModel.instance().getContext().dp2pxF(1)
        color = HomeViewModel.instance().getContext().resources.getColor(R.color.light_blue,null)
        style = Paint.Style.STROKE
    }

    private val mTextPaint = TextPaint().apply {
        color = HomeViewModel.instance().mColor
        textSize = HomeViewModel.instance().mTextSize
    }

    private var oneLineHeight = 0f

    private var mText:String = ""

    private var cy = 0f
    //文本
    private var mTextLines = listOf<String>()
    private fun changedBorderSize(){
        //换行
        mTextLines = mText.split("\n")
        if (mTextLines.isEmpty()) return

        var maxWidth = 0f
        mTextLines.forEach { line ->
            val w = mTextPaint.measureText(line)
            maxWidth = Math.max(maxWidth,w)
        }

        val metrics = mTextPaint.fontMetrics
        oneLineHeight = metrics.bottom - metrics.top
        val height = oneLineHeight * mTextLines.size

        //矩形区域
        val space = (rectF.height() - height)/2
        rectF.top = rectF.top + space - mPadding
        rectF.right = rectF.left + maxWidth + mPadding*2
        rectF.bottom = rectF.top + height + 2 * mPadding

        mBorderPath.reset()
        mBorderPath.addRect(rectF, Path.Direction.CW)

        //坐标
        val offsety = (metrics.descent - metrics.ascent)/2 - metrics.descent
        cy = rectF.top + mPadding + oneLineHeight/2 + offsety
    }

    private val mPadding = HomeViewModel.instance().getContext().dp2pxF(5)

    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        rectF.top = y
        rectF.left = x
        rectF.right = x
        rectF.bottom = y
    }

    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        mBorderPath.reset()
        mBorderPath.addRect(rectF, Path.Direction.CW)
    }

    fun updateText(text: String){
        mText = text
        changedBorderSize()
    }

    override fun draw(canvas: Canvas) {
        if (mShapeState == ShapeState.DRAWING){
            canvas.drawPath(mBorderPath, mBorderPaint)
        }
        mTextPaint.color = HomeViewModel.instance().mColor
        mTextLines.forEachIndexed { index,line ->
            canvas.drawText(line,rectF.left+mPadding,cy+index * oneLineHeight,mTextPaint)
        }
        super.draw(canvas)
    }

    override fun containsPointInPath(x: Float, y: Float): Boolean {
        return rectF.contains(x,y)
    }
}