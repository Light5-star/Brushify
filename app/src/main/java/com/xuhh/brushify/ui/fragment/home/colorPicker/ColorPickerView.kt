package com.xuhh.brushify.ui.fragment.home.colorPicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.xuhh.brushify.ui.util.dp2px
import com.xuhh.brushify.ui.util.dp2pxF

class ColorPickerView(
    context: Context,
    attrs: AttributeSet? = null
): View(context, attrs) {
    private var defaultWidth = dp2px(200)
    private var defaultHeight = dp2px(200)
    private var centerX = 0f
    private var centerY = 0f
    private var mColorPickerRadius = 0f
    private var mColorPickerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }
    private lateinit var mSweepGradient: SweepGradient

    private var mTouchX = 0f
    private var mTouchY = 0f
    private var mSelectRadius = dp2pxF(15)
    private var mSelectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        setShadowLayer(15f,0f,0f,Color.BLACK)
    }

    //色相
    private var mHue = 0f
    //饱和度
    private var mSaturation = 1f
    //明度
    private var mLightness = 1f

    private var mSelectedColor: Int = Color.BLACK
    private var mCallback: (Int) -> Unit = {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        val widthmode = MeasureSpec.getMode(widthMeasureSpec)
        if (widthmode != MeasureSpec.EXACTLY) {
            width = defaultWidth
        }
        var height = MeasureSpec.getSize(heightMeasureSpec)
        val heightmode = MeasureSpec.getMode(heightMeasureSpec)
        if (heightmode != MeasureSpec.EXACTLY) {
            height = defaultHeight
        }
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = measuredWidth / 2f
        centerY = measuredHeight / 2f
        mColorPickerRadius = Math.min(measuredWidth,measuredHeight) / 2f
        mSweepGradient = SweepGradient(
            centerX,
            centerY,
            intArrayOf(
                0xFFFF0000.toInt(),
                0xFFFFFF00.toInt(),
                0xFF00FF00.toInt(),
                0xFF00FFFF.toInt(),
                0xFF0000FF.toInt(),
                0xFFFF00FF.toInt(),
                0xFFFF0000.toInt()
            ),
            floatArrayOf(
                1f/6 * 0,
                1f/6 * 1,
                1f/6 * 2,
                1f/6 * 3,
                1f/6 * 4,
                1f/6 * 5,
                1.0f
            )
        )
        mColorPickerPaint.shader = mSweepGradient

        mTouchX = centerX
        mTouchY = centerY
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(centerX,centerY,mColorPickerRadius,mColorPickerPaint)
        //将HSV转化成颜色
        mSelectedColor = Color.HSVToColor(floatArrayOf(mHue,mSaturation,mLightness))
        mCallback(mSelectedColor)

        mSelectPaint.color = mSelectedColor
        canvas.drawCircle(mTouchX,mTouchY,mSelectRadius,mSelectPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_MOVE -> {
                mTouchX = event.x
                mTouchY = event.y

                if (isInPickerView(mTouchX,mTouchY)) {
                    val radians = Math.atan2(
                        (mTouchY - centerY).toDouble(),
                        (mTouchX - centerX).toDouble()
                    )
                    var degree = Math.toDegrees(radians).toFloat()
                    if (degree < 0) {
                        degree = 360 - Math.abs(degree)
                    }
                    mHue = degree

                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                mCallback(mSelectedColor)
            }

        }
        return true
    }

    fun getCurrentColor(): Int{
        return mSelectedColor
    }

    fun addPickColorListener(listener: (Int) -> Unit){
        mCallback = listener
    }

    //
    fun setSaturation(value: Float){
        mSaturation = value
        invalidate()
    }

    fun setLightness(value: Float){
        mLightness = value
        invalidate()
    }

    private fun isInPickerView(x: Float, y: Float): Boolean{
        val a = Math.abs(x - centerX)
        val b = Math.abs(y - centerY)
        val c = Math.sqrt(a * a + b * b.toDouble()).toFloat()
        return c <= mColorPickerRadius - mSelectRadius
    }
}