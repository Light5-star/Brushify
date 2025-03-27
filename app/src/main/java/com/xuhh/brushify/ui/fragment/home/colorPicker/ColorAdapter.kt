package com.xuhh.brushify.ui.fragment.home.colorPicker

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xuhh.brushify.R
import com.xuhh.brushify.databinding.ColorItemLayoutBinding
import com.xuhh.brushify.ui.fragment.home.view.BroadCastCenter
import com.xuhh.brushify.viewModel.HomeViewModel

class ColorAdapter: RecyclerView.Adapter<ColorAdapter.MyViewHolder>() {
    private var colorList: List<Int> = emptyList()
    private val TYPE_ADD = 8888
    private val TYPE_DELETE = 88887
    private val TYPE_NORMAL = 6666
    var actionListener: (ItemAction) -> Unit = {}
    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val colorView: View = itemView.findViewById<View>(R.id.color_view)
        fun bind(color: Int,type: Int) {
            colorView.setBackgroundColor(color)
            itemView.setOnClickListener {
                when(type) {
                    TYPE_ADD -> {actionListener(ItemAction.ADD)}
                    TYPE_DELETE -> {actionListener(ItemAction.DELETE)}
                    TYPE_NORMAL -> {
                        HomeViewModel.instance().mColor = color
                        itemView.context.sendBroadcast(Intent(BroadCastCenter.TEXT_COLOR_CHANGE_NAME))
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        var view:View? = null
        when (viewType) {
            TYPE_ADD -> {
                view = layoutInflater.inflate(R.layout.color_item_add_layout,parent,false)
            }
            TYPE_DELETE -> {
                view = layoutInflater.inflate(R.layout.color_item_delete_layout,parent,false)
            }
            TYPE_NORMAL -> {
                view = layoutInflater.inflate(R.layout.color_item_layout,parent,false)
            }
        }
        return MyViewHolder(view!!)
    }

    override fun getItemCount(): Int {
        return colorList.size + 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_ADD
            1 -> TYPE_DELETE
            else -> TYPE_NORMAL
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        when(position){
            0 -> holder.bind(0,TYPE_ADD)
            1 -> holder.bind(1,TYPE_DELETE)
            else -> holder.bind(colorList[position-2],TYPE_NORMAL)
        }
    }

    fun setColors(colors: List<Int>) {
        colorList = colors
        notifyDataSetChanged()
    }

}