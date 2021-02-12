package com.example.guru2_diaryapp.TimeLine

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru2_diaryapp.DiaryData
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.R
import com.example.guru2_diaryapp.diaryView.DiaryView

class TimeLineView : AppCompatActivity() {
    //DB
    lateinit var myDBHelper: MyDBHelper
    lateinit var sqldb: SQLiteDatabase
    lateinit var cursor:Cursor
    var TimeLineData = ArrayList<DiaryData>()

    //View
    lateinit var timeline_rv: RecyclerView
    lateinit var recyclerViewAdapter: TimeLineRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_view)

        myDBHelper = MyDBHelper(this)
        timeline_rv = findViewById(R.id.timeLineRv)

            TimeLineData.addAll(PageDown(0))
            recyclerViewAdapter = TimeLineRecyclerViewAdapter(TimeLineData,this, timeline_rv){
                data, num ->  Toast.makeText(this,"인덱스:${num} data: ${data}",Toast.LENGTH_SHORT).show()
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

    //선택한 일기 삭제
    fun DeletePost(id:Int){
        sqldb = myDBHelper.writableDatabase
        sqldb.execSQL("DELETE FROM diary_posts WHERE post_id = $id")
        sqldb.close()
    }


    //추가로 글 불러오기
    private fun PageDown(BottomPost:Int):ArrayList<DiaryData>{
        var mydiaryData = ArrayList<DiaryData>()
        sqldb = myDBHelper.readableDatabase
        cursor = sqldb.rawQuery("SELECT * FROM diary_posts LEFT OUTER JOIN diary_categorys" +
                " ON diary_posts.category_id = diary_categorys.category_id ORDER BY reporting_date DESC;",null)
        cursor.moveToPosition(BottomPost)
        var num = 0
        while (cursor.moveToNext() && num < 50) {
            val id = cursor.getInt(cursor.getColumnIndex("post_id"))
            val date =
                cursor.getInt(cursor.getColumnIndex("reporting_date"))
            val weather =
                    cursor.getInt(cursor.getColumnIndex("weather"))
            val category =
                cursor.getString(cursor.getColumnIndex("category_name"))
            val content =
                cursor.getString(cursor.getColumnIndex("content"))

            val img = cursor.getString(cursor.getColumnIndex("img_file"))

            mydiaryData.add (DiaryData( id, date, weather, category, content, img))
            num++
        }
        sqldb.close()
        return mydiaryData
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val pos = recyclerViewAdapter?.pos
        val item = recyclerViewAdapter?.item[pos]
        val pos_id = recyclerViewAdapter?.pos_id
        DeletePost(pos_id)

        var intent:Intent = getIntent()
        finish()
        startActivity(intent)

        return true

    }
}

