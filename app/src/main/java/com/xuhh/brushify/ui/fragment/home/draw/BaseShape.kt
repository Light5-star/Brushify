package com.xuhh.brushify.ui.fragment.home.draw

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.util.Log
import com.xuhh.brushify.R
import com.xuhh.brushify.ui.fragment.home.view.ShapeState
import com.xuhh.brushify.ui.util.dp2pxF
import com.xuhh.brushify.ui.util.toast
import com.xuhh.brushify.viewModel.HomeViewModel

/**
 * 绘制图形的抽象类
 */
abstract class BaseShape {
    protected var startX:Float = 0f
    protected var startY:Float = 0f
    protected var endX:Float = 0f
    protected var endY:Float = 0f
    protected var mShapeState: ShapeState = ShapeState.NORMAL
    protected var mIsInMoveMode = false //是否处于移动模式
    protected val mPath = Path()

    var centerX:Float = 0f
    var centerY:Float = 0f
    var rectF:RectF = RectF()
    //图形画笔
    val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = HomeViewModel.instance().mStrokeWidth
        color = HomeViewModel.instance().mColor
        style = HomeViewModel.instance().mStrokeStyle
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }
    //边框画笔
    val mOutlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = HomeViewModel.instance().getContext().dp2pxF(2)
        color = Color.parseColor("#6375FE")
        style = Paint.Style.STROKE
    }
    protected val mCornerRectBitmap:Bitmap by lazy{
        BitmapFactory.decodeResource(
            HomeViewModel.instance().getContext().resources,
            R.drawable.scale_corner
        )
    }
    //展示范围
    protected val mCornerSize: Float by lazy {
        HomeViewModel.instance().getContext().dp2pxF(13) /2
    }

    //响应范围
    private val mReactSize: Float by lazy{
        HomeViewModel.instance().getContext().dp2pxF(16)
    }

    protected var mMovePosition = MovePosition.NONE
    protected var mMoveStartX = 0f
    protected var mMoveStartY = 0f
    protected var mMoveDx = 0f
    protected var mMoveDy = 0f

    open fun fillColor(){
        mPaint.color = HomeViewModel.instance().mColor
    }

    fun select(){
        mShapeState = ShapeState.SELECT
    }

    fun unSelect(){
        mShapeState = ShapeState.NORMAL
        mMovePosition = MovePosition.NONE
    }

    open fun calculateMovePosition(x: Float,y: Float){
        mMoveStartX = x
        mMoveStartY = y
        if (x in rectF.left - mReactSize .. rectF.left + mReactSize && y in rectF.top - mReactSize .. rectF.top + mReactSize){
            mMovePosition = MovePosition.TOP_LEFT
        }else if (x in rectF.right - mReactSize .. rectF.right + mReactSize && y in rectF.top - mReactSize .. rectF.top + mReactSize){
            mMovePosition = MovePosition.TOP_RIGHT
        }else if (x in rectF.left - mReactSize .. rectF.left + mReactSize && y in rectF.bottom - mReactSize .. rectF.bottom + mReactSize){
            mMovePosition = MovePosition.BOTTOM_LEFT
        }else if (x in rectF.right - mReactSize .. rectF.right + mReactSize && y in rectF.bottom - mReactSize .. rectF.bottom + mReactSize){
            mMovePosition = MovePosition.BOTTOM_RIGHT
        }else if (x in rectF.left - mReactSize .. rectF.left + mReactSize){
            mMovePosition = MovePosition.LEFT
        }else if (x in rectF.right - mReactSize .. rectF.right + mReactSize){
            mMovePosition = MovePosition.RIGHT
        }else if (y in rectF.top - mReactSize .. rectF.top + mReactSize){
            mMovePosition = MovePosition.TOP
        }else if (y in rectF.bottom - mReactSize .. rectF.bottom + mReactSize){
            mMovePosition = MovePosition.BOTTOM
        }else if (rectF.contains(x,y)){
            mMovePosition = MovePosition.CENTER
        }else {
            mMovePosition = MovePosition.NONE
        }
    }

    fun updateShapeState(state: ShapeState) {
        mShapeState = state
    }

    //起始点和终点
    open fun setStartPoint(x:Float,y:Float){
        startX = x
        startY = y
        //保存颜色
        mPaint.color = HomeViewModel.instance().mColor
        mPaint.strokeWidth = HomeViewModel.instance().mStrokeWidth
        mPaint.style = HomeViewModel.instance().mStrokeStyle
    }
    open fun setEndPoint(x:Float,y:Float){
        when (mMovePosition){
            MovePosition.NONE ->{
                //是不是在Move
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

                rectF.offset(mMoveDx,mMoveDy)
            }
            MovePosition.LEFT ->{
                moveLeft(x,y)
            }
            MovePosition.RIGHT ->{
                moveRight(x,y)
            }
            MovePosition.TOP ->{
                moveTop(x,y)
            }
            MovePosition.BOTTOM ->{
                moveBottom(x,y)
            }
            MovePosition.TOP_LEFT ->{
                moveLeft(x,y)
                moveTop(x,y)
            }
            MovePosition.TOP_RIGHT ->{
                moveRight(x,y)
                moveTop(x,y)
            }
            MovePosition.BOTTOM_LEFT ->{
                moveLeft(x,y)
                moveBottom(x,y)
            }
            MovePosition.BOTTOM_RIGHT ->{
                moveRight(x,y)
                moveBottom(x,y)
            }
            else -> {}
        }
        centerX = (startX + endX) / 2
        centerY = (startY + endY) / 2
    }


    open fun draw(canvas: Canvas){
        if (mShapeState == ShapeState.SELECT){
            canvas.drawRect(rectF,mOutlinePaint)
            canvas.drawBitmap(
                mCornerRectBitmap,
                rectF.left-mCornerSize,
                rectF.top-mCornerSize,
                null
            )
            canvas.drawBitmap(
                mCornerRectBitmap,
                rectF.right-mCornerSize,
                rectF.top-mCornerSize,
                null
            )
            canvas.drawBitmap(
                mCornerRectBitmap,
                rectF.left-mCornerSize,
                rectF.bottom-mCornerSize,
                null
            )
            canvas.drawBitmap(
                mCornerRectBitmap,
                rectF.right-mCornerSize,
                rectF.bottom-mCornerSize,
                null
            )
        }
    }
    open fun containsPointInPath(x:Float, y:Float):Boolean{
        val pathRegion = Region()
        val clipRegion = Region(
            rectF.left.toInt(),
            rectF.top.toInt(),
            rectF.right.toInt(),
            rectF.bottom.toInt()
        )
        pathRegion.setPath(mPath,clipRegion)
        return pathRegion.contains(x.toInt(),y.toInt())
    }

    open fun containsPointInRect(x:Float, y:Float):Boolean{
        val outRectF = RectF().apply {
            left = rectF.left - mCornerSize
            top = rectF.top - mCornerSize
            right = rectF.right + mCornerSize
            bottom = rectF.bottom + mCornerSize
        }
        return outRectF.contains(x,y)
    }

    enum class MovePosition {
        NONE,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        LEFT,
        TOP,
        RIGHT,
        BOTTOM,
        CENTER
    }

    private fun moveLeft(x: Float, y:Float){
        //计算当前移动的距离
        mMoveDx = x - mMoveStartX
        //判断起点和终点的大小关系
        if (startX < endX){ //从左往右
            startX += mMoveDx
        }else{  //从右往左
            endX += mMoveDx
        }
        //当前这个点就是下一个点的起点
        mMoveStartX = x
        //矩形区域也跟着偏移
        rectF.left += mMoveDx
    }
    private fun moveTop(x: Float, y:Float){
        //计算当前移动的距离
        mMoveDy = y - mMoveStartY
        //判断起点和终点的大小关系
        if (startY < endY){ //从上到下
            startY += mMoveDy
        }else{  //从下到上
            endY += mMoveDy
        }
        //当前这个点就是下一个点的起点
        mMoveStartY = y
        //矩形区域也跟着偏移
        rectF.top += mMoveDy
    }
    private fun moveRight(x: Float, y:Float){
        //计算当前移动的距离
        mMoveDx = x - mMoveStartX
        //判断起点和终点的大小关系
        if (startX < endX){ //从左往右
            endX += mMoveDx
        }else{  //从右往左
            startX += mMoveDx
        }
        //当前这个点就是下一个点的起点
        mMoveStartX = x
        //矩形区域也跟着偏移
        rectF.right += mMoveDx
    }
    private fun moveBottom(x: Float, y:Float){
        //计算当前移动的距离
        mMoveDy = y - mMoveStartY
        //判断起点和终点的大小关系
        if (startY < endY){ //从上到下
            endY += mMoveDy
        }else{  //从下到上
            startY += mMoveDy
        }
        //当前这个点就是下一个点的起点
        mMoveStartY = y
        //矩形区域也跟着偏移
        rectF.bottom += mMoveDy
    }

    fun updateMoveMode(isInMoveMode: Boolean) {
        mIsInMoveMode = isInMoveMode
    }

}