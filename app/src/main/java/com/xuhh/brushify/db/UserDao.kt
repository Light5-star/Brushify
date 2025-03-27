package com.xuhh.brushify.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("select * from user_table")
    fun loadUsers(): LiveData<List<User>>

    @Query("select * from user_table where name = :name")
    suspend fun findUser(name:String):List<User>

    @Update
    suspend fun updateUser(user: User)

    @Query("select * from user_table where isLogin = 1")
    suspend fun findLoginedUser():List<User>
}