package com.example.guru2_diaryapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TimeLineView : AppCompatActivity() {
    //DB
    lateinit var myDBHelper: MyDBHelper
    lateinit var sqldb: SQLiteDatabase
    lateinit var cursor:Cursor

    //View
    lateinit var timeline_rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_view)

        myDBHelper = MyDBHelper(this)
        timeline_rv = findViewById(R.id.timeLineRv)

        var TimeLineData = ArrayList<DiaryData>()

        for (i in 1..10) {
            TimeLineData.add(DiaryData(i,20200200+i, "test data",
                    "$i 번 일기 이곳에 내용을 적습니다.",null))
        }

            TimeLineData.addAll(PageDown(0))
            timeline_rv.adapter = TimeLineRecyclerViewAdapter(TimeLineData,this,timeline_rv)
            timeline_rv.layoutManager = LinearLayoutManager(this)


        //최하단에 도달했을 때 (과거 글 추가 로드)
        if(!timeline_rv.canScrollVertically(1)){

            for (i in 11..20) {
                TimeLineData.add(DiaryData(i,20200200+i, "test data",
                        "$i 번 일기 이곳에 내용을 적습니다.",null))

                timeline_rv.adapter = TimeLineRecyclerViewAdapter(TimeLineData,this,timeline_rv)
                timeline_rv.layoutManager = LinearLayoutManager(this)
                Log.d("timeline","load")

            }
        }

        timeline_rv.setOnClickListener{

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
            val category =
                cursor.getString(cursor.getColumnIndex("category_name"))
            val content =
                cursor.getString(cursor.getColumnIndex("content"))
            mydiaryData.add(DiaryData(id,date,category,content,null))
            num++
        }
        sqldb.close()
        return mydiaryData
    }

}

//날짜, 카테고리명, 본문, 사진 정보 리스트
data class DiaryData(var id:Int, var reporting_date:Int,var category_name:String,var content:String,
                     var imgs:ArrayList<String>?)



