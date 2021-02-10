package com.example.guru2_diaryapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.GONE
import androidx.viewpager2.widget.ViewPager2

//타임라인뷰, 검색, 카테고리 내 일기 목록 출력에 사용할 recyclerView 의 adapter
//클릭 이벤트를 람다식으로 처리

class TimeLineRecyclerViewAdapter(var data:ArrayList<DiaryData>, val context: Context, var item: RecyclerView,
var itemClick:(DiaryData,Int)->Unit):
        Adapter<TimeLineRecyclerViewAdapter.ItemViewHolder>() {

    //뷰홀더 클래스 내부 클래스로 선언
    inner class ItemViewHolder(view: View, itemClick: (DiaryData, Int) -> Unit) : RecyclerView.ViewHolder(view) {

        var dateTv = view.findViewById<TextView>(R.id.tlitem_date_tv)
        var categoryTv = view.findViewById<TextView>(R.id.tlitem_catecgory_tv)
        var contentTv = view.findViewById<TextView>(R.id.tlitem_content_tv)
        var imgVp = view.findViewById<ViewPager2>(R.id.tlitem_pic_vp)
        //var weatherIorn = view.findViewById<ImageView>(R.id.iv_weather)

        //onBindViewHolder에서 호출할 bind 함수
        fun bind(data: DiaryData, num: Int) {
            dateTv.text = data.reporting_date.toString()
            categoryTv.text = data.category_name
            contentTv.text = data.content

            if (data.imgs != null){
                imgVp.setVisibility(View.VISIBLE)

            } else {
                imgVp.setVisibility(View.GONE)

            }
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
