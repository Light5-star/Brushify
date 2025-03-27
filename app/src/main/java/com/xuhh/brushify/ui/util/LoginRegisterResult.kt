package com.xuhh.brushify.ui.util

/**
 * 1.希望这个类能够被继承
 * 2.外部不能继承，只能自己内部实现继承
 * 密封类
 *    如果某个类有固定的子类类型
 *    在当前这个文件或者类的内部实现继承
 */
sealed class LoginRegisterResult{
    //如果这个类没有属性 直接使用object替换class
    class Success : LoginRegisterResult()
    class Failure(val type: WrongType): LoginRegisterResult()
    class None: LoginRegisterResult()
}



