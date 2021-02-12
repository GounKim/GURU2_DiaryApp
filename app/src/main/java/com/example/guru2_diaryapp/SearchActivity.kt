package com.example.guru2_diaryapp

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru2_diaryapp.TimeLine.TimeLineRecyclerViewAdapter
import com.example.guru2_diaryapp.diaryView.DiaryView

class SearchActivity : AppCompatActivity() {

    //DB
    lateinit var myDBHelper: MyDBHelper
    lateinit var sqldb: SQLiteDatabase
    lateinit var cursor: Cursor
    var TimeLineData = ArrayList<DiaryData>()
    lateinit var searchKW:String

    //검색 옵션
    lateinit var search: SpannableStringBuilder

    //View
    lateinit var search_et:EditText
    lateinit var timeline_rv: RecyclerView
    lateinit var recyclerViewAdapter: TimeLineRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        myDBHelper = MyDBHelper(this)
        timeline_rv = findViewById(R.id.search_rv)
        search_et = findViewById(R.id.edtSearch)

        search_et.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                searchKW = search_et.text.toString()
                TimeLineData = PageDown(0)
                true
            }else{
                false
            }
        }

        recyclerViewAdapter = TimeLineRecyclerViewAdapter(TimeLineData,this, timeline_rv){
            data, num ->  Toast.makeText(this,"인덱스:${num} data: ${data}", Toast.LENGTH_SHORT).show()
            var intent = Intent(this, DiaryView::class.java)
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
    private fun PageDown(LastIndex:Int):ArrayList<DiaryData>{
        var mydiaryData = ArrayList<DiaryData>()
        sqldb = myDBHelper.readableDatabase
        cursor = sqldb.rawQuery("SELECT * FROM diary_posts LEFT OUTER JOIN diary_categorys" +
                " ON diary_posts.category_id = diary_categorys.category_id WHERE LIKE '%${searchKW}%' " +
                "ORDER BY reporting_date DESC;",null)
        cursor.moveToPosition(LastIndex)
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

            val img = cursor.getBlob(cursor.getColumnIndex("img_file"))
            mydiaryData.add (DiaryData( id, date, weather, category, content, null))
            num++
        }
        sqldb.close()
        return mydiaryData
    }

}