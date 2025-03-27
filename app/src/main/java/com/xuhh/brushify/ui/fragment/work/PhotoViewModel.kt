package com.xuhh.brushify.ui.fragment.work

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xuhh.brushify.ui.fragment.work.album.PhotoModel
import com.xuhh.brushify.ui.fragment.work.album.loadPhotoModels

class PhotoViewModel: ViewModel() {
    val photoModels: MutableLiveData<ArrayList<PhotoModel>> = MutableLiveData(loadPhotoModels())
    var selectedIndex = 0

    fun reloadData(){
        photoModels.value = loadPhotoModels()
    }

    fun removeAll(models: List<PhotoModel>){
        photoModels.value?.removeAll(models)

    }
}