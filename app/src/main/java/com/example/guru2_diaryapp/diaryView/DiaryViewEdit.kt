package com.example.guru2_diaryapp.diaryView

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.guru2_diaryapp.MainActivity
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.ByteArrayOutputStream


class DiaryViewEdit : AppCompatActivity() {
    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    private val REQUEST_CODE = 0

    lateinit var myDBHelper:MyDBHelper
    lateinit var sqllitedb : SQLiteDatabase
    lateinit var diary_et : EditText
    lateinit var diary_bnv : BottomNavigationView
    lateinit var image_preview : ImageView
    lateinit var date_tv : TextView
    lateinit var category_spinner : Spinner
    lateinit var selected_category : String
    lateinit var currenturi:Uri

    var newDate : Int = 0
    // 일기 작성시 선택할 카테고리 배열
    val categories = arrayOf("일기", "여행", "교환일기")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diary_view_edit)

        diary_et = findViewById(R.id.diary_et)
        diary_bnv = findViewById(R.id.diary_bnv);
        image_preview = findViewById(R.id.image_preview)
        date_tv = findViewById(R.id.date_tv)
        category_spinner = findViewById(R.id.category_spinner)

        // 카테고리 선택 관련
        var adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        // 미리 정의된 레이아웃 사용
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        category_spinner.adapter = adapter

        // 달력에서 선택한 날짜 받아오기
        date_tv.text = intent.getStringExtra("select_date")
        newDate = intent.getIntExtra("newDate", 0)

        myDBHelper = MyDBHelper(this)

        // 일기에서 작성된 글을 가져오기
        var diary_text = intent.getStringExtra("diary_content")
        if(diary_text == null) { // 가져온 것이 아무것도 없다면

        }
        else {
            diary_et.setText(diary_text)
        }

        // 일기에 등록된 이미지 가져오기
        val byteArray = intent.getByteArrayExtra("diary_image")
        if(byteArray == null)
        {
            image_preview.visibility = View.INVISIBLE
        }
        else
        {
            image_preview.visibility = View.VISIBLE
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
            image_preview.setImageBitmap(bitmap);
        }




        //loadDiary()

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
        //svaeDiary(diary_et.text.toString())
        selected_category = categories[category_spinner.selectedItemPosition]

        // intent를 이용해서 Diary View에 내용 전달
        var intent = Intent(this, DiaryView::class.java)
        intent.putExtra("diary_content", diary_et.text.toString())
        intent.putExtra("selected_category", selected_category)
        intent.putExtra("select_date", date_tv.text.toString())
        intent.putExtra("newDate", newDate)

        // 선택한 이미지가 없다면
        if(image_preview.getDrawable() == null)
        {

        }
        // 선택한 이미지가 있다면
        else
        {
            val stream = ByteArrayOutputStream()
            val bitmap = (image_preview.getDrawable() as BitmapDrawable).bitmap
            val scale = (1024 / bitmap.width.toFloat())
            val image_w = (bitmap.width * scale).toInt()
            val image_h = (bitmap.height * scale).toInt()
            val resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true)
            resize.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray: ByteArray = stream.toByteArray()
            intent.putExtra("diary_image", byteArray)
        }

        startActivity(intent)
        Toast.makeText(this, "일기가 저장되었습니다.", Toast.LENGTH_SHORT).show()
        // 뒤로가는 동작이 되지 않으면 아래의 코드도 넣기
        //super.onBackPressed()
    }

    // 일기 내용 저장
    // 공유환경변수 사용 -> DB로 변경
    private fun svaeDiary(content : String) {
        /*var pref = this.getPreferences(0)
        var editor = pref.edit()

        editor.putString("KEY_CONTENT", diary_et.text.toString()).apply()*/

        sqllitedb = myDBHelper.writableDatabase
        /*sqllitedb.execSQL("INSERT INTO diary_posts VALUES ('"
                + diary_et')")*/

    }


    // 일기 내용 저장 => 일기 작성한 데이터를 함수 호출할 때 파라미터로 주면 함수 안쪽에서 저장 처리
    private fun saveDiary(content: String){

        myDBHelper = MyDBHelper(this)
        sqllitedb = myDBHelper.writableDatabase
        var reporting_date : String = date_tv.text.toString()
        var weather : Int = 0

        var category_id : Int = 0
        var content = diary_et.text.toString()

        var changeProfilePath = absolutelyPath(currenturi)
        sqllitedb.execSQL("INSERT INTO diary_posts VALUES (null,'$reporting_date,''$weather,''$category_id,''$content',''$changeProfilePath')")


        var intent = Intent(this, DiaryView::class.java)
        startActivity(intent)
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
        //var writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        var readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        //if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
        if(readPermission != PackageManager.PERMISSION_GRANTED) {
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
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE/*, Manifest.permission.WRITE_EXTERNAL_STORAGE*/), REQUEST_READ_EXTERNAL_STORAGE)
            }
        } else {
            // 권한이 이미 허용됨
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_CODE)
            image_preview.visibility = View.VISIBLE
        }
    }

    // 갤러리에서 사진 가져오기
    override protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                data?.data?.let { uri ->
                    image_preview.setImageURI(uri)
                    currenturi=uri
                }!!
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
            }
        }
    }

    // 절대경로 변환
    private fun absolutelyPath(path: Uri): String? {

        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? = contentResolver.query(path, proj, null, null, null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        if (c != null) {
            c.moveToFirst()
        }

        var result = index?.let { c?.getString(it) }

        return result
    }
}