package com.xuhh.brushify.ui.fragment.home.draw

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import com.xuhh.brushify.ui.fragment.home.draw.shapes.ArrowLineShape
import com.xuhh.brushify.ui.fragment.home.draw.shapes.BezelShape
import com.xuhh.brushify.ui.fragment.home.draw.shapes.CircleShape
import com.xuhh.brushify.ui.fragment.home.draw.shapes.CurveShape
import com.xuhh.brushify.ui.fragment.home.draw.shapes.EraserCurveShape
import com.xuhh.brushify.ui.fragment.home.draw.shapes.LineShape
import com.xuhh.brushify.ui.fragment.home.draw.shapes.LocationShape
import com.xuhh.brushify.ui.fragment.home.draw.shapes.RectangleShape
import com.xuhh.brushify.ui.fragment.home.draw.shapes.TextShape
import com.xuhh.brushify.ui.fragment.home.draw.shapes.TriangleShape
import com.xuhh.brushify.ui.fragment.home.view.BroadCastCenter
import com.xuhh.brushify.ui.fragment.home.view.ShapeState
import com.xuhh.brushify.viewModel.HomeViewModel

/**
 * 管理图层
 */
class Layer(val id: Int, val width: Int, val height: Int) {
    private var mCanvas: Canvas
    private var mBitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val mShapes: ArrayList<BaseShape> = arrayListOf()
    private var mLastSelectedShape: BaseShape? = null

    private val mIconClickReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (mLastSelectedShape != null) {
                    mLastSelectedShape?.unSelect()
                    mLastSelectedShape = null
                }
            }
        }
    }

    init {
        mCanvas = Canvas(mBitmap)
        val intentField = IntentFilter(BroadCastCenter.ICON_CLICK_BROADCAST_NAME)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            HomeViewModel.instance().getContext().registerReceiver(
                mIconClickReceiver,
                intentField,
                Context.RECEIVER_EXPORTED,
            )
        }else{
            HomeViewModel.instance().getContext().registerReceiver(
                mIconClickReceiver,
                intentField
            )
        }
    }

    fun onDestroy(){
        HomeViewModel.instance().getContext().unregisterReceiver(mIconClickReceiver)
    }

    fun getBitmap(): Bitmap {
        return mBitmap
    }

    fun addShape(type: ShapeType,startX: Float,startY: Float) {
        var tShape: BaseShape? = null
        tShape = when(type) {
            ShapeType.Circle -> { CircleShape() }
            ShapeType.Rectangle -> { RectangleShape() }
            ShapeType.Line -> { LineShape() }
            ShapeType.Curve -> { CurveShape() }
            ShapeType.Triangle -> { TriangleShape() }
            ShapeType.Bezel -> { BezelShape() }
            ShapeType.Arrow -> { ArrowLineShape() }
            ShapeType.Location -> { LocationShape() }
            ShapeType.Text -> { TextShape() }
            ShapeType.Eraser -> { EraserCurveShape() }
            else -> { null }
        }

        tShape?.let {
            it.setStartPoint(startX,startY)
            mShapes.add(it)
        }
    }

    fun updateShapeState(state: ShapeState){
        currentShape()?.updateShapeState(state)
    }

    fun addEndPoint(endX: Float,endY: Float) {
        currentShape()?.setEndPoint(endX,endY)
    }

    fun draw() {
        mBitmap.eraseColor(Color.TRANSPARENT)
        mCanvas.setBitmap(mBitmap)
        mShapes.forEach { shape ->
            shape.draw(mCanvas)
        }
    }

    fun undo(){
        if (mShapes.isNotEmpty()){
            mShapes.removeLast()
        }
    }

    fun clear(){
        mShapes.clear()
    }

    fun updateText(text: String) {
        currentShape()?.let { shape ->
            if (shape is TextShape) {
                shape.updateText(text)
            }
        }
    }

    fun fillColor(x: Float,y: Float){
        for (shape in mShapes.asReversed()) {
            if (shape.containsPointInPath(x,y)) {
                shape.fillColor()
            }
        }
    }

    fun selectShape(x: Float,y: Float) {
        val selectedShape = findSelectedShape(x,y)
        if (selectedShape == null) {
            if (mLastSelectedShape != null) {
                mLastSelectedShape?.unSelect()
                mLastSelectedShape = null
            }
        }else{
            if (mLastSelectedShape == null) {
                selectedShape.select()
                mLastSelectedShape = selectedShape
            }else {
                if (mLastSelectedShape != selectedShape) {
                    mLastSelectedShape?.unSelect()
                    selectedShape.select()
                    mLastSelectedShape = selectedShape
                }else {
                    selectedShape.calculateMovePosition(x,y)
                }
            }
        }
    }

    private fun findSelectedShape(x: Float,y: Float): BaseShape? {
        for (shape in mShapes.asReversed()) {
            if (shape.containsPointInRect(x,y)) {
                return shape
            }
        }
        return null
    }

    private fun currentShape():BaseShape?{
        //先判断是不是有当前选中的
        if (mLastSelectedShape != null) return mLastSelectedShape!!

        return if (mShapes.isNotEmpty()){
            mShapes.last()
        }else{
            null
        }
    }

    fun updateMoveMode(isInMoveMode: Boolean) {
        mShapes.forEach { shape ->
            shape.updateMoveMode(isInMoveMode)
        }
    }

}