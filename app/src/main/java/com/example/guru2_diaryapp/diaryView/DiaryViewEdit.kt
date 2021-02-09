package com.example.guru2_diaryapp.diaryView

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.MainActivity
import com.example.guru2_diaryapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


class DiaryViewEdit : AppCompatActivity() {
    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    private val REQUEST_CODE = 0
    private val REQUEST_WEATHER_CODE = 1001
    private val REQUEST_GPS_CODE = 1002
    private var lat : String = ""
    private var lon : String = ""
    private var apiID : String = "4dd8b7eb922a5d7e8da094cb922921f2"

    lateinit var myDBHelper:MyDBHelper
    lateinit var sqllitedb : SQLiteDatabase
    lateinit var diary_et : EditText
    lateinit var diary_bnv : BottomNavigationView
    lateinit var image_preview : ImageView
    lateinit var date_tv : TextView
    lateinit var category_spinner : Spinner
    lateinit var selected_category : String
    lateinit var current_weather : ImageView
    var currenturi:Uri?=null


    var newDate : Int = 0
    // 일기 작성시 선택할 카테고리 배열
    val categories = arrayOf("일기", "여행", "교환일기")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diary_view_edit)

        diary_et = findViewById(R.id.diary_et)
        diary_bnv = findViewById(R.id.diary_bnv)
        image_preview = findViewById(R.id.image_preview)
        date_tv = findViewById(R.id.date_tv)
        category_spinner = findViewById(R.id.category_spinner)
        current_weather = findViewById(R.id.current_weather)

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
                    weatherPermission()
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
    /*private fun svaeDiary(content : String) {
        /*var pref = this.getPreferences(0)
        var editor = pref.edit()

        editor.putString("KEY_CONTENT", diary_et.text.toString()).apply()*/

        sqllitedb = myDBHelper.writableDatabase
        /*sqllitedb.execSQL("INSERT INTO diary_posts VALUES ('"
                + diary_et')")*/

    }*/


    // 일기 내용 저장 => 일기 작성한 데이터를 함수 호출할 때 파라미터로 주면 함수 안쪽에서 저장 처리
    private fun saveDiary(content: String){

        myDBHelper = MyDBHelper(this)
        sqllitedb = myDBHelper.writableDatabase
        var reporting_date : String = date_tv.text.toString()
        var weather : Int = 0

        var category_id : Int = 0
        var content = diary_et.text.toString()

        val changeProfilePath = currenturi?.let { absolutelyPath(it) }
        sqllitedb.execSQL("INSERT INTO diary_posts VALUES (null,'$reporting_date,''$weather,''$category_id,''$content',''$changeProfilePath')")

    }

    // 일기 내용 불러오기
    private fun loadDiary() {
        var pref = this.getPreferences(0)
        var content = pref.getString("KEY_CONTENT", "")

        if(content != "") {
            diary_et.setText(content.toString())
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
            locationManager.requestLocationUpdates(
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

    private fun getCurrentWeather() {
        var res: Call<JsonObject> = RetrofitClient
            .getInstance()
            .buildRetrofit()
            .getCurrentWeather(lat,lon,apiID) // avd로 실행할 경우 구글 본사가 현재 위치로 나타남

        res.enqueue(object: Callback<JsonObject> {

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("weather", "Failure : ${t.message.toString()}")
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var jsonObj = JSONObject(response.body().toString())
                Log.d("weather", "Success :: $jsonObj")

                val jsonArray = jsonObj.getJSONArray("weather")
                val jsonObject = jsonArray.getJSONObject(0)
                val descWeather = jsonObject.getString("description")

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
}