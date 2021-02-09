package com.example.guru2_diaryapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewpager2.widget.ViewPager2

//타임라인뷰, 검색, 카테고리 내 일기 목록 출력에 사용할 recyclerView 의 어뎁터
class TimeLineRecyclerViewAdapter(var data:ArrayList<DiaryData>, val context: Context, var item: RecyclerView):
        Adapter<TimeLineRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view:View): RecyclerView.ViewHolder(view) , View.OnClickListener {

        var dateTv = view.findViewById<TextView>(R.id.tlitem_date_tv)
        var categoryTv = view.findViewById<TextView>(R.id.tlitem_catecgory_tv)
        var contentTv = view.findViewById<TextView>(R.id.tlitem_content_tv)
        var imgVp = view.findViewById<ViewPager2>(R.id.tlitem_pic_vp)

        override fun onClick(v: View?) {
            //인텐트로 가진 정보들 넘겨주기. 해당하는 글 보기로 이동
            TODO("Not yet implemented")
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineRecyclerViewAdapter.ItemViewHolder {
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