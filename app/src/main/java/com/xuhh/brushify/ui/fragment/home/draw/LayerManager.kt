package com.xuhh.brushify.ui.fragment.home.draw

import android.graphics.Bitmap
import com.xuhh.brushify.ui.fragment.home.layer.LayerModelManager
import com.xuhh.brushify.ui.fragment.home.view.ShapeState

class LayerManager {
    private val layers: ArrayList<Layer> = arrayListOf()
    fun addLayer(width: Int, height: Int) {
        Layer(layers.size, width, height).apply {
            //新图层添加在第一个
            layers.add(0,this)
            LayerModelManager.instance.addLayer(this)

        }
    }
    fun removeLayer(id: Int): Boolean{
        layers.forEach {
            if (it.id == id){
                it.onDestroy()
                layers.remove(it)
                return true
            }
        }
        return false
    }

    fun switchLayers(from: Int,target: Int):Boolean{
        val temp = layers[from]
        layers[from] = layers[target]
        layers[target] = temp
        return true
    }

    fun getCurrentLayer(): Layer? {
        layers.forEach { layer: Layer ->
            if (LayerModelManager.instance.getCurrentLayerId() == layer.id){
                return layer
            }
        }
        return null
    }

    fun getLayerBitmaps(): List<Bitmap>{
        val bitmapList = arrayListOf<Bitmap>()
        //逆序
        for (layer in layers.asReversed()){
            bitmapList.add(layer.getBitmap())
        }
        return bitmapList
    }

    fun addShape(type: ShapeType,startX: Float,startY: Float) {
        getCurrentLayer()?.addShape(type,startX,startY)
    }

    fun addEndPoint(endX: Float,endY: Float) {
        getCurrentLayer()?.addEndPoint(endX,endY)
    }

    fun updateMoveMode(isInMoveMode: Boolean) {
        getCurrentLayer()?.updateMoveMode(isInMoveMode)
    }

    fun draw(){
        for (layer in layers.asReversed()){
            layer.draw()
        }
    }

    fun undo(){
        getCurrentLayer()?.undo()
    }

    fun clearLayer(){
        getCurrentLayer()?.clear()
    }

    fun updateShapeState(state: ShapeState){
        getCurrentLayer()?.updateShapeState(state)
    }

    fun updateText(text: String){
        getCurrentLayer()?.updateText(text)
    }

    fun fillColor(x: Float,y: Float){
        getCurrentLayer()?.fillColor(x,y)
    }

    fun selectShape(x: Float,y: Float){
        getCurrentLayer()?.selectShape(x,y)
    }

    private fun getLayerWithId(id: Int): Layer? {
        layers.forEach {
            if (it.id == id) {
                return it
            }
        }
        return null
    }

}