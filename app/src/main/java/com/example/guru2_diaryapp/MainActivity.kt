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
import com.example.guru2_diaryapp.CalendarView.OnDayDeco
import com.example.guru2_diaryapp.CalendarView.SaturdayDeco
import com.example.guru2_diaryapp.CalendarView.SundDayDeco
import com.example.guru2_diaryapp.CalendarView.CheckTrakerDialog
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
            CheckTrakerDialog.OnCompleteListener, AddTrackerDialog.OnCompleteListener{

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
    lateinit var imgViewAdd: ImageView

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
        imgViewAdd = bottomSheetDialog.findViewById<ImageView>(R.id.imgViewAdd)!!



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
        //calendarView.addDecorator(MoodDeco())

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

            while (cursor.moveToNext()) {

                if(cursor != null){
                    categoryLayout.visibility = View.VISIBLE
                    var categoryText = cursor.getString(cursor.getColumnIndex("category_name")).toString()
                    val category = TextView(this)
                    category.text = categoryText
                    categoryLayout.addView(category,0)
                    moodImage.visibility = View.GONE
                }

                else{
                    categoryLayout.visibility == View.GONE
                    moodImage.visibility = View.VISIBLE
                }
            }

            // 트래커 영역
            cursor = sqldb.rawQuery("SELECT * FROM habit_check_lists WHERE reporting_date = '${newDate}';", null)

            mainTrackerLayout.removeAllViews()

            while (cursor.moveToNext()) {
                var habit = cursor.getString(cursor.getColumnIndex("habit")).toString()

                var btnHabbit: Button = Button(this)
                btnHabbit.text = habit
                var lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,100)
                lp.setMargins(0,0,0,10)
                btnHabbit.layoutParams = lp

                changeButton(btnHabbit, habit, newDate)
                mainTrackerLayout.addView(btnHabbit)
                btnHabbit.setOnClickListener {
                    show(btnHabbit, habit, newDate)
                }
            }

            imgViewAdd.setOnClickListener {
                addShow()
            }

            /*
            mood_weather_lists 테이블을 합쳤습니다. 맞게 수정해둘게요! 무드 부분도 주석처리 했습니다.
            cursor = sqldb.rawQuery("SELECT mood "
                                            + "FROM mood_weather_lists "
                                            + "WHERE reporting_date = '"+ newDate + "';", null)



            if (cursor.moveToFirst()) {
                var mood = cursor.getInt(0)
                if (mood == 0) {
                    moodImage.setImageResource(R.drawable.ic_mood_good)
                }
                else if (mood == 1) {
                    moodImage.setImageResource(R.drawable.ic_mood_bad)
                }
                else {
                    moodImage.setImageResource(R.drawable.ic_baseline_add_reaction_24)
                }
            }


             */

            cursor.close()
            sqldb.close()

            bottomSheetDialog.show()
        }

        categoryLayout.setOnClickListener() {

            val intent = Intent(this, com.example.guru2_diaryapp.diaryView.DiaryView::class.java)
            intent.putExtra("select_date", selectDate)
            intent.putExtra("newDate", newDate)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.to_timeline_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.action_toTimeLine -> {
                val intent = Intent(this, TimeLineView::class.java)
                startActivity(intent)
                return true
            }
            else -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.nav_category -> {
                val intent = Intent(this, com.example.guru2_diaryapp.category.CategoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_tracker -> {
                val intent = Intent(this, Tracker::class.java)
                startActivity(intent)
            }
            R.id.nav_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawers()
        return true
    }

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

    private fun show(btn: Button, habit: String, newDate: Int) {
        val newFragment = CheckTrakerDialog(btn, habit, newDate)
        newFragment.show(supportFragmentManager,"dialog")
    }

    override fun onInputedData(habitLevel: Int, button: Button, habit: String, newDate: Int) {
        sqldb = myDBHelper.writableDatabase
        Toast.makeText(this, "$newDate , $habit , $habitLevel", Toast.LENGTH_SHORT).show()
        sqldb.execSQL("UPDATE habit_check_lists SET check_result = $habitLevel WHERE reporting_date = '$newDate'AND habit = '$habit';")

        changeButton(button, habit, newDate)
    }

    fun changeButton(button: Button, habit: String, newDate: Int) {
        val cursor: Cursor
        cursor = sqldb.rawQuery("SELECT * FROM habit_check_lists WHERE reporting_date = '$newDate';", null)

        while(cursor.moveToNext()) {
            var str_habit = cursor.getString(cursor.getColumnIndex("habit"))

            if (str_habit == habit) {
                when (cursor.getString(cursor.getColumnIndex("check_result")).toInt()) {
                    0 -> button.setBackgroundResource(R.drawable.button_bad)
                    1 -> button.setBackgroundResource(R.drawable.button_soso)
                    2 -> button.setBackgroundResource(R.drawable.button_good)
                }
                cursor.moveToLast()
            }
        }

        cursor.close()
    }

    // 트래커 추가
    private fun addShow() {
        val newFragment = AddTrackerDialog()
        newFragment.show(supportFragmentManager,"dialog")
    }
    override fun onInputedData(habitTitle: String) {
        sqldb = myDBHelper.writableDatabase
        sqldb.execSQL("INSERT INTO habit_lists VALUES(NULL, '$habitTitle', NULL)")
    }
}