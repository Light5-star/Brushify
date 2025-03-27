package com.xuhh.brushify.ui.fragment.home.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xuhh.brushify.R
import com.xuhh.brushify.ui.fragment.home.colorPicker.ColorAdapter
import com.xuhh.brushify.ui.fragment.home.colorPicker.ColorPickerView
import com.xuhh.brushify.ui.fragment.home.colorPicker.ItemAction
import com.xuhh.brushify.ui.fragment.home.colorPicker.getDefaultColorData

class HSVColorPickerView(
    context: Context,
    attrs: AttributeSet? = null,
):FrameLayout(context, attrs) {
    private lateinit var mColorPickerView: ColorPickerView
    private lateinit var mStaturationBar: AppCompatSeekBar
    private lateinit var mLightnessBar: AppCompatSeekBar
    private lateinit var mTVLightness: TextView
    private lateinit var mTVSaturation: TextView
    private lateinit var mRecyclerView: RecyclerView
    private val mColorAdapter = ColorAdapter()
    private val mColorList: ArrayList<Int> = arrayListOf()
    var pickColorCallbaack: (Int) -> Unit = {}
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mColorPickerView = findViewById<ColorPickerView>(R.id.pickerView)
        mStaturationBar = findViewById<AppCompatSeekBar>(R.id.saturationBar)
        mLightnessBar = findViewById<AppCompatSeekBar>(R.id.lightnessBar)
        mTVLightness = findViewById<TextView>(R.id.tvLightness)
        mTVSaturation = findViewById<TextView>(R.id.tvSaturation)
        mRecyclerView = findViewById<RecyclerView>(R.id.colorRecyclerView)

        //设置颜色选择器
        mColorPickerView.addPickColorListener { color ->
            pickColorCallbaack(color)
        }
        //设置饱和度
        mStaturationBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mTVSaturation.text = "$progress"
                mColorPickerView.setSaturation(progress/100f)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        //设置亮度
        mLightnessBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mTVLightness.text = "$progress"
                mColorPickerView.setLightness(progress/100f)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        mRecyclerView.adapter = mColorAdapter
        mRecyclerView.layoutManager = GridLayoutManager(context,5, RecyclerView.VERTICAL,false).apply {

        }

        mColorList.addAll(getDefaultColorData())
        mColorAdapter.setColors(mColorList)

        mColorAdapter.actionListener = { itemAction ->
            if (itemAction == ItemAction.ADD){
                mColorList.add(0,mColorPickerView.getCurrentColor())
            }else{
                if(mColorList.isNotEmpty()){
                    mColorList.removeFirst()
                }
            }
            //刷新
            mColorAdapter.setColors(mColorList)
        }
    }
}