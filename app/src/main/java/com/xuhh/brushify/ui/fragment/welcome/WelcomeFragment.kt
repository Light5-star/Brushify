package com.xuhh.brushify.ui.fragment.welcome

import android.animation.Animator
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.xuhh.brushify.R
import com.xuhh.brushify.databinding.FragmentWelcomeBinding
import com.xuhh.brushify.ui.base.BaseFragment
import com.xuhh.brushify.viewModel.HomeViewModel
import com.xuhh.brushify.viewModel.UserViewModel
import java.util.Date

class WelcomeFragment: BaseFragment<FragmentWelcomeBinding>() {
    private val viewModel:UserViewModel by activityViewModels()
    override fun initBinding(): FragmentWelcomeBinding {
        return FragmentWelcomeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        HomeViewModel.init(requireActivity())
        mBinding.lottieAnim.addAnimatorListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                navigate()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })
    }

    private fun navigate() {
        if (HomeViewModel.instance().isLock()){
            findNavController().navigate(R.id.action_welcomeFragment_to_picLoginFragment)
        }else{
            findNavController().navigate(R.id.homeFragment)
        }
        /**val user = viewModel.currentUser
        //登录注册跳转模块，暂时弃用
        if (user != null){  //登录类型
            val duration = Date().time - user.loginDate.time
            if (duration < user.validate) {  //有效期内
                //有效期内
                if (user.passwordType == 0) {    //pin
                    findNavController().navigate(R.id.action_welcomeFragment_to_pinLoginFragment)
                } else {  //pic
                    findNavController().navigate(R.id.action_welcomeFragment_to_picLoginFragment)
                }
            }else{
                findNavController().navigate(R.id.homeFragment) //直接进入
            }
        }else{  //无用户
            findNavController().navigate(R.id.homeFragment) //直接进入
        }**/
    }

}