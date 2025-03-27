package com.xuhh.brushify.ui.fragment.home.file

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class FileManager private constructor(){
    private lateinit var mContext: Context
    companion object {
        private val Thumbnail = "thumbnail"
        private val Original = "origin"
        val instance: FileManager by lazy { FileManager() }
        fun init(application: Application){
            instance.mContext = application
        }
    }

    private fun getThumbnailPath(): String {
        return "${mContext.filesDir.path}/save/${Thumbnail}"
    }
    private fun getOriginalPath(): String {
        return "${mContext.filesDir.path}/save/${Original}"
    }

    fun saveBitmap(bitmap: Bitmap, name:String, isOrigin:Boolean){
        val path = if (isOrigin){
            getOriginalPath()
        }else{
            getThumbnailPath()
        }
        val filePath = "$path/$name"

        File(path).mkdirs()

        BufferedOutputStream(FileOutputStream(filePath)).use { bos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos)
        }
        Log.d("FileManager", "文件保存路径: $filePath")
    }

    fun loadThumbnailImages(): List<String>{
        val files = arrayListOf<String>()
        //获取缩略图路径
        val thumbnailPath = getThumbnailPath()
        //获取缩略图路径下所有的文件的名字
        File(thumbnailPath).list()?.forEach { name ->
            val filePath = "$thumbnailPath/$name"
            files.add(filePath)
        }
        return files
    }

    fun getOriginalPathForFile(name:String):String{
        return "${getOriginalPath()}/$name"
    }

    fun removeFile(path: String){
        File(path).also { file ->
            if (file.exists()){
                file.delete()
            }
        }
    }

}