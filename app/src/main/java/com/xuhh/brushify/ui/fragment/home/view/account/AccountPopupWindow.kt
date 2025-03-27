package com.xuhh.brushify.ui.fragment.home.view.account

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.app.ActionBar.LayoutParams
import androidx.navigation.findNavController
import com.xuhh.brushify.R
import com.xuhh.brushify.databinding.AccountLayoutBinding
import com.xuhh.brushify.viewModel.HomeViewModel

class AccountPopupWindow(val context: Context) {
    private var mBinding: AccountLayoutBinding? = null
    private var mView:View? = null
    var addImageSelectListener: (Int) -> Unit = {}
    private val popupWindow: PopupWindow by lazy {
        val inflater = LayoutInflater.from(context)
        mBinding = AccountLayoutBinding.inflate(inflater)
        PopupWindow(context).apply {
            contentView = mBinding!!.root
            initEvent()
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun initEvent(){
        mBinding!!.workLayout.setOnClickListener {
            mBinding!!.bgIndicaterView.y = mBinding!!.workLayout.y
            hide()
            mView?.findNavController()?.navigate(R.id.action_homeFragment_to_accountFragment)
        }
        mBinding!!.lockLayout.setOnClickListener {
            mBinding!!.bgIndicaterView.y = mBinding!!.lockLayout.y
            HomeViewModel.instance().currentNotLogin()
            hide()
            mView?.findNavController()?.navigate(R.id.action_homeFragment_to_picLoginFragment)
        }
        mBinding!!.about.setOnClickListener {
            mBinding!!.bgIndicaterView.y = mBinding!!.about.y
        }
    }

    fun showAsDropDown(view: View, offsetX: Int = 0, offsetY: Int = 0){
        mView = view
        popupWindow.showAsDropDown(view, offsetX, offsetY)
    }
    fun showAtLocation(parent: View,gravity: Int = Gravity.CENTER, offsetX: Int = 0, offsetY: Int = 0){
        mView = parent
        popupWindow.showAtLocation(parent,gravity,offsetX, offsetY)
    }
    fun hide(){
        popupWindow.dismiss()
    }
}