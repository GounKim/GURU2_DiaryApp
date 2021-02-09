package com.example.guru2_diaryapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView

class TimeLineView : AppCompatActivity() {
    lateinit var myDBHelper: MyDBHelper
    lateinit var sqldb:SQLiteDatabase

    lateinit var rvAdapter: RecyclerViewAdapter

    lateinit var timeline_rv:RecyclerView

    //데이터를 db에서 불러오기 위한 리스트
    lateinit var diaryData:ArrayList<DiaryData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_view)

        timeline_rv = findViewById(R.id.timeLineRv)

        var cursor:Cursor
        var sql:String

        //게시글 테이블과 카테고리 테이블을 왼쪽 외부 조인해 조회한다.
        sql = "SELECT * FROM diary_posts LEFT OUTER JOIN diary_categorys"
        sql += " ON diary_posts.category_id = diary_categorys.category_id ORDER BY reporting_date DESC;"

        myDBHelper = MyDBHelper(this)
        sqldb = myDBHelper.readableDatabase
        cursor = sqldb.rawQuery(sql,null)

        
        //순서대로 커서를 가리키며 출력
        var num = 0
        while(cursor.moveToNext()){
            diaryData[num].reporting_date =
                    cursor.getInt(cursor.getColumnIndex("reporting_date"))
            diaryData[num].category_name =
                    cursor.getString(cursor.getColumnIndex("category_name"))
            diaryData[num].content =
                    cursor.getString(cursor.getColumnIndex("content"))
            num++
        }

    }
}