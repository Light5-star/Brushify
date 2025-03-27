package com.xuhh.brushify.ui.fragment.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.app.ActionBar.LayoutParams
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.xuhh.brushify.R
import com.xuhh.brushify.databinding.ColorPickerLayoutBinding
import com.xuhh.brushify.databinding.FragmentHomeBinding
import com.xuhh.brushify.databinding.LayerItemLayoutBinding
import com.xuhh.brushify.databinding.LayerPopupViewLayoutBinding
import com.xuhh.brushify.ui.base.BaseFragment
import com.xuhh.brushify.ui.fragment.home.file.FileManager
import com.xuhh.brushify.ui.fragment.home.layer.LayerModel
import com.xuhh.brushify.ui.fragment.home.layer.LayerModelManager
import com.xuhh.brushify.ui.fragment.home.layer.LayerState
import com.xuhh.brushify.ui.fragment.home.view.BroadCastCenter
import com.xuhh.brushify.ui.fragment.home.view.HSVColorPickerView
import com.xuhh.brushify.ui.fragment.home.view.LoadingView
import com.xuhh.brushify.ui.fragment.home.view.account.AccountPopupWindow
import com.xuhh.brushify.ui.fragment.home.view.bg_image.PickBackgroundImagePopupWindow
import com.xuhh.brushify.ui.fragment.home.view.strocksize.StrockBarView
import com.xuhh.brushify.ui.util.IconState
import com.xuhh.brushify.ui.util.OperationType
import com.xuhh.brushify.ui.util.TimeUtil
import com.xuhh.brushify.ui.util.delayTask
import com.xuhh.brushify.ui.util.dp2px
import com.xuhh.brushify.ui.util.getDrawToolIconModels
import com.xuhh.brushify.ui.util.getHomeMenuIconModels
import com.xuhh.brushify.ui.util.getMenuIconModel
import com.xuhh.brushify.ui.util.getOperationToolIconModels
import com.xuhh.brushify.viewModel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class HomeFragment: BaseFragment<FragmentHomeBinding>() {
    private val closeBottom:Int by lazy {
        mBinding.drawLayout.top + mBinding.menuIconView.bottom + mBinding.menuIconView.paddingBottom + mBinding.drawLayout.paddingBottom
    }
    private val openBottom:Int by lazy {
        mBinding.drawLayout.bottom
    }
    private var isDrawMenuOpen = true
    private val drawMenuCloseAnim: AnimatorSet by lazy {
        var r = ObjectAnimator.ofFloat(mBinding.menuIconView,"rotation",0f,90f).apply {
            duration = 300
        }
        var m = ObjectAnimator.ofInt(mBinding.drawLayout,"bottom",openBottom,closeBottom).apply {
            duration = 400
        }
        AnimatorSet().apply {
            playTogether(r,m)
        }
    }
    private val drawMenuOpenAnim: AnimatorSet by lazy {
        var r = ObjectAnimator.ofFloat(mBinding.menuIconView,"rotation",90f,0f).apply {
            duration = 300
        }
        var m = ObjectAnimator.ofInt(mBinding.drawLayout,"bottom",closeBottom,openBottom).apply {
            duration = 400
        }
        AnimatorSet().apply {
            playTogether(r,m)
        }

    }
    //操作菜单
    private val actionHideLeft:Int by lazy {
        mBinding.root.width
    }
    private val actionShowLeft:Int by lazy {
        mBinding.actionMenuView.left
    }
    private val actionMenuHideAnim: AnimatorSet by lazy {
        val alpha = ObjectAnimator.ofFloat(mBinding.actionMenuView,"alpha",1f,0f).apply {
            duration = 300
        }
        val move = ObjectAnimator.ofInt(mBinding.actionMenuView,"left",actionShowLeft,actionHideLeft).apply {
            duration = 300
        }
        AnimatorSet().apply{
            playTogether(move)
        }
    }
    private val actionMenuShowAnim: AnimatorSet by lazy {
        val alpha = ObjectAnimator.ofFloat(mBinding.actionMenuView,"alpha",0f,1f).apply {
            duration = 300
        }
        val move = ObjectAnimator.ofInt(mBinding.actionMenuView,"left",actionHideLeft,actionShowLeft).apply {
            duration = 300
        }
        AnimatorSet().apply{
            playTogether(move)
        }
    }

    private val arrowRightAnim: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(mBinding.arrowImageView,"rotation",180f,0f).apply {
            duration = 300
        }
    }
    private val arrowLeftAnim: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(mBinding.arrowImageView,"rotation",0f,-180f).apply {
            duration = 300
        }
    }
    private var isArrowRight = true
    private val mainMenuCloseLeft:Int by lazy {
        mBinding.mainMenuView.right
    }
    private val mainMenuOpenLeft:Int by lazy {
        mBinding.mainMenuView.left
    }
    private val mainMenuCloseAnim: ObjectAnimator by lazy {
        ObjectAnimator.ofInt(mBinding.mainMenuView,"left",mainMenuOpenLeft,mainMenuCloseLeft).apply {
            duration = 300
        }
    }
    private val mainMenuOpenAnim: ObjectAnimator by lazy {
        ObjectAnimator.ofInt(mBinding.mainMenuView,"left",mainMenuCloseLeft,mainMenuOpenLeft).apply {
            duration = 300
        }
    }

    //颜色选择器
    private lateinit var mHSVColorPickerView: HSVColorPickerView
    private val mColorPickerPopupWindow: PopupWindow by lazy {
        val colorPickerBinding = ColorPickerLayoutBinding.inflate(layoutInflater)
        mHSVColorPickerView = colorPickerBinding.root
        mHSVColorPickerView.pickColorCallbaack = { color ->
            HomeViewModel.instance().mColor = color
            mBinding.drawView.refreshTextColor()
        }
        /**
         * PopupWindow(requireContext())
         */
        PopupWindow(requireContext()).apply {
            contentView = colorPickerBinding.root
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private var mLayerPopupViewBinding: LayerPopupViewLayoutBinding? = null
    private val mLayerPopupWindow: PopupWindow by lazy {
        mLayerPopupViewBinding = LayerPopupViewLayoutBinding.inflate(layoutInflater)
        mLayerPopupViewBinding!!.addBtn.clickCallback = {
            HomeViewModel.instance().mLayerManager.addLayer(mBinding.drawView.width, mBinding.drawView.height)
            refreshLayerRecycleView()
        }
        initLayerRecycleView()
        PopupWindow(requireContext()).apply {
            contentView = mLayerPopupViewBinding!!.root
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private val mStrokeBarPopupWindow: PopupWindow by lazy {
        val barView = StrockBarView(requireContext())
        PopupWindow(requireContext()).apply {
            contentView = barView
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private val pickImagePopupWindow: PickBackgroundImagePopupWindow by lazy {
        PickBackgroundImagePopupWindow(requireContext()).apply {
            addImageSelectListener = { resId ->
                mBinding.drawView.changeBackgroundImage(resId)
            }
        }
    }

    private val mAccountPopupWindow: AccountPopupWindow by lazy {
        AccountPopupWindow(requireContext())
    }

    private val mLoadingView: LoadingView by lazy {
        LoadingView(requireContext())
    }

    override fun initBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeViewModel.init(this)
    }

    override fun initView() {
        //工具栏布局
        mBinding.menuIconView.setIconModel(getMenuIconModel())
        mBinding.drawMenuView.setIcons(getDrawToolIconModels())
        mBinding.mainMenuView.setIcons(getHomeMenuIconModels())
        mBinding.actionMenuView.setIcons(getOperationToolIconModels())

        //工具栏伸缩
        mBinding.menuIconView.clickCallback = {
            sendUnselectShapeBroadCast()
            if (isDrawMenuOpen){
                drawMenuCloseAnim.start()
                actionMenuHideAnim.start()
                hideColorPicker()
                mBinding.actionMenuView.resetIconState()
            }else{
                drawMenuOpenAnim.start()
                actionMenuShowAnim.start()
            }
            isDrawMenuOpen = !isDrawMenuOpen
        }
        mBinding.arrowImageView.setOnClickListener {
            sendUnselectShapeBroadCast()
            if (isArrowRight){  //关闭
                arrowLeftAnim.start()
                mainMenuCloseAnim.start()
                hideLayerView()
                mBinding.mainMenuView.resetIconState()
            }else{
                arrowRightAnim.start()
                mainMenuOpenAnim.start()
            }
            isArrowRight = !isArrowRight
        }
        mBinding.actionMenuView.iconClickListener = { type,state ->sendUnselectShapeBroadCast()
            resetMainMenuView()
            when(type){
                OperationType.OPERATION_PALETTE -> {
                    hideStokeBarView()
                    if(state == IconState.NORMAL){
                        hideColorPicker()
                    }else{
                        showColorPicker()
                    }
                }
                OperationType.OPERATION_UNDO -> {
                    //重置按钮状态
                    delayTask(200){
                        mBinding.actionMenuView.resetIconState()
                    }
                    //撤销并刷新
                    HomeViewModel.instance().mLayerManager.undo()
                    mBinding.drawView.refreshDrawView()
                    refreshLayerRecycleView()
                }
                OperationType.OPERATION_DELETE -> {
                    //重置按钮状态
                    delayTask(200){
                        mBinding.actionMenuView.resetIconState()
                    }
                    //撤销并刷新
                    HomeViewModel.instance().mLayerManager.clearLayer()
                    mBinding.drawView.refreshDrawView()
                    refreshLayerRecycleView()
                }
                OperationType.OPERATION_PENCIL -> {
                    hideColorPicker()
                    if(state == IconState.NORMAL){
                        hideStokeBarView()
                    }else{
                        showStokeBarView()
                    }
                }
                else -> {}
            }
        }

        //监听事件
        mBinding.drawMenuView.iconClickListener = { type,state ->
            sendUnselectShapeBroadCast()
            if (type == OperationType.DRAW_MOVE){
                HomeViewModel.instance().mLayerManager
                    .updateMoveMode( state == IconState.SELECTED)
            }

            if (state == IconState.NORMAL){
                mBinding.drawView.setCurrentDrawType(OperationType.NONE)
            }else {
                mBinding.drawView.setCurrentDrawType(type)
            }
        }
        mBinding.mainMenuView.iconClickListener = { type,state ->
            resetActionMenuView()
            sendUnselectShapeBroadCast()
            when(type){
                OperationType.MENU_LAYER -> {
                    mAccountPopupWindow.hide()
                    pickImagePopupWindow.hide()
                    //图层
                    if(state == IconState.NORMAL){
                        hideLayerView()
                    }else{
                        showLayerView()
                    }
                }
                OperationType.MENU_PICTURE -> {
                    hideLayerView()
                    mAccountPopupWindow.hide()
                    if (state == IconState.NORMAL){
                        pickImagePopupWindow.hide()
                    }else {
                        pickImagePopupWindow.showAsDropDown(
                            mBinding.mainMenuView,
                            requireContext().dp2px(50),
                            requireContext().dp2px(25)
                        )
                    }
                }
                OperationType.MENU_DOWNLOAD -> {
                    saveDrawViewToAlbum()
                    delayTask(200){
                        mBinding.mainMenuView.resetIconState()
                    }
                    hideLayerView()
                    mAccountPopupWindow.hide()
                    pickImagePopupWindow.hide()
                }
                OperationType.MENU_SHARE -> {
                    shareImage()
                    delayTask(200){
                        mBinding.mainMenuView.resetIconState()
                    }
                    hideLayerView()
                    mAccountPopupWindow.hide()
                    pickImagePopupWindow.hide()
                }
                OperationType.MENU_ACCOUNT -> {
                    if (state == IconState.SELECTED){
                        mAccountPopupWindow.showAsDropDown(
                            mBinding.mainMenuView,
                            -requireContext().dp2px(30),
                            requireContext().dp2px(30)
                        )
                    }else{
                        mAccountPopupWindow.hide()
                    }
                    hideLayerView()
                    pickImagePopupWindow.hide()
                }
                OperationType.MENU_SAVE -> {
                    delayTask(200){
                        mBinding.mainMenuView.resetIconState()
                    }
                    hideLayerView()
                    mAccountPopupWindow.hide()
                    pickImagePopupWindow.hide()
                    mLoadingView.show(mBinding.root)
                    lifecycleScope.launch {
                        mBinding.drawView.getBitmap().collect{ bitmap ->
                            val name = TimeUtil.getTimeName()
                            FileManager.instance.saveBitmap(bitmap,name,true)
                            val thumbBitmap = Bitmap.createScaledBitmap(
                                bitmap,
                                (bitmap.width * 0.2).toInt(),
                                (bitmap.height * 0.2).toInt(),
                                true)
                            FileManager.instance.saveBitmap(thumbBitmap,name,false)
                            withContext(Dispatchers.Main){
                                mLoadingView.hide()
                            }
                        }
                    }
                }
                else -> {}
            }

        }
        mBinding.drawView.refreshLayerListener = {
            refreshLayerRecycleView()
        }

        mBinding.edInput.addTextChangedListener( afterTextChanged = {
            mBinding.drawView.refreshText(it.toString())
        })

        mBinding.drawView.addShowKeyboardListener = { isShow ->
            if (isShow){
                mBinding.edInput.requestFocus()
                showKeyboard()
            }else {
                mBinding.edInput.clearFocus()
                hideKeyboard()
                mBinding.edInput.text.clear()
            }
        }

    }

    private fun resetMainMenuView() {
        mBinding.mainMenuView.resetIconState()
        hideLayerView()
        mAccountPopupWindow.hide()
        pickImagePopupWindow.hide()
    }

    private fun resetActionMenuView() {
        mBinding.actionMenuView.resetIconState()
        hideStokeBarView()
        hideColorPicker()
    }

    private fun shareImage() {
        lifecycleScope.launch {
            mBinding.drawView.getBitmap().collect{ bitmap ->
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
    }

    private fun saveImageToExternalPath(file:File,bitmap: Bitmap) {
        FileOutputStream(file).use { fos ->
            BufferedOutputStream(fos).use { bos ->
                bitmap.compress(Bitmap.CompressFormat.PNG,100,bos)
            }
        }
    }

    private fun showKeyboard(){
        val insetsController = WindowCompat.getInsetsController(requireActivity().window,mBinding.edInput)
        insetsController.show(WindowInsetsCompat.Type.ime())
    }
    private fun hideKeyboard(){
        val insetsController = WindowCompat.getInsetsController(requireActivity().window,mBinding.edInput)
        insetsController.hide(WindowInsetsCompat.Type.ime())
    }

    private fun refreshLayerRecycleView(){
        if (mLayerPopupViewBinding != null){
            val datas = LayerModelManager.instance.getLayerModels()
            mLayerPopupViewBinding!!.recyclerView.models = datas

        }
    }
    private fun initLayerRecycleView(){
        mLayerPopupViewBinding!!.recyclerView.linear().setup {
            addType<LayerModel>(R.layout.layer_item_layout)
            //绑定
            onBind {
//                val binding = LayerItemLayoutBinding.inflate(layoutInflater)
                val binding = getBinding<LayerItemLayoutBinding>()
                val data = getModel<LayerModel>()
                binding.layerImageView.setImageBitmap(data.bitmap)
                //边框
                binding.coverView.visibility = if (data.state ==LayerState.NORMAL){
                    View.INVISIBLE
                }else{
                    View.VISIBLE
                }

                binding.root.setOnClickListener {
                    //修改 + 刷新
                    LayerModelManager.instance.selectLayer(data)
                    refreshLayerRecycleView()
                }
            }

            itemTouchHelper = ItemTouchHelper(object : DefaultItemTouchCallback(){

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    super.onSwiped(viewHolder, direction)
                    var index = viewHolder.layoutPosition
                    val model = (viewHolder as BindingAdapter.BindingViewHolder).getModel<LayerModel>()
                    //重设选中
                    LayerModelManager.instance.resetCurrentSelected(index,model)
                    //删除图层
                    HomeViewModel.instance().mLayerManager.removeLayer(model.id)
                    refreshLayerRecycleView()
                    mBinding.drawView.refreshDrawView()
                }

                override fun onDrag(
                    source: BindingAdapter.BindingViewHolder,
                    target: BindingAdapter.BindingViewHolder
                ) {
                    super.onDrag(source, target)

                    val sourceIndex = source.layoutPosition
                    val targetIndex = target.layoutPosition
                    val sLayerModel = source.getModel<LayerModel>()
                    val tLayerModel = target.getModel<LayerModel>()

                    HomeViewModel.instance().mLayerManager.switchLayers(sourceIndex,targetIndex)
                    mBinding.drawView.refreshDrawView()
                }
            })
        }.models = LayerModelManager.instance.getLayerModels()
    }

    private fun hideColorPicker(){
        mColorPickerPopupWindow.dismiss()
    }
    private fun showColorPicker(){
        mColorPickerPopupWindow.showAtLocation(
            mBinding.root,
            Gravity.END,
            mBinding.root.width - mBinding.actionMenuView.left,
            0
        )
    }

    private fun hideLayerView(){
        mLayerPopupWindow.dismiss()
    }
    private fun showLayerView(){
        mLayerPopupWindow.showAsDropDown(
            mBinding.mainMenuView,
            requireContext().dp2px(30),
            requireContext().dp2px(30)
        )
    }

    private fun hideStokeBarView(){
        mStrokeBarPopupWindow.dismiss()
    }
    private fun showStokeBarView(){
        mStrokeBarPopupWindow.showAtLocation(
            mBinding.root,
            Gravity.END,
            mBinding.root.width - mBinding.actionMenuView.left,
            0
        )
    }

    private fun sendUnselectShapeBroadCast(){
        //取消选中
        requireContext().sendBroadcast(Intent(BroadCastCenter.ICON_CLICK_BROADCAST_NAME))
        delayTask(200){
            mBinding.drawView.refreshDrawView()
        }
    }

    private fun saveDrawViewToAlbum(){
        lifecycleScope.launch {
            mBinding.drawView.getBitmap().collect{ bitmap ->
                val imagesUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                }else{
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                val contentValues = ContentValues().apply {

                    put(MediaStore.Images.Media.DISPLAY_NAME,TimeUtil.getTimeName())
                    put(MediaStore.Images.Media.WIDTH,"${bitmap.width}")
                    put(MediaStore.Images.Media.HEIGHT,"${bitmap.height}")
                    put(MediaStore.Images.Media.MIME_TYPE,"image/png")
                }
                val imgUri = requireContext().contentResolver.insert(imagesUri,contentValues)
                //输出流
                imgUri?.let {
                    requireContext().contentResolver.openOutputStream(imgUri)?.use {
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,it)
                        //动画
                        mLoadingView.hide()
                    }
                }
            }
        }
    }
}