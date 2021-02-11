package com.example.guru2_diaryapp.category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guru2_diaryapp.R

class CategoryRecyclerViewAdapter(var dataSet : List<Int>) : RecyclerView.Adapter<CategoryRecyclerViewAdapter.MyViewHolder>() {
    inner class MyViewHolder(ItemView : View) : RecyclerView.ViewHolder(ItemView) {
        var diary_title_left : TextView = ItemView.findViewById(R.id.diary_title_left)

        fun bind(position : Int) {
            diary_title_left.text = "일기 ${2 * position + 1}"
        }
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : MyViewHolder {
        val context = parent.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.category_recyclerview_item, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount() : Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(position)
    }
}