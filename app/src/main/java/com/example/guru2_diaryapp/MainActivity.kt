package com.example.guru2_diaryapp;

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.guru2_diaryapp.CalendarView.*
import com.example.guru2_diaryapp.TimeLine.TimeLineView
import com.example.guru2_diaryapp.Tracker.AddTrackerDialog
import com.example.guru2_diaryapp.Tracker.Tracker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
            CheckTrakerDialog.OnCompleteListener, SetMoodDialog.OnCompleteListener{

    // 화면
    lateinit var calendarView: MaterialCalendarView

    // 메뉴
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar:Toolbar

    // BottomSheetDialog (하단 슬라이드)
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var categoryLayout: LinearLayout
    lateinit var moodImage: ImageView
    lateinit var mainTrackerLayout: LinearLayout    // 트래커

    // DB
    lateinit var myDBHelper: MyDBHelper
    lateinit var sqldb:SQLiteDatabase

    // 일기로 전달될 날짜
    lateinit var selectDate : String
    var newDate : Int = 0
    var postID : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myDBHelper = MyDBHelper(this)

        calendarView = findViewById(R.id.calendarView)

        //툴바를 액션바로 설정
        toolbar = findViewById(R.id.Maintoolbar)
        setSupportActionBar(toolbar)

        // actionbar의 왼쪽에 버튼 추가
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_book_24)

        // 네비게이션 드로어 설정
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.naviView)

        navigationView.setNavigationItemSelectedListener(this)

        // BottomSheetDialog 연결
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.activity_main_bottom_sheet_dialog)

        categoryLayout = bottomSheetDialog.findViewById(R.id.categoryName)!!
        moodImage = bottomSheetDialog.findViewById<ImageView>(R.id.moodImage)!!
        mainTrackerLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.maintrackerLayout)!!


        // 달력 생성
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMaximumDate(CalendarDay.from(2000, 0, 1))
                .setMaximumDate(CalendarDay.from(2100, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit()
        calendarView.setCurrentDate(Date(System.currentTimeMillis()))
        calendarView.setDateSelected(Date(System.currentTimeMillis()),true)
        calendarView.addDecorator(SundDayDeco())
        calendarView.addDecorator(SaturdayDeco())
        calendarView.addDecorator(OnDayDeco(this))

        // 달력 Date 클릭시
        calendarView.setOnDateChangedListener { widget, date, selected ->

            var year = date.year
            var month = date.month + 1
            var day = date.day
            newDate = year * 10000 + month * 100 + day

            selectDate = "${year}.${month}.${day}.(${getDayName(year, month, day)})"

            sqldb = myDBHelper.readableDatabase
            var cursor: Cursor
            cursor = sqldb.rawQuery("SELECT * FROM diary_posts LEFT OUTER JOIN diary_categorys " +
                    "ON diary_posts.category_id = diary_categorys.category_id WHERE reporting_date =  $newDate", null)

             //SELECT (얻을 컬럼) FROM 테이블명1 INNER JOIN 테이블명2 ON (조인 조건);

            // 그전에 만들어진 category view들 없애기
            categoryLayout.removeAllViews()
            val categories = ArrayList<TextView>()
            val postIds = ArrayList<Int>()
            var i : Int = 0

            if(cursor.moveToFirst()) { // 작성된 글이 하나 이상일때
                i = 0
                do{
                    var categoryText = cursor.getString(cursor.getColumnIndex("category_name"))
                    postID = cursor.getInt(cursor.getColumnIndex("post_id"))
                    postIds.add(postID)

                    categoryLayout.visibility = View.VISIBLE
                    //moodImage.visibility = View.GONE

                    val category = TextView(this)
                    category.text = categoryText
                    category.gravity = 1
                    categoryLayout.addView(category,0)
                    categories.add(category)
                    i++
                } while(cursor.moveToNext())
            } else { // 작성된 글이 없을때
                categoryLayout.visibility == View.VISIBLE
                moodImage.visibility = View.VISIBLE
                val text = TextView(this)
                text.text = "저장된 일기가 없습니다."
                text.gravity = 1 // 글을 중앙에 배치
                categoryLayout.addView(text)

                categoryLayout.setOnClickListener {
                    val intent = Intent(this, com.example.guru2_diaryapp.diaryView.DiaryView::class.java)
                    intent.putExtra("newDate", newDate) // 날짜 넘겨주기
                    startActivity(intent)
                }
            }

            for (x in 0..i-1) {
                categories[x].setOnClickListener() {
                    val intent = Intent(this, com.example.guru2_diaryapp.diaryView.DiaryView::class.java)
                    intent.putExtra("postID", postIds[x])
                    startActivity(intent)
                }
            }

            // 트래커 영역
            cursor = sqldb.rawQuery("SELECT * FROM habit_lists;", null)

            mainTrackerLayout.removeAllViews()

            var habitBtns = ArrayList<Button>()
            var countBtn = 0
            var habit : String = ""

            while (cursor.moveToNext()) {
                habit = cursor.getString(cursor.getColumnIndex("habit")).toString()

                if (habit != "mood") {  // 무드아닌 habit 버튼 생성
                    var btnHabbit: Button = Button(this)
                    btnHabbit.text = habit
                    var lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100)
                    lp.setMargins(0, 0, 0, 10)
                    btnHabbit.layoutParams = lp

                    changeButton(btnHabbit, habit, newDate) // db정보에 맞게
                    mainTrackerLayout.addView(btnHabbit)

                    habitBtns.add(btnHabbit)
                    countBtn++

                    /*btnHabbit.setOnClickListener {  // 버튼 클릭시
                        show(btnHabbit, habit, newDate) // 달성치 사용자입력받기
                    }*/
                }
            }
            for (x in 0..countBtn-1) {
                habitBtns[x].setOnClickListener() {
                    show(habitBtns[x], habit, newDate) // 달성치 사용자입력받기
                }
            }


            // mood 추가
            cursor = sqldb.rawQuery("SELECT check_result FROM habit_check_lists WHERE reporting_date = '$newDate';", null)

            if (cursor.moveToFirst()) {
                var moodCheck = cursor.getInt(0)
                setMoodImage(moodCheck)
            }
            moodImage.setOnClickListener {
                setMoodShow(newDate)
            }

            cursor.close()
            sqldb.close()

            bottomSheetDialog.show()
        }
    }

    // 옵션메뉴 생성(타임라인)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.to_timeline_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {   // 타임라인메뉴 선택시
            R.id.action_toTimeLine -> {
                val intent = Intent(this, TimeLineView::class.java)
                startActivity(intent)
                return true
            }
            else -> {   // 드로어 메뉴 선택시
                drawerLayout.openDrawer(GravityCompat.START)
                return super.onOptionsItemSelected(item)
            }
        }
    }

    // 드로어 메뉴의 메뉴 선택시
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.nav_category -> {  // 카태고리
                val intent = Intent(this, com.example.guru2_diaryapp.category.CategoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_tracker -> {   // 트래커
                val intent = Intent(this, Tracker::class.java)
                startActivity(intent)
            }
            R.id.nav_search -> {    // 검색
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {  // 설정
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawers()
        return true
    }

    // 뒤로가기 버튼 클릭
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers()
        }
        else {
            super.onBackPressed()
        }
    }

    // 요일 구하기
    fun getDayName(year : Int, month : Int, day : Int): String {
        val str_day = arrayOf("일", "월", "화", "수", "목", "금", "토")
        var month_day = Array<Int>(12) {31}
        var total_day = 0

        // 년
        if((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
            month_day[1] = 29
        } else {
            month_day[1] = 28
        }
        month_day[3] = 30
        month_day[5] = 30
        month_day[8] = 30
        month_day[10] = 30

        // 월
        for(i in 1..month-1 step 1) {
            total_day += month_day[i-1]
        }

        // 일
        total_day += day - 1;

        var answer_day = (5 + total_day) % 7

        return str_day[answer_day]
    }

    // 트래커 habit 달성도 채크 선택창(dialog) 띄우기
    private fun show(btn: Button, habit: String, newDate: Int) {
        val newFragment = CheckTrakerDialog(btn, habit, newDate)
        newFragment.show(supportFragmentManager,"dialog")
    }

    // 사용자가 클릭한 habit 달성도 db반영
    override fun onInputedData(habitLevel: Int, button: Button, habit: String, newDate: Int) {
        sqldb = myDBHelper.writableDatabase
        val cursor: Cursor
        cursor = sqldb.rawQuery("SELECT habit FROM habit_check_lists WHERE reporting_date = '$newDate' AND habit = '$habit';",null)
        if (!cursor.moveToFirst()) { // 레코드가 비어있다면
            sqldb.execSQL("INSERT INTO habit_check_lists VALUES($newDate, '$habit', $habitLevel);") // 새로 추가
        } else {
            sqldb.execSQL("UPDATE habit_check_lists SET check_result = $habitLevel")
        }
        changeButton(button, habit, newDate)
        cursor.close()
    }

    // db의 habit달성도를 버튼색으로 반영
    fun changeButton(button: Button, habit: String, newDate: Int) {
        val cursor: Cursor
        cursor = sqldb.rawQuery("SELECT * FROM habit_check_lists WHERE reporting_date = '$newDate';", null)

        while(cursor.moveToNext()) {
            var str_habit = cursor.getString(cursor.getColumnIndex("habit"))

            if (str_habit == habit) {
                when (cursor.getString(cursor.getColumnIndex("check_result")).toInt()) {
                    1 -> button.setBackgroundResource(R.drawable.button_bad)
                    2 -> button.setBackgroundResource(R.drawable.button_soso)
                    3 -> button.setBackgroundResource(R.drawable.button_good)
                }
                //cursor.moveToLast()
                break
            }
        }

        cursor.close()
    }

    // mood 설정
    private fun setMoodShow(newDate: Int) {
        val newFragment = SetMoodDialog(newDate)
        newFragment.show(supportFragmentManager,"dialog")
    }
    override fun onInputedData(mood: Int, newDate: Int) {
        sqldb = myDBHelper.writableDatabase
        val cursor: Cursor
        cursor = sqldb.rawQuery("SELECT check_result FROM habit_check_lists WHERE habit = 'mood' AND reporting_date = '$newDate';",null)

        if (!cursor.moveToFirst()) {
            sqldb.execSQL("INSERT INTO habit_check_lists VALUES($newDate, 'mood', $mood);")
        }
        cursor.close()
    }

    // moodImage를 mood값에 맞게 설정
    fun setMoodImage(mood: Int) {
        when (mood) {
            0 -> moodImage.setImageResource(R.drawable.ic__mood_add)
            1 -> moodImage.setImageResource(R.drawable.ic_mood_bad_main)
            2 -> moodImage.setImageResource(R.drawable.ic_mood_soso_main)
            3 -> moodImage.setImageResource(R.drawable.ic_mood_good_main)
            4 -> moodImage.setImageResource(R.drawable.ic_mood_sick_main)
            5 -> moodImage.setImageResource(R.drawable.ic_mood_surprise_main)
        }
    }

    fun nullIdPostDelete() {
        //아이디값이 null인 데이터가 있다면 삭제(오류로 생성된 레코드)
        sqldb = myDBHelper.writableDatabase
        var cursor: Cursor
        cursor = sqldb.rawQuery("SELECT post_id FROM diary_posts WHERE post_id IS NULL;", null)

        if (cursor.count >= 1) {
            sqldb.execSQL("DELETE FROM diary_posts WHERE post_id IS NULL;")
            Log.d("db", "오류 검사를 마쳤습니다.")
        }
        sqldb.close()
    }
}