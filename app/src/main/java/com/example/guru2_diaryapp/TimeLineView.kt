package com.example.guru2_diaryapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TimeLineView : AppCompatActivity() {
    //DB
    lateinit var myDBHelper: MyDBHelper
    lateinit var sqldb: SQLiteDatabase
    lateinit var cursor:Cursor
    lateinit var button: Button
    //View
    lateinit var timeline_rv: RecyclerView
    lateinit var linearLayoutManager:LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_view)

        myDBHelper = MyDBHelper(this)
        timeline_rv = findViewById(R.id.timeLineRv)

        var DiaryTimeLine = ArrayList<DiaryData>()
        DiaryTimeLine.add(DiaryData(20200202,"기본","이곳에 내용을 적습니다.",
                null))


        button = findViewById(R.id.button)
        button.setOnClickListener {
            DiaryTimeLine.addAll(PageDown(0))
            timeline_rv.adapter = TimeLineRecyclerViewAdapter(DiaryTimeLine,this,timeline_rv)
            timeline_rv.layoutManager = LinearLayoutManager(this)
        }



        //스크롤 아래쾅 리스너 -> 과거로


        //스크롤 위쾅 리스너 -> 현재로

    }

    //이미지 저장
    private fun ImgLoadFromDB(cursor:Cursor){

    }

    /* 계획:
    가장 아래에 로드된 데이터가 무엇인지 확인 후 그 이후로 50개를 arraylist로 불러온다.
    BottomPost = 현재 로딩된 데이터 중 가장 아래 데이터
    */

    //아래 스크롤 과거 글 불러오기
    private fun PageDown(BottomPost:Int):ArrayList<DiaryData>{
        var mydiaryData = ArrayList<DiaryData>()
        sqldb = myDBHelper.readableDatabase
        cursor = sqldb.rawQuery("SELECT * FROM diary_posts LEFT OUTER JOIN diary_categorys" +
                " ON diary_posts.category_id = diary_categorys.category_id ORDER BY reporting_date DESC;",null)

        cursor.moveToPosition(BottomPost)
        var num = 0
        while (cursor.moveToNext() && num < 20) {
            var date =
                cursor.getInt(cursor.getColumnIndex("reporting_date"))
            var category =
                cursor.getString(cursor.getColumnIndex("category_name"))
            var content =
                cursor.getString(cursor.getColumnIndex("content"))
            mydiaryData.add(DiaryData(date,category,content,null))
            num++
        }
        sqldb.close()
        return mydiaryData
    }

    //위 스크롤 (최신글 불러오기)
    private fun PageUp(topPostId:Int):ArrayList<DiaryData>{
        var mydiaryData = ArrayList<DiaryData>()
        sqldb = myDBHelper.readableDatabase
        cursor = sqldb.rawQuery("SELECT * FROM diary_posts LEFT OUTER JOIN diary_categorys" +
                " ON diary_posts.category_id = diary_categorys.category_id ORDER BY reporting_date DESC;",null)

        cursor.moveToPosition(topPostId)
        var num = 0
        while (cursor.moveToPrevious() && num < 20) {
            var date =
                    cursor.getInt(cursor.getColumnIndex("reporting_date"))
            var category =
                    cursor.getString(cursor.getColumnIndex("category_name"))
            var content =
                    cursor.getString(cursor.getColumnIndex("content"))
            mydiaryData.add(DiaryData(date,category,content,null))
            num++
        }
        sqldb.close()
        return mydiaryData
    }
}

//날짜, 카테고리명, 본문, 사진 정보 리스트
data class DiaryData(var reporting_date:Int,var category_name:String,var content:String,
                     var imgs:ArrayList<String>?)



