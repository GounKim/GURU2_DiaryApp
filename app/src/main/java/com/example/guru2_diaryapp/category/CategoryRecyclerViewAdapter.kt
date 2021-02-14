package com.example.guru2_diaryapp.category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guru2_diaryapp.DiaryData
import com.example.guru2_diaryapp.R

class CategoryRecyclerViewAdapter(var dataSet : ArrayList<DiaryData>, var context:Context,
                                  var item:RecyclerView, var itemClick:(Int, Int)->Unit)
    : RecyclerView.Adapter<CategoryRecyclerViewAdapter.MyViewHolder>() {

    var data = dataSet


    inner class MyViewHolder(View : View, itemClick:(Int, Int)->Unit) : RecyclerView.ViewHolder(View) {
        var diary_title = View.findViewById<TextView>(R.id.diary_title)
        var diary_img = View.findViewById<ImageView>(R.id.diary_img)

        fun bind( id:Int , position : Int) {
            diary_title.text = data[position].content

            //클릭 리스너를 달아준다.
            itemView.setOnClickListener {
                itemClick(id,position)
            }
        }
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : MyViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.category_recyclerview_item, parent, false)

        return MyViewHolder(view,itemClick)
    }

    override fun getItemCount() : Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position].id,position)
    }

    fun deleteItem (post:DiaryData){
        data.remove(post)
        notifyDataSetChanged()
    }

    fun addItem (post:DiaryData){
        data.add(post)
        notifyDataSetChanged()
    }

    fun pageDown (loadPosts:ArrayList<DiaryData>){
        data.addAll(loadPosts)
        notifyDataSetChanged()
    }
}