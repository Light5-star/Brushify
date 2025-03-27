package com.xuhh.brushify.ui.fragment.home.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import com.xuhh.brushify.ui.fragment.home.draw.LayerManager
import com.xuhh.brushify.ui.fragment.home.draw.ShapeType
import com.xuhh.brushify.ui.util.OperationType
import com.xuhh.brushify.viewModel.HomeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DrawView(
    context: Context,
    attrs: AttributeSet? = null
): View(context,attrs) {
    private var mDrawShapeType: ShapeType = ShapeType.NONE
    private var mActionType:ActionType = ActionType.NONE
    private val layerManager: LayerManager by lazy {
        HomeViewModel.instance().mLayerManager
    }

    var refreshLayerListener: () -> Unit = {}
    var addShowKeyboardListener: (Boolean) -> Unit = {}

    //文本输入状态
    private var mTextState = TextState.NONE
    private val mTextColorChangeReceiver: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            refreshTextColor()
        }
    }

    @DrawableRes
    private var mBackgroundResourceId: Int? = null
        set(value){
            field = value
            if (value != null){
                mBackgroundBitmap = BitmapFactory.decodeResource(resources, value)
                invalidate()
            }
        }
    private var mBackgroundBitmap: Bitmap? = null

    fun changeBackgroundImage(@DrawableRes id:Int){
        mBackgroundResourceId = id
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //注册广播
        val intentFilter = IntentFilter(BroadCastCenter.TEXT_COLOR_CHANGE_NAME)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                mTextColorChangeReceiver,
                intentFilter,
                Context.RECEIVER_EXPORTED
            )
        }else{
            context.registerReceiver(
                mTextColorChangeReceiver,
                intentFilter
            )
        }
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //销毁广播
        context.unregisterReceiver(mTextColorChangeReceiver)
    }

    val mRect = RectF()
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        layerManager.addLayer(measuredWidth, measuredHeight)
        mRect.right = measuredWidth.toFloat()
        mRect.bottom = measuredHeight.toFloat()
    }

    fun refreshTextColor(){
        if (mDrawShapeType == ShapeType.Text && mTextState == TextState.EDITING){
            invalidate()
        }
    }

    //刷新
    fun refreshDrawView(){
        invalidate()
    }

    suspend fun getBitmap(): Flow<Bitmap> {
        val bitmapFlow:Flow<Bitmap> = flow{
            val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            mBackgroundBitmap?.let { canvas.drawBitmap(mBackgroundBitmap!!,null,mRect,null) }
            layerManager.getLayerBitmaps().forEach { layerBitmap ->
                canvas.drawBitmap(layerBitmap,0f,0f,null)
            }
            emit(bitmap)
        }
        return bitmapFlow
    }

    override fun onDraw(canvas: Canvas) {
        if (mBackgroundResourceId != null){
            canvas.drawBitmap(mBackgroundBitmap!!,null,mRect,null)
        }
        layerManager.draw()
        layerManager.getLayerBitmaps().forEach { bitmap ->
            //在view上绘制图
            canvas.drawBitmap(bitmap,0f,0f,null)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                when(mActionType) {
                    ActionType.DRAW -> {
                        if ( mDrawShapeType == ShapeType.Text && mTextState == TextState.EDITING) {
                            addShowKeyboardListener(false)
                            mTextState = TextState.NONE
                            layerManager.updateShapeState(ShapeState.NORMAL)
                            invalidate()
                        }else {
                            layerManager.addShape(mDrawShapeType, event.x, event.y)
                            layerManager.updateShapeState(ShapeState.DRAWING)

                            if (mDrawShapeType == ShapeType.Text) {
                                if (mTextState == TextState.NONE) {
                                    addShowKeyboardListener(true)
                                    mTextState = TextState.EDITING
                                }
                            }
                        }
                    }
                    ActionType.FILL -> {
                        layerManager.fillColor(event.x, event.y)
                        invalidate()
                    }
                    ActionType.MOVE -> {
                        layerManager.selectShape(event.x, event.y)
                        invalidate()
                    }
                    else -> {  }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                when(mActionType) {
                    ActionType.DRAW,ActionType.MOVE -> {
                        layerManager.addEndPoint(event.x, event.y)
                        invalidate()
                    }
                    else -> { }
                }
            }
            MotionEvent.ACTION_UP -> {
                refreshLayerListener()
                //编辑状态
                if (mDrawShapeType != ShapeType.Text && mActionType != ActionType.MOVE) {
                    layerManager.updateShapeState(ShapeState.NORMAL)
                    invalidate()
                }
            }
        }
        return true
    }

    /**
     * 接收文本
     */
    fun refreshText(text: String){
        HomeViewModel.instance().mLayerManager.updateText(text)
        invalidate()
    }

    /**
     * 设置当前绘制工具类型为NONE
     */
    fun resetDrawToolType(){
        mActionType = ActionType.NONE
    }

    fun setCurrentDrawType(type:OperationType){
        when (type) {
            OperationType.NONE -> {
                mActionType = ActionType.NONE
                mDrawShapeType = ShapeType.NONE
                mTextState = TextState.NONE
            }
            OperationType.DRAW_MENU -> {
                mActionType = ActionType.NONE
                mDrawShapeType = ShapeType.NONE
                mTextState = TextState.NONE
            }
            OperationType.DRAW_MOVE -> {
                mActionType = ActionType.MOVE
                mDrawShapeType = ShapeType.NONE
                mTextState = TextState.NONE
                //layerManager.updateMoveMode(true)
            }
            OperationType.DRAW_BRUSH -> {
                mActionType = ActionType.FILL
                mDrawShapeType = ShapeType.NONE
                mTextState = TextState.NONE
            }
            else -> {
                mActionType = ActionType.DRAW
                mDrawShapeType = when (type) {
                    OperationType.DRAW_CIRCLE -> ShapeType.Circle
                    OperationType.DRAW_RECTANGLE -> ShapeType.Rectangle
                    OperationType.DRAW_LINE -> ShapeType.Line
                    OperationType.DRAW_CURVE -> ShapeType.Curve
                    OperationType.DRAW_TRIANGLE -> ShapeType.Triangle
                    OperationType.DRAW_BEZEL -> ShapeType.Bezel
                    OperationType.DRAW_LINE_ARROW -> ShapeType.Arrow
                    OperationType.DRAW_LOCATION -> ShapeType.Location
                    OperationType.DRAW_TEXT -> ShapeType.Text
                    OperationType.DRAW_ERASER -> ShapeType.Eraser
                    else -> ShapeType.NONE
                }
            }
        }
    }
}