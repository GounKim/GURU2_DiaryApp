package com.example.guru2_diaryapp.diaryView

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.guru2_diaryapp.DiaryData
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.MainActivity
import com.example.guru2_diaryapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class DiaryViewEdit : AppCompatActivity() {
    // 권한 관련 변수
    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    private val REQUEST_CODE = 0
    private val REQUEST_WEATHER_CODE = 1001
    private val REQUEST_GPS_CODE = 1002
    // 날씨 관련 변수
    private var lat : String = ""
    private var lon : String = ""
    private var apiID : String = "4dd8b7eb922a5d7e8da094cb922921f2"

    lateinit var myDBHelper:MyDBHelper
    lateinit var sqllitedb : SQLiteDatabase

    lateinit var toolbar:Toolbar

    lateinit var diary_et : EditText
    lateinit var diary_bnv : BottomNavigationView
    lateinit var image_preview : ImageView
    lateinit var date_tv : TextView
    lateinit var category_spinner : Spinner
    lateinit var current_weather : ImageView

    var currenturi:Uri?=null
    var postID : Int = 0
    var newDate : Int = 0
    var category_id : Int = 0
    var descWeather : String = ""
    var imgID :Int = -1

    // 일기 작성시 선택할 카테고리 배열
    var categories = ArrayList<Pair<Int,String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diary_view_edit)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        diary_et = findViewById(R.id.diary_et)
        diary_bnv = findViewById(R.id.diary_bnv)
        image_preview = findViewById(R.id.image_preview)
        date_tv = findViewById(R.id.date_tv)
        category_spinner = findViewById(R.id.category_spinner)
        current_weather = findViewById(R.id.current_weather)

        myDBHelper = MyDBHelper(this)

        // 카테고리 선택 관련
        categories = getCategoryName()
        var adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        // 미리 정의된 레이아웃 사용

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        category_spinner.adapter = adapter

        // DiaryView에서 postId 값 가져오기
        postID = intent.getIntExtra("postID", 0)
        newDate = intent.getIntExtra("newDate", -1)

        //글 가져오기
        if(postID > 0 ) {
            loadDiary()
        }

        date_tv.text = newDate.toString()


        // 하단의 메뉴 선택될 때 호출될 리스너 등록
        diary_bnv.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item?.itemId) {
                R.id.weather -> { // 현재 날씨 추가
                    weatherPermission()
                }
                R.id.current_time -> { // 현재 시간 추가
                    diary_et.append(getCurrentTime())
                }
                R.id.image -> { // 이미지 추가
                    selectGallery()
                }
            }
            return@OnNavigationItemSelectedListener true
        })
    }

    // 상단에 메뉴
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_top_diary_edit, menu)
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
            R.id.action_save -> {
                if (postID == -1) // 이전에 작성된 글이 없다면
                {
                    saveDiary() // 저장
                } else { // 작성된 글이 있다면
                    updateDiary() // 업데이트
                }
                Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // 뒤로가기 동작
    override fun onBackPressed() { // 일기 작성이 취소됩니다.
        var dig = AlertDialog.Builder(this)
        dig.setTitle("작성 취소")
        dig.setMessage("일기 작성을 취소하시겠습니까?")
        dig.setPositiveButton("확인") { dialog, which ->
            super.onBackPressed()
        }
        dig.setNegativeButton("취소", null)
        dig.show()

    }

    // 첫 작성 후 저장과 수정 후 저장에 따른 구분이 필요
    // 일기 내용 저장 => 일기 작성한 데이터를 함수 호출할 때 파라미터로 주면 함수 안쪽에서 저장 처리
    private fun saveDiary(){
        sqllitedb = myDBHelper.writableDatabase

        var reporting_date : Int = newDate
        var weather : Int = DiaryData().saveWeatherID(descWeather)
        var category_id : Int = 0
        var content = diary_et.text.toString()

        sqllitedb.execSQL("INSERT INTO diary_posts VALUES (null, $reporting_date, $weather, $category_id,'$content', null);")

    }

    // 일기 내용 불러오기
    private fun loadDiary() {
        sqllitedb = myDBHelper.readableDatabase
        var cursor = sqllitedb.rawQuery("SELECT * FROM diary_posts WHERE post_id =  $postID", null)

        if (cursor.moveToFirst()) { // 레코드가 비어있다면 false 반환

            val weather = cursor.getInt(cursor.getColumnIndex("weather")) // 날씨
            descWeather = DiaryData().setWeatherDesc(weather)
            DiaryData().setWeatherIcon(weather, current_weather)

            category_id = cursor.getInt(cursor.getColumnIndex("category_id"))
            diary_et.setText(cursor.getString(cursor.getColumnIndex("content"))) // 내용
            newDate = cursor.getInt(cursor.getColumnIndex("reporting_date"))
            date_tv.text = newDate.toString()

            try {

                val image : ByteArray? = cursor.getBlob(cursor.getColumnIndex("img_file")) ?: null
                if(image != null) {
                    val bitmap : Bitmap? = BitmapFactory.decodeByteArray(image, 0, image!!.size)
                    image_preview.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(this, "저장된 사진이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (rte : RuntimeException) {
                Toast.makeText(this, "사진을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        cursor.close()
        sqllitedb.close()
    }

    // 일기 내용 업데이트
    private fun updateDiary() {
        sqllitedb = myDBHelper.writableDatabase

        var reporting_date : Int = newDate
        var weather : Int = DiaryData().saveWeatherID(descWeather)
        var category_id : Int = categories[category_spinner.selectedItemPosition].first
        var content = diary_et.text.toString()
        val image = image_preview.drawable
        var byteArray : ByteArray ?= null

        try {
            // 이미지 파일을 Bitmap 파일로, Bitmap 파일을 byteArray로 변환시켜서 BLOB 형으로 DB에 저장
            val bitmapDrawable = image as BitmapDrawable?
            val bitmap = bitmapDrawable?.bitmap
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            byteArray = stream.toByteArray()
        } catch (cce: ClassCastException) { // 사진을 따로 저장안할 경우
            Log.d("image null", "이미지 저장 안함")
        }

        if(byteArray == null) { // 저장하려는 사진이 없을 경우
            sqllitedb.execSQL("UPDATE diary_posts SET reporting_date = $reporting_date, weather = $weather, " +
                    "category_id = $category_id, content = '$content', img_file = null WHERE post_id = $postID")
        } else { // 저장하려는 사진이 있는 경우
            var udtQuery : String = "UPDATE diary_posts SET reporting_date = $reporting_date, weather = $weather, " +
                    "category_id = $category_id, content = '$content', img_file = ? WHERE post_id = $postID"
            var stmt : SQLiteStatement = sqllitedb.compileStatement(udtQuery)
            stmt.bindBlob(1, byteArray)
            stmt.execute()
        }

    }

    // 갤러리
    private fun selectGallery() {
        // 앨범 접근 권한
        var readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

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
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                data?.data?.let { uri ->
                    image_preview.setImageURI(uri)
                    currenturi = uri
                }!!
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
            }
        }
    }

    // 날씨 관련 접근 권한
    private fun weatherPermission() {
        // 인터넷 권한
        var internetPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)

        if(internetPermission != PackageManager.PERMISSION_GRANTED) { // 권한이 허용되지 않은 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) { // 이전에 이미 권한이 거부되었을 때 설명
                var dig = AlertDialog.Builder(this)
                dig.setTitle("권한이 필요한 이유")
                dig.setMessage("현재 날씨 정보를 얻기 위해서는 인터넷 권한을 필요로 합니다.")
                dig.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this@DiaryViewEdit,
                        arrayOf(Manifest.permission.INTERNET),
                        REQUEST_WEATHER_CODE)
                }
                dig.setNegativeButton("취소", null)
                dig.show()
            } else {
                // 처음 권한 요청
                ActivityCompat.requestPermissions(this@DiaryViewEdit,
                    arrayOf(Manifest.permission.INTERNET),
                    REQUEST_WEATHER_CODE)
            }
        } else {
            // 인터넷 권한이 이미 허용됨
            // 위치 권한 + 현 위치 불러오기
            locationPermission()
        }
    }

    // 위치 관련 접근 권한
    private fun locationPermission(){
        // 위치 권한
        var finePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        var coarsePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if(finePermission != PackageManager.PERMISSION_GRANTED || coarsePermission != PackageManager.PERMISSION_GRANTED) { // 권한이 허용되지 않은 경우

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) { // 이전에 이미 권한이 거부되었을 때 설명

                var dig = AlertDialog.Builder(this)
                dig.setTitle("권한이 필요한 이유")
                dig.setMessage("현재 날씨 정보를 얻기 위해서는 위치 권한을 필요로 합니다.")
                dig.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this@DiaryViewEdit,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                        REQUEST_GPS_CODE)
                }
                dig.setNegativeButton("취소", null)
                dig.show()
            } else {
                // 처음 권한 요청
                ActivityCompat.requestPermissions(this@DiaryViewEdit,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_GPS_CODE)
            }
        } else {
            // 권한이 이미 허용됨
            // 위치 정보 업데이트
            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    location?.let {
                        //position = LatLng(it.latitude, it.longitude)
                        lat = it.latitude.toString()
                        lon = it.longitude.toString()
                        Log.d("lat and long", "update position : ${lat} and ${lon}")
                    }
                    // 현 위치의 현재 날씨
                    getCurrentWeather()
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }
            locationManager.requestLocationUpdates( // 위치 업데이트 관련
                LocationManager.GPS_PROVIDER,
                10000,
                1f,
                locationListener
            )
        }
    }

    // 위치 관련
    private val locationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    // 현재 날씨
    private fun getCurrentWeather() {
        var res: retrofit2.Call<JsonObject> = RetrofitClient
                .getInstance()
                .buildRetrofit()
                .getCurrentWeather(lat,lon,apiID) // avd로 실행할 경우 구글 본사가 현재 위치로 나타남

        res.enqueue(object: Callback<JsonObject> {

            override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable) {
                Log.d("weather", "Failure : ${t.message.toString()}")
                Toast.makeText(this@DiaryViewEdit, "날씨를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: retrofit2.Call<JsonObject>, response: Response<JsonObject>) {
                var jsonObj = JSONObject(response.body().toString())
                Log.d("weather", "Success :: $jsonObj")

                val jsonArray = jsonObj.getJSONArray("weather")
                val jsonObject = jsonArray.getJSONObject(0)
                descWeather = jsonObject.getString("description")

                if (descWeather == "clear sky") { // 맑은 하늘
                    Toast.makeText(this@DiaryViewEdit, "날씨 : 맑음", Toast.LENGTH_SHORT).show()
                    current_weather.setImageResource(R.drawable.sunny)
                } else if (descWeather == "mist") { // 안개
                    Toast.makeText(this@DiaryViewEdit, "날씨 : 안개", Toast.LENGTH_SHORT).show()
                    current_weather.setImageResource(R.drawable.mist)
                } else if (descWeather == "few clouds") { // 조금 흐림
                    Toast.makeText(this@DiaryViewEdit, "날씨 : 구름 조금", Toast.LENGTH_SHORT).show()
                    current_weather.setImageResource(R.drawable.few_cloud)
                } else if (descWeather == "broken clouds") { // 흩어진 구름
                    Toast.makeText(this@DiaryViewEdit, "날씨 : 구름 조금", Toast.LENGTH_SHORT).show()
                    current_weather.setImageResource(R.drawable.few_cloud)
                } else if (descWeather == "scattered clouds") { // 흩어진 구름
                    Toast.makeText(this@DiaryViewEdit, "날씨 : 구름 조금", Toast.LENGTH_SHORT).show()
                    current_weather.setImageResource(R.drawable.few_cloud)
                } else if (descWeather == "overcast clouds") { // 흐린 구름, 많은 구름
                    Toast.makeText(this@DiaryViewEdit, "날씨 : 흐림", Toast.LENGTH_SHORT).show()
                    current_weather.setImageResource(R.drawable.cloud)
                }else if (descWeather == "light rain") { // 약한 비
                    Toast.makeText(this@DiaryViewEdit, "날씨 : 약한 비", Toast.LENGTH_SHORT).show()
                    current_weather.setImageResource(R.drawable.light_rain)
                } else if (descWeather == "moderate rain") { // 비 - 보통
                    Toast.makeText(this@DiaryViewEdit, "날씨 : 비", Toast.LENGTH_SHORT).show()
                    current_weather.setImageResource(R.drawable.moderate_rain)
                } else if (descWeather == "heavy intensity rain") { // 강한 비
                    Toast.makeText(this@DiaryViewEdit, "날씨 : 강한 비", Toast.LENGTH_SHORT).show()
                    current_weather.setImageResource(R.drawable.heavy_rain)
                } else if (descWeather == "thunderstorm") { // 천둥번개
                    Toast.makeText(this@DiaryViewEdit, "날씨 : 천둥번개", Toast.LENGTH_SHORT).show()
                    current_weather.setImageResource(R.drawable.thunderstorm)
                }else if (descWeather == "snow"){ // 눈
                    Toast.makeText(this@DiaryViewEdit, "날씨 : 눈", Toast.LENGTH_SHORT).show()
                    current_weather.setImageResource(R.drawable.snow)
                } else {
                    Toast.makeText(this@DiaryViewEdit, "날씨 : 알 수 없음", Toast.LENGTH_SHORT).show()
                    current_weather.setImageResource(R.drawable.ic_baseline_refresh_24)
                }
            }
        })
    }

    // 현재 시간
    private fun getCurrentTime() : String {
        val now = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm", Locale.KOREA).format(now) // 시:분 형태

        return dateFormat
    }

    //카테고리 정보 불러오기
    private fun getCategoryName():ArrayList<Pair<Int,String>>{
        sqllitedb = myDBHelper.readableDatabase
        var myCategory = ArrayList<Pair<Int,String>>()
        var cursor:Cursor
        cursor = sqllitedb.rawQuery("SELECT * FROM diary_categorys;",null)
        while(cursor.moveToNext()) {
            var id = cursor.getInt(cursor.getColumnIndex("category_id"))
            var tab = cursor.getString(cursor.getColumnIndex("category_name"))
            myCategory.add(Pair(id, tab))
        }
        cursor.close()
        sqllitedb.close()
        return myCategory
    }
}