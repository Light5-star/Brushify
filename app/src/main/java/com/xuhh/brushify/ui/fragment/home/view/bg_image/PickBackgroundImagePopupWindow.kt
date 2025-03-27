package com.xuhh.brushify.ui.fragment.home.view.bg_image

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.app.ActionBar.LayoutParams
import com.bumptech.glide.Glide
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.xuhh.brushify.R
import com.xuhh.brushify.databinding.LayerItemLayoutBinding
import com.xuhh.brushify.databinding.PickImagePopupViewLayoutBinding
import com.xuhh.brushify.ui.fragment.home.layer.LayerState

class PickBackgroundImagePopupWindow(val context: Context) {
    private var mBinding: PickImagePopupViewLayoutBinding? = null
    private val mImageModelManager = ImageModelManager()
    var addImageSelectListener: (Int) -> Unit = {}
    private val popupWindow: PopupWindow by lazy {
        val inflater = LayoutInflater.from(context)
        mBinding = PickImagePopupViewLayoutBinding.inflate(inflater)
        initRecycleView()
        PopupWindow(context).apply {
            contentView = mBinding!!.root
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun showAsDropDown(view: View, offsetX: Int = 0, offsetY: Int = 0){
        popupWindow.showAsDropDown(view, offsetX, offsetY)
    }
    fun showAtLocation(parent: View,gravity: Int = Gravity.CENTER, offsetX: Int = 0, offsetY: Int = 0){
        popupWindow.showAtLocation(parent,gravity,offsetX, offsetY)
    }
    fun hide(){
        popupWindow.dismiss()
    }

    private fun initRecycleView(){
        mBinding?.apply {
            recyclerView.linear().setup {
                addType<ImageModel>(R.layout.layer_item_layout)
                onBind {
                    val binding = getBinding<LayerItemLayoutBinding>()
                    val model = getModel<ImageModel>()
                    Glide.with(binding.root).load(model.id).into(binding.layerImageView)
                    //显示
                    binding.coverView.visibility = if (model.state == LayerState.SELECTED) View.VISIBLE else View.INVISIBLE
                    binding.root.setOnClickListener {
                        mImageModelManager.select(model)
                        addImageSelectListener(model.id)
                        refreshRecyclerview()
                    }
                }
            }.models = mImageModelManager.getModels()
        }
    }

    private fun refreshRecyclerview(){
        mBinding!!.recyclerView.models = mImageModelManager.getModels()
    }
}