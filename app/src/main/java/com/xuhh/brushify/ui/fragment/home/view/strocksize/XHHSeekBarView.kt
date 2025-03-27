package com.xuhh.brushify.ui.fragment.home.view.strocksize

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.xuhh.brushify.R
import com.xuhh.brushify.ui.util.dp2pxF

class XHHSeekBarView (
    context: Context,
    attrs: AttributeSet? = null,
) : View(context,attrs) {

    private var mMin: Int = 0
    private var mMax: Int = 0
    var addProgressChangeListener: (Int) -> Unit = {}
    private var mProgress: Int = 0
        set(value) {
            field = value
            mTextWidth = mTextPaint.measureText("$value")
            addProgressChangeListener(value)
        }
    private var mOrientation = Orientation.VERTICAL
    private var mProgressBarWidth = dp2pxF(10)
    private var mDotSize = dp2pxF(30)
    private var mDefaultHeight = dp2pxF(180)
    private var mProgressBackgroundColor = Color.parseColor("#D1D1D1")
    private var mProgressColor = Color.parseColor("#6375FE")
    private var mDotColor = Color.WHITE
    private var mPadding = dp2pxF(1)
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val mBgRect = RectF()
    private val mProgressRect = RectF()
    private var mCx = 0f
    private var mCy = 0f

    var addTouchStateListener: (Boolean) -> Unit = {}

    private val mTextPaint = TextPaint().apply {
        color = Color.BLACK
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14f,resources.displayMetrics)
    }

    private var mOffset = 0f
    private var mTextWidth = 0f

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.XHHSeekBarView)
        mMin = typedArray.getInteger(R.styleable.XHHSeekBarView_min,1)
        mMax = typedArray.getInteger(R.styleable.XHHSeekBarView_max,50)
        mProgress = typedArray.getInteger(R.styleable.XHHSeekBarView_progress,10)
        val value = typedArray.getInteger(R.styleable.XHHSeekBarView_orientation,0)
        mOrientation = if (value == 0) Orientation.VERTICAL else Orientation.HORIZONTAL
        typedArray.recycle()

        mProgress = if (mProgress < mMin) mMin else if (mProgress > mMax) mMax else mProgress
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        if (widthMode != MeasureSpec.EXACTLY){
            if (mOrientation == Orientation.VERTICAL) {
                widthSize = (mDotSize + mPadding * 2).toInt()
            }else{
                widthSize = mDefaultHeight.toInt()
            }
        }

        var heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode != MeasureSpec.EXACTLY){
            if (mOrientation == Orientation.VERTICAL) {
                heightSize = mDefaultHeight.toInt()
            }else{
                heightSize = (mDotSize + mPadding * 2).toInt()
            }
        }

        setMeasuredDimension(widthSize,heightSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //确定矩形区域
        if (mOrientation == Orientation.VERTICAL) {
            val hspace = (measuredWidth - mProgressBarWidth )/2
            mBgRect.apply {
                left = hspace
                top = 0f
                right = measuredWidth - hspace
                bottom = measuredHeight.toFloat()
            }
            mProgressRect.apply {
                left = hspace
                top = 0f
                right = measuredWidth - hspace
                bottom = mProgress / (mMax - mMin)  * measuredHeight.toFloat()
            }
            //圆圈坐标
            mCx = measuredWidth /2f
            mCy = mProgressRect.bottom
        }else{
            val vspace = (measuredHeight - mProgressBarWidth )/2
            mBgRect.apply {
                left = 0f
                top = vspace
                right = measuredWidth.toFloat()
                bottom = measuredHeight - vspace
            }
            mProgressRect.apply {
                left = 0f
                top = vspace
                right = mProgress.toFloat() / (mMax-mMin) * measuredWidth
                bottom = measuredHeight - vspace
            }
            //圆圈坐标
            mCx = mProgressRect.right
            mCy = measuredHeight /2f
        }

        val fontMetrics = mTextPaint.fontMetrics
        mOffset = (fontMetrics.descent - fontMetrics.ascent)/2 - fontMetrics.descent
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.color = mProgressBackgroundColor
        //背景
        canvas.drawRoundRect(
            mBgRect,
            mProgressBarWidth/2,
            mProgressBarWidth/2,
            mPaint
        )
        mPaint.color = mProgressColor
        canvas.drawRoundRect(
            mProgressRect,
            mProgressBarWidth/2,
            mProgressBarWidth/2,
            mPaint
        )
        if (mOrientation == Orientation.VERTICAL){
            if (mProgressRect.bottom <= mDotSize/2){
                mCy = mDotSize/2
            }else if (mProgressRect.bottom >= height - mDotSize/2){
                mCy = height - mDotSize/2
            }else{
                mCy = mProgressRect.bottom
            }
        }else{
            if (mProgressRect.right <= mDotSize/2){
                mCx = mDotSize/2
            }else if (mProgressRect.right >= width - mDotSize/2){
                mCx = width - mDotSize/2
            }else{
                mCx = mProgressRect.right
            }
        }

        mPaint.color = mDotColor
        canvas.drawCircle(mCx, mCy, mDotSize/2f, mPaint)
        canvas.drawText("${mProgress}",mCx - mTextWidth/2,mCy + mOffset,mTextPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_MOVE ->{
                if (mOrientation == Orientation.VERTICAL){
                    if (event.y in 0f.. measuredHeight.toFloat()) {
                        mProgressRect.bottom = event.y
                        mProgress = ((event.y / measuredHeight) * (mMax - mMin)).toInt() + 1
                        addTouchStateListener(true)
                        invalidate()
                    }
                }else{
                    if (event.x in 0f.. measuredWidth.toFloat()) {
                        mProgressRect.right = event.x
                        mProgress = ((event.x / measuredWidth) * (mMax - mMin)).toInt() + 1
                        addTouchStateListener(true)
                        invalidate()
                    }
                }

            }
            MotionEvent.ACTION_UP ->{
                addTouchStateListener(false)
            }
        }
        return true
    }

    enum class Orientation {
        HORIZONTAL, VERTICAL
    }
}