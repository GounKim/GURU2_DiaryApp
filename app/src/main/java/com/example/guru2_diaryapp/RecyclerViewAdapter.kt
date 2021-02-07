package com.example.guru2_diaryapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

//타임라인뷰, 검색, 카테고리 내 일기 목록 출력에 사용할 recyclerView 의 어뎁터
class RecyclerViewAdapter(var data:ArrayList<DiaryData>, val context: Context, var item: RecyclerView):
        Adapter<RecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view:View): RecyclerView.ViewHolder(view) {

        var dateTv = view.findViewById<TextView>(R.id.tlitem_date_tv)
        var categoryTv = view.findViewById<TextView>(R.id.tlitem_catecgory_tv)
        var contentTv = view.findViewById<TextView>(R.id.tlitem_content_tv)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_timeline_view, parent,false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.dateTv.text = data[position].reporting_date.toString()
            holder.categoryTv.text = data[position].category_name
            holder.contentTv.text = data[position].content

    }

    override fun getItemCount(): Int {
        return data.size
    }

}