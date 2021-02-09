package com.example.guru2_diaryapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewpager2.widget.ViewPager2

//타임라인뷰, 검색, 카테고리 내 일기 목록 출력에 사용할 recyclerView 의 어뎁터
class TimeLineRecyclerViewAdapter(var data:ArrayList<DiaryData>, val context: Context, var item: RecyclerView,
var itemClick:(DiaryData,Int)->Unit):
        Adapter<TimeLineRecyclerViewAdapter.ItemViewHolder>() {


    inner class ItemViewHolder(view: View, itemClick: (DiaryData, Int) -> Unit) : RecyclerView.ViewHolder(view) {

        var dateTv = view.findViewById<TextView>(R.id.tlitem_date_tv)
        var categoryTv = view.findViewById<TextView>(R.id.tlitem_catecgory_tv)
        var contentTv = view.findViewById<TextView>(R.id.tlitem_content_tv)
        var imgVp = view.findViewById<ViewPager2>(R.id.tlitem_pic_vp)

        fun bind(data: DiaryData, num: Int) {
            dateTv.text = data.reporting_date.toString()
            categoryTv.text = data.category_name
            contentTv.text = data.content
            itemView.setOnClickListener {
                itemClick(data, num)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineRecyclerViewAdapter.ItemViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.item_timeline_view, parent, false)
        return ItemViewHolder(view,itemClick)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(data[position], position)
    }
        override fun getItemCount(): Int {
            return data.size
        }

}

