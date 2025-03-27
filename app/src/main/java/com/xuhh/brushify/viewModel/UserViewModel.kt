package com.xuhh.brushify.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.xuhh.brushify.db.Repository
import com.xuhh.brushify.db.User
import com.xuhh.brushify.ui.util.LoginRegisterResult
import com.xuhh.brushify.ui.util.PasswordType
import com.xuhh.brushify.ui.util.WrongType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class UserViewModel(
    application: Application
):AndroidViewModel(application) {
    private val repository = Repository(application)
    //实时获取用户信息
    val users:LiveData<List<User>>

    //记录登录注册结果
    private var _loginRegisterResult:MutableLiveData<LoginRegisterResult> = MutableLiveData(LoginRegisterResult.None())
    val loginRegisterResult:LiveData<LoginRegisterResult> = _loginRegisterResult

    //记录当前登录的用户
    private var _currentUser:User? = null
    val currentUser get() = _currentUser

    //记录查找登录用户的信息是否完成
    private var _isFindFinihsed:MutableLiveData<Boolean> = MutableLiveData(false)
    val isFindFinihsed: LiveData<Boolean> = _isFindFinihsed

    //启动就查看是否有登录的用户
    init {
        users = repository.loadUsers()

        viewModelScope.launch(Dispatchers.IO) {
            repository.findLoginedUser().also {
                if (it.isNotEmpty()){
                    withContext(Dispatchers.Main){
                        _currentUser = it[0]
                    }
                }
                _isFindFinihsed.postValue(true)
            }
        }
    }

    fun resetLoginState(){
        _loginRegisterResult.value = LoginRegisterResult.None()
    }

    //注册
    fun register(name:String, pinPassword:String, picPassword:String){
        if (name.isEmpty() || pinPassword.isEmpty() || picPassword.isEmpty()){
            _loginRegisterResult.value = LoginRegisterResult.Failure(WrongType.NAME_OR_PASSWORD_EMPTY)
            return
        }
        //插入用户
        viewModelScope.launch(Dispatchers.IO){
            repository.insertUser(User(0,name,pinPassword, picPassword))
            _loginRegisterResult.postValue( LoginRegisterResult.Success())
        }
    }

    //退出登录
    fun logout(){
        if (_currentUser != null){
            //修改这个用户的登录状态
            _currentUser!!.isLogin = false
            //更新数据库信息
            updateUser(_currentUser!!)
        }
    }

    //登录
    fun login(name: String, password: String, type: PasswordType){
        if (name.isEmpty() || password.isEmpty()){
            _loginRegisterResult.value = LoginRegisterResult.Failure(WrongType.NAME_OR_PASSWORD_EMPTY)
            return
        }
        //查找用户是否存在
        viewModelScope.launch(Dispatchers.IO) {
            repository.findUser(name).also {
                withContext(Dispatchers.Main) {
                    if (it.isEmpty()){
                        _loginRegisterResult.value = LoginRegisterResult.Failure(WrongType.USER_NOT_FOUND)
                    }else{
                        val user = it[0]
                        if (type == PasswordType.PIN && password == user.pinPassword){
                            changeLoginStatus(user)
                        }else if (type == PasswordType.PIC && password == user.picPassword){
                            changeLoginStatus(user)
                        }else{
                            _loginRegisterResult.value = LoginRegisterResult.Failure(WrongType.WRONG_PASSWORD)
                        }
                    }
                }
            }
        }
    }

    //修改密码类型
    fun changePasswordType(type: PasswordType){
        if (_currentUser == null) return
        _currentUser!!.passwordType = if (type == PasswordType.PIN) 0 else 1

        updateUser(_currentUser!!)
    }
    /**
     * 修改登录状态
     */
    private fun changeLoginStatus(user: User){
        //判断是否已经有人登录了
        if (_currentUser == null){
            user.isLogin = true
            user.loginDate = Date() //记录登录时间
            updateUser(user)
            _currentUser = user
            //更新登录结果
            _loginRegisterResult.postValue(LoginRegisterResult.Success())
        }else{
            //判断两次登录的人是不是同一个
            if (_currentUser!!.id != user.id){
                //取消之前的状态
                _currentUser!!.isLogin =  false
                updateUser(_currentUser!!)
                //修改当前用户为登录
                user.isLogin = true
                user.loginDate = Date() //记录登录时间
                updateUser(user)
                _loginRegisterResult.postValue(LoginRegisterResult.Success())
                _currentUser = user
            }else{
                //已经登录了，需要更新登录时间
                user.loginDate = Date() //记录登录时间
                updateUser(user)
                _loginRegisterResult.postValue(LoginRegisterResult.Success())
                _currentUser = user
            }
        }
    }

    /**
     * 更新用户
     */
    private fun updateUser(user: User){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateUser(user)
        }
    }
}






