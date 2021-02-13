package com.example.guru2_diaryapp.TimeLine

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru2_diaryapp.DiaryData
import com.example.guru2_diaryapp.MainActivity
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.R
import com.example.guru2_diaryapp.diaryView.DiaryView

class TimeLineView : AppCompatActivity() {
    //DB
    lateinit var myDBHelper: MyDBHelper
    lateinit var sqldb: SQLiteDatabase
    lateinit var postCursor: Cursor
    var TimeLineData = ArrayList<DiaryData>()

    //View
    lateinit var timeline_rv: RecyclerView
    lateinit var recyclerViewAdapter: TimeLineRecyclerViewAdapter

    lateinit var toolbar:androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_view)

        //툴바 장착
        toolbar = findViewById(R.id.toolbar2)
        setSupportActionBar(toolbar)

        myDBHelper = MyDBHelper(this)
        timeline_rv = findViewById(R.id.timeLineRv)

            TimeLineData.addAll(PageDown())
            recyclerViewAdapter = TimeLineRecyclerViewAdapter(TimeLineData,this, timeline_rv){
                data, num ->  Toast.makeText(this,"ID: ${data.id}",Toast.LENGTH_SHORT).show()
                var intent = Intent(this, DiaryView::class.java)
                intent.putExtra("postID", data.id)
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
    private fun PageDown():ArrayList<DiaryData>{
        var mydiaryData = ArrayList<DiaryData>()
        sqldb = myDBHelper.readableDatabase
        postCursor = sqldb.rawQuery("SELECT * FROM diary_posts LEFT OUTER JOIN diary_categorys" +
                " ON diary_posts.category_id = diary_categorys.category_id ORDER BY reporting_date DESC;",null)

        if (postCursor.moveToFirst()) { // 저장된 글이 있다면
            var id : Int = 0
            var date : Int = 0
            var weather : Int = 0
            var category : String = ""
            var content : String = ""
            var bitmap : Bitmap? = null

            do {
                try {
                    id = postCursor.getInt(postCursor.getColumnIndex("post_id"))
                    date =
                            postCursor.getInt(postCursor.getColumnIndex("reporting_date"))
                    weather =
                            postCursor.getInt(postCursor.getColumnIndex("weather"))
                    category =
                            postCursor.getString(postCursor.getColumnIndex("category_name"))
                    content =
                            postCursor.getString(postCursor.getColumnIndex("content"))
                    val image : ByteArray? = postCursor.getBlob(postCursor.getColumnIndex("img_file")) ?: null
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
            } while (postCursor.moveToNext())
            sqldb.close()
        } else { // 저장된 글이 없다면
            Toast.makeText(this, "저장된 일기가 없습니다.", Toast.LENGTH_SHORT).show()
        }
        return mydiaryData
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val pos_id = recyclerViewAdapter.pos_id
        DeletePost(pos_id)

        //삭제 후 새로고침
        finish()
        startActivity(Intent(this, this::class.java))
        return true
    }

    // 상단에 메뉴
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_top_timeline, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item?.itemId) {
            R.id.action_go_home -> { // 메인 화면으로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.action_search ->{
                val intent = Intent(this,SearchActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}

