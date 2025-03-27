package com.xuhh.brushify.ui.fragment.work.photobrowser

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import android.view.View
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.xuhh.brushify.R
import com.xuhh.brushify.databinding.FragmentPhotoBroswerBinding
import com.xuhh.brushify.databinding.PhotoBrowserItemLayoutBinding
import com.xuhh.brushify.model.IconModel
import com.xuhh.brushify.ui.base.BaseFragment
import com.xuhh.brushify.ui.fragment.home.file.FileManager
import com.xuhh.brushify.ui.fragment.home.view.LoadingView
import com.xuhh.brushify.ui.fragment.work.PhotoViewModel
import com.xuhh.brushify.ui.fragment.work.album.PhotoModel
import com.xuhh.brushify.ui.util.OperationType
import com.xuhh.brushify.ui.util.TimeUtil
import com.xuhh.brushify.ui.util.delayTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class PhotoBrowserFragment: BaseFragment<FragmentPhotoBroswerBinding>() {
    private var mCurrentIndex: Int = 0
    private val mViewModel: PhotoViewModel by activityViewModels()
    private val mLoadingView: LoadingView by lazy {
        LoadingView(requireContext())
    }

    override fun initBinding(): FragmentPhotoBroswerBinding {
        return FragmentPhotoBroswerBinding.inflate(layoutInflater)
    }

    override fun initView() {
        super.initView()
        //监听数据
        mViewModel.photoModels.observe(viewLifecycleOwner){ models ->
            mBinding.recyclerView.models = models
            mBinding.recyclerView.scrollToPosition(mViewModel.selectedIndex)
            mCurrentIndex = mViewModel.selectedIndex
            mBinding.blurImageView.setImageBitmap(BlurUtil.blur(models[mViewModel.selectedIndex].thumbnailPath,50f))
        }
        //分页显示
        PagerSnapHelper().attachToRecyclerView(mBinding.recyclerView)
        //recycleview滑动监听
        mBinding.recyclerView.linear(RecyclerView.HORIZONTAL).setup {
            addType<PhotoModel>(R.layout.photo_browser_item_layout)
            onBind {
                val binding = getBinding<PhotoBrowserItemLayoutBinding>()
                val model = getModel<PhotoModel>()
                Glide.with(context).load(model.thumbnailPath).into(binding.iconImageView)
            }

            mBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val index = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    //设置虚化背景
                    if (index != -1 && mCurrentIndex != index) {
                        val models = mViewModel.photoModels.value
                        models?.let {
                            mBinding.blurImageView.setImageBitmap(
                                BlurUtil.blur(it[index].thumbnailPath, 50f)
                            )
                        }
                        mCurrentIndex = index
                    }

                }
            })
        }
        //配置底部
        mBinding.actionMenuView.setIcons(listOf(
            IconModel(OperationType.OPERATION_DELETE,R.string.garbage),
            IconModel(OperationType.MENU_DOWNLOAD,R.string.download),
            IconModel(OperationType.MENU_SHARE,R.string.share)
        ))

        mBinding.actionMenuView.iconClickListener = { type,_ ->
            delayTask(200){
                mBinding.actionMenuView.resetIconState()
            }
            if (type == OperationType.OPERATION_DELETE) {
                mLoadingView.show(mBinding.root)
                lifecycleScope.launch(Dispatchers.IO) {
                    val model = mViewModel.photoModels.value?.get(mCurrentIndex)
                    FileManager.instance.removeFile(model!!.originalPath)
                    FileManager.instance.removeFile(model!!.thumbnailPath)
                    withContext(Dispatchers.Main){
                        mLoadingView.hide {
                            mViewModel.removeAll(listOf(model))
                            mBinding.recyclerView.bindingAdapter.notifyDataSetChanged()
                            if (mViewModel.photoModels.value!!.isNotEmpty()){
                                if (mViewModel.photoModels.value!!.size - 1 == mCurrentIndex){
                                    mCurrentIndex = 0
                                }
                                val newModel = mViewModel.photoModels.value!![mCurrentIndex]
                                mBinding.blurImageView.setImageBitmap(BlurUtil.blur(newModel.thumbnailPath,50f))
                            }else{
                                mBinding.blurImageView.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            } else if (type == OperationType.MENU_DOWNLOAD) {
                downloadToPhotoAlbum()
            } else if (type == OperationType.MENU_SHARE) {
                shareImage()
            }
        }
    }

    private fun downloadToPhotoAlbum() {
        mLoadingView.show(mBinding.root)
        val path = mViewModel.photoModels.value!![mCurrentIndex].originalPath
        val bitmap = BitmapFactory.decodeFile(path)

        val imagesUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }else{
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val contentValues = ContentValues().apply {

            put(MediaStore.Images.Media.DISPLAY_NAME, TimeUtil.getTimeName())
            put(MediaStore.Images.Media.WIDTH,"${bitmap.width}")
            put(MediaStore.Images.Media.HEIGHT,"${bitmap.height}")
            put(MediaStore.Images.Media.MIME_TYPE,"image/png")
        }
        val imgUri = requireContext().contentResolver.insert(imagesUri,contentValues)
        //输出流
        imgUri?.let {
            requireContext().contentResolver.openOutputStream(imgUri)?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                //动画
                mLoadingView.hide()
            }
        }
    }

    private fun shareImage() {
        lifecycleScope.launch {
            val path = mViewModel.photoModels.value!![mCurrentIndex].originalPath
            val bitmap = BitmapFactory.decodeFile(path)
            val externalDir = requireContext().getExternalFilesDir(null)
            val file = File(externalDir,"brushify.png")
            saveImageToExternalPath(file,bitmap)
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "com.xuhh.brushify.provider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/png"
            intent.putExtra(Intent.EXTRA_STREAM,uri)

            requireContext().startActivity(Intent.createChooser(intent,"分享图片"))

        }
    }

    private fun saveImageToExternalPath(file: File, bitmap: Bitmap) {
        FileOutputStream(file).use { fos ->
            BufferedOutputStream(fos).use { bos ->
                bitmap.compress(Bitmap.CompressFormat.PNG,100,bos)
            }
        }
    }
}