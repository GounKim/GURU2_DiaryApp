package com.example.guru2_diaryapp.diaryView

import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.guru2_diaryapp.MainActivity
import com.example.guru2_diaryapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class DiaryViewEdit : AppCompatActivity() {
    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    private val REQUEST_CODE = 0
    lateinit var diary_et : EditText
    lateinit var diary_bnv : BottomNavigationView
    lateinit var image_preview : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diary_view_edit)

        diary_et = findViewById(R.id.diary_et)
        diary_bnv = findViewById(R.id.diary_bnv);
        image_preview = findViewById(R.id.image_preview)

        loadDiary()

        // 하단의 메뉴 선택될 때 호출될 리스너 등록
        diary_bnv.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item?.itemId) {
                R.id.mood -> {

                }
                R.id.weather -> {

                }
                R.id.current_time -> {

                }
                R.id.sticker -> {

                }
                R.id.image -> {
                    selectGallery()
                }
            }
            return@OnNavigationItemSelectedListener true
        })


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

    private fun selectGallery() {
        // 앨범 접근 권한
        var writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        var readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // 권한이 허용되지 않음
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // 이전에 이미 권한이 거부되었을 때 설명
                var dig = AlertDialog.Builder(this)
                dig.setTitle("권한이 필요한 이유")
                dig.setMessage("사진 정보를 얻기 위해서는 외부 저장소 권한이 필수로 필요합니다.")
                dig.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this@DiaryViewEdit,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
                }
                dig.setNegativeButton("취소", null)
                dig.show()
            } else {
                // 처음 권한 요청
                ActivityCompat.requestPermissions(this@DiaryViewEdit,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
            }
        } else {
            // 권한이 이미 허용됨
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_CODE)

        }
    }

    // 갤러리에서 사진 가져오기
    override protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.data?.let { uri ->
                    image_preview.setImageURI(uri)
                }!!
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
            }
        }
    }
}