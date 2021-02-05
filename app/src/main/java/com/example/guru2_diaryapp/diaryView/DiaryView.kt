package com.example.guru2_diaryapp.diaryView

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2_diaryapp.MainActivity
import com.example.guru2_diaryapp.R


class DiaryView : AppCompatActivity() {
    lateinit var diary_tv : TextView
    lateinit var diary_image : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diary_view)

        diary_tv = findViewById(R.id.diary_tv)
        diary_image = findViewById(R.id.diary_image)

        // 편집화면에서 작성한 글을 가져오기
        var diary_text = intent.getStringExtra("diary_content")
        if(diary_text == "") { // 가져온 것이 아무것도 없다면
            diary_tv.text = "등록된 일기가 없습니다"
        }
        else {
            diary_tv.text = diary_text
        }

        // 편집화면에서 등록한 이미지 가져오기
        //val byteArray = intent.getByteArrayExtra("image")
        //val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        //diary_image.setImageBitmap(bitmap);

        // 일기 편집 화면으로 이동
        // 잘 작동되는지 테스트 필요
        diary_tv.setOnClickListener {
            val intent = Intent(this, DiaryViewEdit::class.java)
            startActivity(intent)
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
}