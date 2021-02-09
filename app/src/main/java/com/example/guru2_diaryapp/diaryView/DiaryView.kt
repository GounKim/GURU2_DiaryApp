package com.example.guru2_diaryapp.diaryView

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2_diaryapp.DiaryData
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.MainActivity
import com.example.guru2_diaryapp.R
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream


class DiaryView : AppCompatActivity() {
    lateinit var diary_tv : TextView
    lateinit var diary_image : ImageView
    lateinit var date_tv : TextView
    var newDate : Int = 0
    lateinit var current_category : TextView
    lateinit var current_weather : ImageView

    lateinit var myDBHelper:MyDBHelper
    lateinit var sqllitedb : SQLiteDatabase
    var postID : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diary_view)

        diary_tv = findViewById(R.id.diary_tv)
        diary_image = findViewById(R.id.diary_image)
        date_tv = findViewById(R.id.date_tv)
        current_category = findViewById(R.id.current_category)
        current_weather = findViewById(R.id.current_weather)

        myDBHelper = MyDBHelper(this)

        // 달력에서 선택한 날짜 받아오기
        //date_tv.text = intent.getStringExtra("select_date")
        newDate = intent.getIntExtra("newDate", 0)

        loadDiary()

        // 선택한 카테고리 가져오기
        /*var category_text = intent.getStringExtra("selected_category")
        if(category_text == null) { // 가져온 것이 없다면
            current_category.text = "카테고리"
        }
        else {
            current_category.text = category_text
        }

        // 편집화면에서 작성한 글을 가져오기
        var diary_text = intent.getStringExtra("diary_content")
        if(diary_text == null) { // 가져온 것이 아무것도 없다면
            diary_tv.text = "등록된 일기가 없습니다"
        }
        else {
            diary_tv.text = diary_text
        }

        // 편집화면에서 등록한 이미지 가져오기
        val byteArray = intent.getByteArrayExtra("diary_image")
        if(byteArray == null)
        {
            diary_image.visibility = View.INVISIBLE
        }
        else
        {
            diary_image.visibility = View.VISIBLE
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
            diary_image.setImageBitmap(bitmap);
        }*/


        // 일기 편집 화면으로 이동
        diary_tv.setOnClickListener {
            val intent = Intent(this, DiaryViewEdit::class.java)
            intent.putExtra("postID", postID)
            startActivity(intent)

            /*val intent = Intent(this, DiaryViewEdit::class.java)
            intent.putExtra("select_date", date_tv.text.toString())
            intent.putExtra("newDate", newDate)
            intent.putExtra("selected_category", current_category.toString())
            intent.putExtra("diary_content", diary_text)
            // 이미지가 없다면
            if(diary_image.getDrawable() == null)
            {

            }
            // 이미지가 있다면
            else
            {
                val stream = ByteArrayOutputStream()
                val bitmap = (diary_image.getDrawable() as BitmapDrawable).bitmap
                val scale = (1024 / bitmap.width.toFloat())
                val image_w = (bitmap.width * scale).toInt()
                val image_h = (bitmap.height * scale).toInt()
                val resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true)
                resize.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val byteArray: ByteArray = stream.toByteArray()
                intent.putExtra("diary_image", byteArray)
            }
            startActivity(intent)*/
        }


    }

    // 상단에 메뉴
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_top, menu)
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
        }

        return super.onOptionsItemSelected(item)
    }

    fun loadDiary() {
        sqllitedb = myDBHelper.readableDatabase
        val cursor : Cursor = sqllitedb.rawQuery("SELECT * FROM diary_posts WHERE reporting_date = '${newDate}';", null)

        cursor.moveToFirst()

        postID = cursor.getInt(cursor.getColumnIndex("post_id"))

        val date = cursor.getInt(cursor.getColumnIndex("reporting_date"))
        val year = date / 10000
        val month = (date % 10000) / 100
        val day = date / 1000000
        date_tv.text = "${year}.${month}.${day}.(${MainActivity().getDayName(year, month, day)})"

        val weather = cursor.getInt(cursor.getColumnIndex("weather"))
        DiaryData().loadWeatherIcon(weather, current_weather)

        val category = cursor.getInt(cursor.getColumnIndex("category_id"))
        current_category.text = DiaryData().loadCategoryName(category)

        diary_tv.text = cursor.getString(cursor.getColumnIndex("content"))

        sqllitedb.close()
    }
}

