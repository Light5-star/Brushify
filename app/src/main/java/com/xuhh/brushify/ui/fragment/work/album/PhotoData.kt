package com.xuhh.brushify.ui.fragment.work.album

import com.xuhh.brushify.ui.fragment.home.file.FileManager

fun loadPhotoModels():ArrayList<PhotoModel>{
    val models = arrayListOf<PhotoModel>()

    FileManager.instance.loadThumbnailImages().forEach { thumbnailPath ->
        val index = thumbnailPath.lastIndexOf("/")
        val name = thumbnailPath.substring(index+1)
        val model = PhotoModel(thumbnailPath,FileManager.instance.getOriginalPathForFile(name))
        models.add(model)
    }
    return models
}