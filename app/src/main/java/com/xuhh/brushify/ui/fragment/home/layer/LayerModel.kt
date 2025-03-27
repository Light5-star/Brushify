package com.xuhh.brushify.ui.fragment.home.layer

import android.graphics.Bitmap
import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.item.ItemDrag
import com.drake.brv.item.ItemSwipe

data class LayerModel (
    val id: Int,
    val bitmap: Bitmap,
    var state: LayerState = LayerState.NORMAL,
    override var itemOrientationSwipe: Int = ItemOrientation.LEFT,
    override var itemOrientationDrag: Int = ItemOrientation.VERTICAL
    ): ItemSwipe,ItemDrag