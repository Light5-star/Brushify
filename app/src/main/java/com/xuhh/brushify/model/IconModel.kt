package com.xuhh.brushify.model

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.xuhh.brushify.R
import com.xuhh.brushify.ui.util.IconState
import com.xuhh.brushify.ui.util.OperationType
import kotlinx.parcelize.Parcelize

@Parcelize
data class IconModel (
    val type: OperationType,
    @StringRes val iconString: Int,
    var state: IconState = IconState.NORMAL,
    @ColorRes val normalColor: Int = R.color.middle_black,
    @ColorRes val selectedColor: Int = R.color.light_blue
):Parcelable