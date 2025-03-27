package com.xuhh.brushify.ui.fragment.login

import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xuhh.brushify.R
import com.xuhh.brushify.databinding.FragmentPicLoginBinding
import com.xuhh.brushify.ui.base.BaseFragment
import com.xuhh.brushify.ui.util.toast
import com.xuhh.brushify.viewModel.HomeViewModel

class PicLoginFragment: BaseFragment<FragmentPicLoginBinding>() {
    override fun initBinding(): FragmentPicLoginBinding {
        return FragmentPicLoginBinding.inflate(layoutInflater)
    }

    override fun initView() {
        super.initView()
        mBinding.unlockView.addPicPathFinishedListener { password ->
            if (HomeViewModel.instance().isLogin()){
                if (HomeViewModel.instance().passwordIsTrue(password)){
                    toast("登录成功")
                    findNavController().navigate(R.id.action_picLoginFragment_to_homeFragment)
                    true
                }else{
                    toast("密码错误")
                    false
                }
            }else{
                HomeViewModel.instance().setPassword(password)
                toast("设置密码成功")
                findNavController().navigate(R.id.action_picLoginFragment_to_homeFragment)
                true
            }
        }

    }


}