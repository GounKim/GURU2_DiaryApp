package com.example.guru2_diaryapp.TimeLine

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru2_diaryapp.DiaryData
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.R
import com.example.guru2_diaryapp.diaryView.DiaryView

class SearchActivity : AppCompatActivity() {

    //DB
    lateinit var myDBHelper: MyDBHelper
    lateinit var sqldb: SQLiteDatabase
    lateinit var cursor: Cursor

    //타임라인
    var TimeLineData = ArrayList<DiaryData>()

    //검색 키워드
    lateinit var searchKW:String
    var flag:Boolean = false

    //View
    lateinit var search_v:EditText
    lateinit var timeline_rv: RecyclerView
    lateinit var recyclerViewAdapter: TimeLineRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        myDBHelper = MyDBHelper(this)
        timeline_rv = findViewById(R.id.search_rv)
        search_v = findViewById(R.id.search_v)

        search_v.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                searchKW = search_v.text.toString()
                TimeLineData.addAll(PageDown())

                //첫 검색이 아닐시 초기화
                if(flag) {
                    recyclerViewAdapter.notifyDataSetChanged()
                }
                recyclerViewAdapter = TimeLineRecyclerViewAdapter(TimeLineData,this, timeline_rv){
                    data, num ->
                    var intent = Intent(this, DiaryView::class.java)
                    intent.putExtra("postID",data.reporting_date)
                    startActivity(intent)
                }

                flag = true
                timeline_rv.adapter = recyclerViewAdapter
                timeline_rv.layoutManager = LinearLayoutManager(this)
                true
            }else{
                false
            }
        }

    }

    //선택한 일기 삭제
    fun DeletePost(id:Int){
        sqldb = myDBHelper.writableDatabase
        sqldb.execSQL("DELETE FROM diary_posts WHERE post_id = $id")
        sqldb.close()
    }

    //추가로 글 불러오기
    private fun PageDown():ArrayList<DiaryData>{
        var mydiaryData = ArrayList<DiaryData>()
        sqldb = myDBHelper.readableDatabase

        var sql = "SELECT * FROM diary_posts LEFT OUTER JOIN diary_categorys" +
                " ON diary_posts.category_id = diary_categorys.category_id WHERE diary_posts.content LIKE '%${searchKW}%' " +
                "ORDER BY reporting_date DESC;"
        cursor = sqldb.rawQuery(sql,null)

        if (cursor.moveToNext()) { // 저장된 글이 있다면
            var id : Int = 0
            var date : Int = 0
            var weather : Int = 0
            var category : String = ""
            var content : String = ""
            var bitmap : Bitmap? = null

            do {
                try {
                    id = cursor.getInt(cursor.getColumnIndex("post_id"))
                    date =
                            cursor.getInt(cursor.getColumnIndex("reporting_date"))
                    weather =
                            cursor.getInt(cursor.getColumnIndex("weather"))
                    category =
                            cursor.getString(cursor.getColumnIndex("category_name"))
                    content =
                            cursor.getString(cursor.getColumnIndex("content"))
                    val image : ByteArray? = cursor.getBlob(cursor.getColumnIndex("img_file")) ?: null
                    bitmap = BitmapFactory.decodeByteArray(image, 0, image!!.size)
                }
                catch (rte: RuntimeException) { // null 값이 있을 경우 exception
                    bitmap = null
                }
                if (bitmap != null ) { // 등록한 이미지가 있다면
                    mydiaryData.add (DiaryData( id, date, weather, category, content, bitmap))
                } else { // 등록한 이미지가 없다면
                    mydiaryData.add (DiaryData( id, date, weather, category, content, null))
                }
            } while (cursor.moveToNext())
            sqldb.close()
        } else { // 저장된 글이 없다면
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
        return mydiaryData
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val pos_id = recyclerViewAdapter.pos_id
        DeletePost(pos_id)
        var intent:Intent = getIntent()
        finish()
        startActivity(intent)

        return true
    }

}


