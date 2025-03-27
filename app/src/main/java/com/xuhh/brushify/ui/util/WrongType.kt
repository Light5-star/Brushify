package com.xuhh.brushify.ui.util

/**
 * 登录错误类型
 * USER_NOT_FOUND, 用户已存在
*  WRONG_PASSWORD, 密码错误
*  USER_LOGINED, 用户已登录
 */
enum class WrongType {
    USER_NOT_FOUND,
    WRONG_PASSWORD,
    USER_LOGINED,
    NAME_OR_PASSWORD_EMPTY
}