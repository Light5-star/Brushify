package com.xuhh.brushify.ui.fragment.work.album

data class PhotoModel(
    val thumbnailPath: String,
    val originalPath: String,
    var selectState: SelectState = SelectState.NORMAL
)
