package com.xuhh.brushify.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xuhh.brushify.ui.util.TypeConverter

//声明不支持的类型到哪里去找转化器
@TypeConverters(TypeConverter::class)
@Database(entities = [User::class], version = 1)
abstract class UserDatabase: RoomDatabase(){
    abstract fun userDao(): UserDao

    companion object{
        private var INSTANCE: UserDatabase? = null
        fun getInstance(context: Context): UserDatabase {
            if (INSTANCE == null){
                synchronized(this){
                    if (INSTANCE == null){
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            UserDatabase::class.java,
                            "user_database"
                        ).build()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}