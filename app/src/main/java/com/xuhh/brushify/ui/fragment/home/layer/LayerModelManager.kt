package com.xuhh.brushify.ui.fragment.home.layer

import com.xuhh.brushify.ui.fragment.home.draw.Layer

class LayerModelManager private constructor(){
    private val dataList:ArrayList<LayerModel> = arrayListOf()
    private var mLastSelectedLayerModel: LayerModel? = null

    companion object {
        val instance: LayerModelManager by lazy {
            LayerModelManager()
        }
    }

    // 加载图层数据
    fun getLayerModels(): List<LayerModel> {
        return dataList
    }

    fun getCurrentLayerId(): Int {
        return mLastSelectedLayerModel!!.id
    }

    fun addLayer(layer: Layer){
        val layerModel = LayerModel(layer.id, layer.getBitmap(),LayerState.SELECTED)
        mLastSelectedLayerModel?.state = LayerState.NORMAL
        dataList.add(0, layerModel)
        mLastSelectedLayerModel = layerModel
    }

    //修改滑动删除后应该操作哪一个
    fun resetCurrentSelected(index: Int,model: LayerModel){
        if (model.id != mLastSelectedLayerModel!!.id) return
        if (index == dataList.size){
            //删除最后一个
            if (dataList.isNotEmpty()){
                mLastSelectedLayerModel = dataList.first()
                mLastSelectedLayerModel!!.state = LayerState.SELECTED
            }else{
                mLastSelectedLayerModel = null
            }
        }else{
            //删除的不是最后一个，自动下移
            //下一个元素的编号和删除元素的编号一致
            mLastSelectedLayerModel = dataList[index]
            mLastSelectedLayerModel!!.state = LayerState.SELECTED
        }
    }

    //删除
    fun removeLayer(id: Int){
        //一个
        if (dataList.size == 1) return
        dataList.forEachIndexed { index, layerModel ->
            if (layerModel.id == id) {
                if (layerModel.state == LayerState.SELECTED) {
                    if (index == dataList.size -1){
                        mLastSelectedLayerModel = dataList.first()
                        mLastSelectedLayerModel!!.state = LayerState.SELECTED
                    }else{
                        mLastSelectedLayerModel = dataList[index + 1]
                        mLastSelectedLayerModel!!.state = LayerState.SELECTED
                    }
                }
                dataList.remove(layerModel)
            }
            return
        }
    }

    fun switchLayer(source: Int,dest: Int){
        val temp = dataList[source]
        dataList[source] = dataList[dest]
        dataList[dest] = temp
    }

    fun selectLayer(model: LayerModel){
        if (mLastSelectedLayerModel != model){
            mLastSelectedLayerModel!!.state = LayerState.NORMAL
            model.state = LayerState.SELECTED
            mLastSelectedLayerModel = model
        }
    }
}

