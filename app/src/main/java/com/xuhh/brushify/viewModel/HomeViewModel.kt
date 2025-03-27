package com.xuhh.brushify.viewModel

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.TypedValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.xuhh.brushify.model.IconModel
import com.xuhh.brushify.ui.fragment.home.draw.LayerManager
import com.xuhh.brushify.ui.util.dp2pxF
import com.xuhh.brushify.ui.view.IconTextView
import java.lang.ref.WeakReference

class HomeViewModel(application: Application): AndroidViewModel(application) {
    var mStrokeWidth = application.dp2pxF(1)
    var mColor = Color.BLACK
    var mStrokeStyle = Paint.Style.STROKE

    private var isLock = false
    private var isLogin = false
    private var password: String? = ""

    var mTextSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        18f,
        application.applicationContext.resources.displayMetrics
    )

    //图层
    val mLayerManager: LayerManager = LayerManager()


    fun getContext(): Context {
        return getApplication()
    }

    fun isLock(): Boolean{
        return isLock
    }

    fun setLock(){
        isLock = true
    }

    fun setUnLock(){
        isLock = false
    }

    fun isLogin(): Boolean{
        return isLogin
    }

    fun currentLogin(){
        isLogin = true
    }

    fun currentNotLogin(){
        isLogin = false
    }

    fun passwordIsTrue(password: String): Boolean{
        return this.password == password
    }

    fun setPassword(password: String){
        this.password = password
        setLock()
    }

    //其他地方没有view等的可以使用context
    companion object {
        private var instance: HomeViewModel? = null
        fun init(owner: ViewModelStoreOwner) {
            if (instance == null) {
                instance = ViewModelProvider(owner).get(HomeViewModel::class.java)
            }
        }
        fun instance(): HomeViewModel {
            if (instance == null){
                throw Exception("必须调用init方法")
            }
            return instance!!
        }
    }

}