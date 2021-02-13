package com.example.guru2_diaryapp.diaryView

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2_diaryapp.DiaryData
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.MainActivity
import com.example.guru2_diaryapp.R
import java.time.LocalDate
import java.time.LocalDateTime


class DiaryView : AppCompatActivity() {
    lateinit var diary_tv : TextView
    lateinit var diary_image : ImageView
    lateinit var date_tv : TextView
    lateinit var current_category : TextView
    lateinit var current_weather : ImageView

    lateinit var myDBHelper:MyDBHelper
    lateinit var sqllitedb : SQLiteDatabase
    var postID:Int = -1
    var newDate : Int = 0

    lateinit var toolbar:androidx.appcompat.widget.Toolbar

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diary_view)

        //툴바 장착
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        diary_tv = findViewById(R.id.diary_tv)
        diary_image = findViewById(R.id.diary_image)
        date_tv = findViewById(R.id.date_tv)
        current_category = findViewById(R.id.current_category)
        current_weather = findViewById(R.id.current_weather)

        myDBHelper = MyDBHelper(this)

        // 달력에서 새 일기 작성 날짜와 수정할 일기 번호 받아오기
        newDate = intent.getIntExtra("newDate", 0)
        if (newDate > 0 ) date_tv.text = newDate.toString()
        else date_tv.text = LocalDateTime.now().toString()

        postID = intent.getIntExtra("postID", -1)

        //작성한 글이 있다면 불러온다.
        if(postID > 0) loadDiary()

        diary_tv.setOnClickListener {
            val intent = Intent(this, DiaryViewEdit::class.java)

            //저장된 글이 없을 경우
            if(postID <= 0){
                intent.putExtra("newDate", newDate)
                startActivity(intent)

             //저장된 글이 있을 경우
            }else if (postID > 0){
                intent.putExtra("postID",postID)
                startActivity(intent)
            }
        }

    }

    // 상단에 메뉴
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_top_diary_view, menu)
        return true
    }

    // 상단 메뉴 작동
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.action_main -> { // 메인 화면으로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.action_delete -> { // 글 삭제 버튼
                if(postID > 0) { // 빈 글이 아니라면
                    var dig = AlertDialog.Builder(this)
                    dig.setTitle("삭제 메시지")
                    dig.setMessage("해당 일기를 삭제하시겠습니까?")
                    dig.setPositiveButton("확인") { dialog, which ->
                        deleteDiary()
                        Toast.makeText(this, "일기가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        this.finish()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dig.setNegativeButton("취소", null)
                    dig.show()
                } else { // 빈 글이라면
                    Toast.makeText(this, "삭제할 일기가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun loadDiary() { // DB에서 데이터 가져오기
        sqllitedb = myDBHelper.readableDatabase

        var cursor = sqllitedb.rawQuery("SELECT * FROM diary_posts LEFT OUTER JOIN diary_categorys " +
                "ON diary_posts.category_id = diary_categorys.category_id WHERE post_id = $postID", null)

        if(cursor.moveToFirst()) {

                //날씨 아이콘 세팅
                val weather = cursor.getInt(cursor.getColumnIndex("weather"))
                DiaryData().setWeatherIcon(weather, current_weather)

                date_tv.text = cursor.getInt(cursor.getColumnIndex("reporting_date")).toString()
                current_category.text = cursor.getString(cursor.getColumnIndex("category_name"))
                diary_tv.text = cursor.getString(cursor.getColumnIndex("content"))

            try {
                val image : ByteArray? = cursor.getBlob(cursor.getColumnIndex("img_file")) ?: null
                val bitmap : Bitmap? = BitmapFactory.decodeByteArray(image, 0, image!!.size)
                diary_image.setImageBitmap(bitmap)
            } catch (rte : RuntimeException) {
                Toast.makeText(this, "사진을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        cursor.close()
        sqllitedb.close()
    }

    fun deleteDiary() { // 다이어리 삭제
        sqllitedb = myDBHelper.writableDatabase
        sqllitedb.execSQL("DELETE FROM diary_posts WHERE post_id = $postID;")
    }
}