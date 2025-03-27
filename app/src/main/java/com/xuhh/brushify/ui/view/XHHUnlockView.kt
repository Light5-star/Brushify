package com.xuhh.brushify.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.xuhh.brushify.R
import com.xuhh.brushify.model.DotModel
import com.xuhh.brushify.ui.util.DotState
import com.xuhh.brushify.ui.util.delayTask
import com.xuhh.brushify.ui.util.dp2px
import java.lang.ref.WeakReference

class XHHUnlockView: View {
    //默认尺寸
    private val mDefaultSize:Int by lazy {
       dp2px(300)
    }
    //绘制原点的矩形区域尺寸
    private var mSquareSize = 0
    //半径
    private var mRadius = 0f
    //间距
    private var mSpace = 0f
    //第一个点的圆心
    private var cx = 0f
    private var cy = 0f
    //保存9个点的模型数据
    private val mDotModels = arrayListOf<DotModel>()
    //绘制背景原点的画笔
    private val bgCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#5CCCCCCC") //255*0.36 = 92  -> 5C
    }
    private val mDotPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    //记录上一个被选中的点
    private var mLastSelectedDot: DotModel? = null
    //移动时画线的path
    private var mMovePath = Path()
    //两点之间画线的path
    private var mLinePath = Path()
    //画线的画笔
    private var mMovePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = context.getColor(R.color.blue)
        strokeWidth = dp2px(3).toFloat()
    }
    //记录密码
    private var mPasswordBuilder = StringBuilder()

    //回调接口
    private var mCallBack:(String)->Boolean = {true}

    constructor(context: Context):super(context){}
    constructor(context: Context, attrs:AttributeSet?):super(context,attrs){}

    //测量自身的尺寸
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        if (widthMode != MeasureSpec.EXACTLY){
            widthSize = mDefaultSize
        }

        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if(heightMode != MeasureSpec.EXACTLY){
            heightSize = mDefaultSize
        }

        setMeasuredDimension(widthSize,heightSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //以最小边作为去矩形的尺寸
        mSquareSize = Math.min(measuredWidth,measuredHeight)
        //确定半径
        mRadius = mSquareSize / 10f
        mSpace = mRadius
        //确定第一个圆的中心点坐标
        cx = (measuredWidth-mSquareSize)/2 + mSpace + mRadius
        cy = (measuredHeight-mSquareSize)/2 + mSpace + mRadius
        //创建九个点模型数据
        initNineDotModels()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawNineDot(canvas)
        drawMovePath(canvas)
        drawLinePath(canvas)
        drawSelectedDot(canvas)
    }

    //外部通过这个方法设置监听我的数据回调
    fun addPicPathFinishedListener(listener:(String)->Boolean){
        mCallBack = listener
    }

    private fun drawLinePath(canvas: Canvas) {
        if(mLinePath.isEmpty) return
        canvas.drawPath(mLinePath, mMovePaint)
    }

    //绘制移动时的路径
    private fun drawMovePath(canvas: Canvas) {
        if(mMovePath.isEmpty) return
        canvas.drawPath(mMovePath, mMovePaint)
    }

    //创建九个点模型数据
    private fun initNineDotModels(){
        var number = 1
        for (i in 0..2){ //确定行
            for (j in 0..2) {//确定列
                val x = cx + j * (2*mRadius + mSpace)
                val y = cy + i * (2*mRadius + mSpace)
                val model = DotModel(number,x,y,mRadius, WeakReference(context))
                number++

                mDotModels.add(model)
            }
        }
    }
    private fun drawNineDot(canvas: Canvas){
        for (i in 0..2){ //确定行
            for (j in 0..2) {//确定列
                canvas.drawCircle(
                    cx + j * (2*mRadius + mSpace),
                    cy + i * (2*mRadius + mSpace),
                    mRadius,
                    bgCirclePaint
                )
            }
        }
    }
    private fun drawSelectedDot(canvas: Canvas){
        mDotModels.forEach {
            if (it.state == DotState.SELECTED){
                canvas.drawBitmap(it.normalBitmap,null,it.rectF,mDotPaint)
            }else if (it.state == DotState.ERROR){
                canvas.drawBitmap(it.errorBitmap,null,it.rectF,mDotPaint)
            }
        }
    }
    //监听触摸事件
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN ->{touchEvent(event.x,event.y)}
            MotionEvent.ACTION_MOVE ->{touchEvent(event.x,event.y)}
            MotionEvent.ACTION_UP ->{ handleResult() }
        }
        return true
    }

    //处理触摸事件
    private fun touchEvent(x:Float, y:Float){
        //判断触摸点是不是在某一个圆点内部
        mDotModels.forEach { dot ->
            if (dot.containPoint(x,y)){
                if (dot.state == DotState.NORMAL){
                    dot.state = DotState.SELECTED
                    mLastSelectedDot = dot //记录最后一个选中的点
                    mPasswordBuilder.append(dot.num) //记录当前点的编号
                    invalidate() //通知界面绘制

                    if (mLinePath.isEmpty){
                        //这个点就是起点
                        mLinePath.moveTo(dot.cx,dot.cy)
                    }else{
                        //路径中的一个点
                        mLinePath.lineTo(dot.cx,dot.cy)
                    }
                }
            }else{ //path -> moveTo  -> lineTo -> drawPath
                //触摸在圆点的外部
                if (mLastSelectedDot != null){
                    //有上一个点我们才会连线
                    mMovePath.reset() //重置路径 清空
                    //起点永远是上一个点
                    mMovePath.moveTo(mLastSelectedDot!!.cx,mLastSelectedDot!!.cy)
                    //终点是当前点
                    mMovePath.lineTo(x,y)
                    //绘制
                    invalidate()
                }
            }
        }
    }

    //外部设置显示错误状态
    fun showError(){
        //1.点亮的点状态改为error
        mDotModels.forEach { dot ->
            if (dot.state == DotState.SELECTED){
                dot.state = DotState.ERROR
            }
        }
        //2.线的颜色改为红丝
        mMovePaint.color = Color.RED

        invalidate() //通知界面绘制
    }

    //处理结果
    private fun handleResult(){
        //清空movePath
        mMovePath.reset()
        invalidate()

        //将结果回调给外部
        val shouldClear = mCallBack(mPasswordBuilder.toString())

        val delayTime = if (shouldClear) {
            200L
        }else{
            //显示错误状态
            1000L
        }
        //500ms 后清空界面
        delayTask(delayTime) {
            clear()
        }
    }

    private fun clear(){
        //清空连线
        mLinePath.reset()
        //点亮的点还原
        mDotModels.forEach { dot ->
            if (dot.state != DotState.NORMAL){
                dot.state = DotState.NORMAL
            }
        }
        //清空密码
        mPasswordBuilder.clear()
        //画笔颜色还原
        mMovePaint.color = resources.getColor(R.color.blue,null)
        invalidate() //通知界面绘制
    }
}