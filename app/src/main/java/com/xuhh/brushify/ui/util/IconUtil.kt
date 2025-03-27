package com.xuhh.brushify.ui.util

import com.xuhh.brushify.R
import com.xuhh.brushify.model.IconModel

/**
 * 获取绘制菜单menu的model
 */
fun getMenuIconModel(): IconModel {
    return IconModel(
        OperationType.DRAW_MENU,
        R.string.menu,
        IconState.SELECTED,
        R.color.light_red,
        R.color.white,
    )
}

/**
 * 构建绘制工具菜单的数据模型
 */
fun getDrawToolIconModels(): List<IconModel> {
    return listOf(
        IconModel(OperationType.DRAW_MOVE, R.string.move),
        IconModel(OperationType.DRAW_ERASER, R.string.eraser),
        IconModel(OperationType.DRAW_BRUSH, R.string.brush),
        IconModel(OperationType.DRAW_LINE_ARROW, R.string.line_arrow),
        IconModel(OperationType.DRAW_CIRCLE, R.string.circle),
        IconModel(OperationType.DRAW_TRIANGLE, R.string.triangle),
        IconModel(OperationType.DRAW_RECTANGLE, R.string.rectangle),
        IconModel(OperationType.DRAW_TEXT, R.string.text),
        IconModel(OperationType.DRAW_LINE, R.string.line),
        IconModel(OperationType.DRAW_BEZEL, R.string.bezel),
        IconModel(OperationType.DRAW_LOCATION, R.string.location),
        IconModel(OperationType.DRAW_CURVE, R.string.curve)
    )
}

/**
 * 构建主菜单的数据模型
 */
fun getHomeMenuIconModels(): List<IconModel> {
    return listOf(
        IconModel(OperationType.MENU_SAVE, R.string.save),
        IconModel(OperationType.MENU_DOWNLOAD, R.string.download),
        IconModel(OperationType.MENU_SHARE, R.string.share),
        IconModel(OperationType.MENU_PICTURE, R.string.picture),
        IconModel(OperationType.MENU_ACCOUNT, R.string.account),
        IconModel(OperationType.MENU_LAYER, R.string.layer)
    )
}

/**
 * 构建绘制操作工具的数据模型
 */
fun getOperationToolIconModels(): List<IconModel> {
    return listOf(
        IconModel(OperationType.OPERATION_UNDO, R.string.undo),
        IconModel(OperationType.OPERATION_DELETE, R.string.garbage),
        IconModel(OperationType.OPERATION_PENCIL, R.string.pencil),
        IconModel(OperationType.OPERATION_PALETTE, R.string.palette)
    )
}