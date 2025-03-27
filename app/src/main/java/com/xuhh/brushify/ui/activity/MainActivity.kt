package com.xuhh.brushify.ui.activity

import com.xuhh.brushify.databinding.ActivityMainBinding
import com.xuhh.brushify.ui.base.BaseActivity


class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun initBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {

    }

}