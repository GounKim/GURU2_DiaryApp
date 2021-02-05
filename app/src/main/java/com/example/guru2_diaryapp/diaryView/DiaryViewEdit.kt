package com.example.guru2_diaryapp.diaryView

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.example.guru2_diaryapp.MainActivity
import com.example.guru2_diaryapp.R

class DiaryViewEdit : AppCompatActivity() {
    lateinit var diary_et : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diary_view_edit)

        diary_et = findViewById(R.id.diary_et)

        loadDiary()
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

    // 뒤로가기 동작
    override fun onBackPressed() {
        svaeDiary(diary_et.text.toString())
        var intent = Intent(this, DiaryView::class.java)
        intent.putExtra("diary_content", diary_et.text.toString())
        startActivity(intent)
        Toast.makeText(this, "일기가 저장되었습니다.", Toast.LENGTH_SHORT).show()
        // 뒤로가는 동작이 되지 않으면 아래의 코드도 넣기
        //super.onBackPressed()
    }

    // 일기 내용 저장
    // 공유환경변수 사용
    private fun svaeDiary(content : String) {
        var pref = this.getPreferences(0)
        var editor = pref.edit()

        editor.putString("KEY_CONTENT", diary_et.text.toString()).apply()
    }

    // 일기 내용 불러오기
    private fun loadDiary() {
        var pref = this.getPreferences(0)
        var content = pref.getString("KEY_CONTENT", "")

        if(content != "") {
            diary_et.setText(content.toString())
        }
    }
}