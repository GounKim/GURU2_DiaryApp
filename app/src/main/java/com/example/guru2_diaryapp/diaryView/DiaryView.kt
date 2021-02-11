package com.example.guru2_diaryapp.diaryView

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
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
    lateinit var linearLayout: LinearLayout

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
        linearLayout = findViewById(R.id.diary_linear)

        myDBHelper = MyDBHelper(this)

        // 달력에서 선택한 날짜 받아오기
        newDate = intent.getIntExtra("newDate", 0)
        date_tv.text = intent.getStringExtra("select_date")
        postID = intent.getIntExtra("postID", -1)

        if(postID != -1)
        {
            loadDiary()
        }

        // 일기 편집 화면으로 이동
        linearLayout.setOnClickListener {
            val intent = Intent(this, DiaryViewEdit::class.java)
            intent.putExtra("postID", postID)
            intent.putExtra("newDate", newDate)
            intent.putExtra("select_date", date_tv.text.toString()) // 날짜 넘겨주기
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
            // 메인 화면으로 이동
            R.id.action_main -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.action_delete -> {
                if(postID != -1) {
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
                } else {
                    Toast.makeText(this, "삭제할 일기가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun loadDiary() {
        sqllitedb = myDBHelper.readableDatabase
        var cursor : Cursor = sqllitedb.rawQuery("SELECT * FROM diary_posts WHERE post_id =  $postID;", null)

        if(cursor.moveToFirst()) {
            //postID = cursor.getInt(cursor.getColumnIndex("post_id"))

            /*val date = cursor.getInt(cursor.getColumnIndex("reporting_date"))
            val year = date / 10000
            val month = (date % 10000) / 100
            val day = date / 1000000
            date_tv.text = "${year}.${month}.${day}.(${MainActivity().getDayName(year, month, day)})"*/

            val weather = cursor.getInt(cursor.getColumnIndex("weather"))
            DiaryData().loadWeatherIcon(weather, current_weather)

            val category = cursor.getInt(cursor.getColumnIndex("category_id"))
            current_category.text = DiaryData().loadCategoryName(category)

            diary_tv.text = cursor.getString(cursor.getColumnIndex("content"))
        }
        cursor = sqllitedb.rawQuery("SELECT * FROM diary_imgs WHERE post_id =  $postID;", null)

        if(cursor.moveToFirst())
        {
            do {
                val imgID = cursor.getInt(cursor.getColumnIndex("img_id"))
                val image : ByteArray? = cursor.getBlob(cursor.getColumnIndex("img_file")) ?: null
                val bitmap : Bitmap? = BitmapFactory.decodeByteArray(image, 0, image!!.size)

                diary_image.setImageBitmap(bitmap)
            } while(cursor.moveToNext()) // 사진 여러장 넣기 위해 while문 만들어둠
        }
        sqllitedb.close()
    }

    fun deleteDiary() {
        sqllitedb = myDBHelper.writableDatabase
        sqllitedb.execSQL("DELETE FROM diary_posts WHERE post_id = $postID;")
        // 해당 글에 해당하는 사진들을 모두 지우기
        sqllitedb.execSQL("DELETE FROM diary_imgs WHERE post_id = $postID;")
    }
}

