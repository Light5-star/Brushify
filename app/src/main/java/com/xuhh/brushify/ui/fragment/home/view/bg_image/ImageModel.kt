package com.xuhh.brushify.ui.fragment.home.view.bg_image

import androidx.annotation.DrawableRes
import com.xuhh.brushify.ui.fragment.home.layer.LayerState

data class ImageModel(
    @DrawableRes val id: Int,
    var state: LayerState = LayerState.NORMAL
)