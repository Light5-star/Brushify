package com.xuhh.brushify.db

import android.content.Context
import androidx.lifecycle.LiveData

class Repository(context: Context) {
    private val database = UserDatabase.getInstance(context)
    private val userDao = database.userDao()

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    fun loadUsers(): LiveData<List<User>>{
        return userDao.loadUsers()
    }

    suspend fun updateUser(user: User){
        userDao.updateUser(user)
    }

    suspend fun findUser(name:String):List<User>{
        return userDao.findUser(name)
    }

    suspend fun findLoginedUser():List<User>{
        return userDao.findLoginedUser()
    }
}