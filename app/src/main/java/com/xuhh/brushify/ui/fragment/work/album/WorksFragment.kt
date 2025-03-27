package com.xuhh.brushify.ui.fragment.work.album

import android.graphics.Color
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.xuhh.brushify.R
import com.xuhh.brushify.databinding.FragmentMyWorksBinding
import com.xuhh.brushify.databinding.MyworkAlbumItemLayoutBinding
import com.xuhh.brushify.ui.base.BaseFragment
import com.xuhh.brushify.ui.fragment.home.file.FileManager
import com.xuhh.brushify.ui.fragment.home.view.LoadingView
import com.xuhh.brushify.ui.fragment.work.PhotoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorksFragment: BaseFragment<FragmentMyWorksBinding>() {
    private val mViewModel: PhotoViewModel by activityViewModels()
    private var mEditingState = EditingState.NORMAL
    private var mSelectedModel = arrayListOf<PhotoModel>()
    private val mLoadingView: LoadingView by lazy { LoadingView(requireContext()) }

    override fun initBinding(): FragmentMyWorksBinding {
        return FragmentMyWorksBinding.inflate(layoutInflater)
    }

    override fun initView() {
        mViewModel.reloadData()
        mViewModel.photoModels.observe(viewLifecycleOwner){ models ->
            mBinding.recyclerView.models = models
        }
        mBinding.recyclerView.grid(5).setup {
            addType<PhotoModel>(R.layout.mywork_album_item_layout)
            onBind {
                val binding = getBinding<MyworkAlbumItemLayoutBinding>()
                val model = getModel<PhotoModel>()
                Glide.with(requireContext()).load(model.thumbnailPath).into(binding.ivThumbnail)
                if (mEditingState == EditingState.NORMAL) {
                    binding.ivUnChoose.visibility = View.INVISIBLE
                }else{
                    binding.ivUnChoose.visibility = View.VISIBLE
                }

                if (model.selectState == SelectState.NORMAL) {
                    binding.ivSelected.visibility = View.INVISIBLE
                }else{
                    binding.ivSelected.visibility = View.VISIBLE
                }

                binding.root.setOnClickListener{
                    if (mEditingState == EditingState.NORMAL){
                        mViewModel.selectedIndex = modelPosition
                        findNavController().navigate(R.id.action_workFragment_to_photoBrowserFragment)
                    }else{

                        if (model.selectState == SelectState.NORMAL){//选中
                            model.selectState = SelectState.SELECTED
                            mSelectedModel.add(model)
                        }else {
                            model.selectState = SelectState.NORMAL
                            mSelectedModel.remove(model)
                        }
                        notifyItemChanged(modelPosition)
                    }
                }
            }
        }
        mBinding.tvEdit.setOnClickListener{
            if (mEditingState == EditingState.NORMAL){
                mBinding.tvEdit.text = "Done"
                mBinding.tvEdit.setTextColor(Color.WHITE)
                mBinding.deleteImageView.visibility = View.VISIBLE
                mEditingState = EditingState.EDITING
            }else{
                mBinding.tvEdit.text = "Edit"
                mBinding.tvEdit.setTextColor(Color.parseColor("#B4B4B4"))
                mBinding.deleteImageView.visibility = View.INVISIBLE
                mEditingState = EditingState.NORMAL
            }
        }

        mBinding.backImageView.setOnClickListener {
            findNavController().navigateUp()
        }

        mBinding.deleteImageView.setOnClickListener {
            if (mSelectedModel.isEmpty()) return@setOnClickListener
            mLoadingView.show(mBinding.root)
            lifecycleScope.launch(Dispatchers.IO) {
                mSelectedModel.forEach { model ->
                    FileManager.instance.removeFile(model.originalPath)
                    FileManager.instance.removeFile(model.thumbnailPath)
                }
                withContext(Dispatchers.Main){
                    mLoadingView.hide {
                        mViewModel.removeAll(mSelectedModel)
                        mSelectedModel.clear()
                        mBinding.recyclerView.bindingAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}