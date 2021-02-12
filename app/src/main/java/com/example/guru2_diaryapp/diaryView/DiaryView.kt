package com.example.guru2_diaryapp.diaryView

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2_diaryapp.DiaryData
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.MainActivity
import com.example.guru2_diaryapp.R


class DiaryView : AppCompatActivity() {
    lateinit var diary_tv : TextView
    lateinit var diary_image : ImageView
    lateinit var date_tv : TextView
    lateinit var current_category : TextView
    lateinit var current_weather : ImageView

    lateinit var myDBHelper:MyDBHelper
    lateinit var sqllitedb : SQLiteDatabase
    var postID : Int = 0
    var newDate : Int = 0

    lateinit var toolbar:androidx.appcompat.widget.Toolbar

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

        // 달력에서 선택한 날짜 받아오기
        newDate = intent.getIntExtra("newDate", 0)
        date_tv.text = intent.getStringExtra("select_date")
        postID = intent.getIntExtra("postID", -1)

        if(postID != -1) // 작성된 글이 있다면
        {
            loadDiary() // 불러오기
        }

        // 일기 편집 화면으로 이동
        // 텍스트만 선택하니 이동이 잘안돼서 이미지 클릭도 추가했어요
        diary_image.setOnClickListener {
            val intent = Intent(this, DiaryViewEdit::class.java)
            intent.putExtra("postID", postID)
            intent.putExtra("newDate", newDate)
            intent.putExtra("select_date", date_tv.text.toString())
            startActivity(intent)
        }
        diary_tv.setOnClickListener {
            val intent = Intent(this, DiaryViewEdit::class.java)
            intent.putExtra("postID", postID)
            intent.putExtra("newDate", newDate)
            intent.putExtra("select_date", date_tv.text.toString())
            startActivity(intent)
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
            }
            R.id.action_delete -> { // 글 삭제 버튼
                if(postID != -1) { // 빈 글이 아니라면
                    var dig = AlertDialog.Builder(this)
                    dig.setTitle("삭제 메시지")
                    dig.setMessage("해당 일기를 삭제하시겠습니까?")
                    dig.setPositiveButton("확인") { dialog, which ->
                        deleteDiary()
                        Toast.makeText(this, "일기가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        this.finish()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
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
                "ON diary_posts.category_id = diary_categorys.category_id WHERE post_id =  $postID", null)

        if(cursor.moveToFirst()) {
            try {
                val weather = cursor.getInt(cursor.getColumnIndex("weather")) // 날씨
                DiaryData().setWeatherIcon(weather, current_weather)

                current_category.text = cursor.getString(cursor.getColumnIndex("category_name"))

                diary_tv.text = cursor.getString(cursor.getColumnIndex("content")) // 일기 내용


                val image : ByteArray? = cursor.getBlob(cursor.getColumnIndex("img_file")) ?: null
                val bitmap : Bitmap? = BitmapFactory.decodeByteArray(image, 0, image!!.size)

                diary_image.setImageBitmap(bitmap)
            } catch (rte : RuntimeException) {
                Toast.makeText(this, "사진을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        sqllitedb.close()
    }

    fun deleteDiary() { // 다이어리 삭제
        sqllitedb = myDBHelper.writableDatabase
        sqllitedb.execSQL("DELETE FROM diary_posts WHERE post_id = $postID;")
    }
}

