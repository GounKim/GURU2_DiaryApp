package com.example.guru2_diaryapp

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru2_diaryapp.diaryView.DiaryView

class TimeLineView : AppCompatActivity() {
    //DB
    lateinit var myDBHelper: MyDBHelper
    lateinit var sqldb: SQLiteDatabase
    lateinit var cursor:Cursor
    var TimeLineData = ArrayList<DiaryData>()

    //View
    lateinit var timeline_rv: RecyclerView
    private var recyclerViewAdapter: TimeLineRecyclerViewAdapter?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_view)

        myDBHelper = MyDBHelper(this)
        timeline_rv = findViewById(R.id.timeLineRv)

            TimeLineData.addAll(PageDown(0))
            recyclerViewAdapter = TimeLineRecyclerViewAdapter(TimeLineData,this, timeline_rv){
                data, num ->  Toast.makeText(this,"인덱스:${num} data: ${data}",Toast.LENGTH_SHORT).show()
                var intent = Intent(this, com.example.guru2_diaryapp.diaryView.DiaryView::class.java)
                intent.putExtra("post_id",data.reporting_date)
                startActivity(intent)
            }
            timeline_rv.adapter = recyclerViewAdapter
            timeline_rv.layoutManager = LinearLayoutManager(this)


        //스크롤이 최하단에 도달했을 때 글 더 불러오기
        if(!timeline_rv.canScrollVertically(1)){

        }

    }


    //추가로 글 불러오기
    private fun PageDown(BottomPost:Int):ArrayList<DiaryData>{
        var mydiaryData = ArrayList<DiaryData>()
        sqldb = myDBHelper.readableDatabase
        cursor = sqldb.rawQuery("SELECT * FROM diary_posts LEFT OUTER JOIN diary_categorys" +
                " ON diary_posts.category_id = diary_categorys.category_id ORDER BY reporting_date DESC;",null)
        cursor.moveToPosition(BottomPost)
        var num = 0
        while (cursor.moveToNext() && num < 20) {
            val id = cursor.getInt(cursor.getColumnIndex("post_id"))
            val date =
                cursor.getInt(cursor.getColumnIndex("reporting_date"))
            val weather =
                    cursor.getInt(cursor.getColumnIndex("weather"))
            val category =
                cursor.getString(cursor.getColumnIndex("category_name"))
            val content =
                cursor.getString(cursor.getColumnIndex("content"))
            mydiaryData.add (DiaryData( id, date, weather, category, content, null))
            num++
        }
        sqldb.close()
        return mydiaryData
    }

    //사진 정보를 불러오는 메소드
    private fun loadImgs():ArrayList<String>{
        var imgs = ArrayList<String>()
        sqldb = myDBHelper.readableDatabase
        cursor = sqldb.rawQuery("SELECT * FROM diary_imgs;",null)
        while(cursor.moveToNext()){
            imgs.add(cursor.getString(cursor.getColumnIndex("img_dir")))
        }
        return imgs
    }

}

