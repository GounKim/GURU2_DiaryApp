package com.example.guru2_diaryapp.Tracker


import android.app.ListActivity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.Gravity.CENTER
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isInvisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.guru2_diaryapp.CalendarView.SaturdayDeco
import com.example.guru2_diaryapp.CalendarView.SundDayDeco
import com.example.guru2_diaryapp.MainActivity
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.R
import com.example.guru2_diaryapp.SettingsActivity
import com.example.guru2_diaryapp.TimeLine.SearchActivity
import com.google.android.gms.common.internal.constants.ListAppsActivityContract
import com.google.android.material.navigation.NavigationView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*
import kotlin.collections.ArrayList

class Tracker : AppCompatActivity(),
        AddTrackerDialog.OnCompleteListener,
        DelTrackerDialog.OnCompleteListener, NavigationView.OnNavigationItemSelectedListener {

    lateinit var myDBHelper: MyDBHelper
    lateinit var sqlitedb: SQLiteDatabase

    // 메뉴
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar

    // 화면
    lateinit var trackerLayout: GridLayout
    lateinit var ivPreMonth: ImageView
    lateinit var ivNextMonth: ImageView
    lateinit var tvYearMonth: TextView

    // 달력
    var thisYear: Int = CalendarDay.today().year
    var thisMonth: Int = CalendarDay.today().month + 1
    var linLayList = ArrayList<LinearLayout>(30)
    var calView = ArrayList<MaterialCalendarView>(30)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)

        //툴바를 액션바로 설정
        toolbar = findViewById(R.id.Maintoolbar)
        setSupportActionBar(toolbar)

        // toolbar 왼쪽에 버튼 추가
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_book_24)

        // 네비게이션 드로어 설정
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.naviView)

        navigationView.setNavigationItemSelectedListener(this)

        myDBHelper = MyDBHelper(this)
        sqlitedb = myDBHelper.readableDatabase

        trackerLayout = findViewById(R.id.trackerLayout)
        ivPreMonth = findViewById(R.id.imgViewPreMonth)
        ivNextMonth = findViewById(R.id.imgViewNextMonth)
        tvYearMonth = findViewById(R.id.tvYearMonth)

        var cCursor: Cursor    // habit_check_lists 용
        var nCursor: Cursor    // habit_lists 용
        nCursor = sqlitedb.rawQuery("SELECT habit, habit_id FROM habit_lists ORDER BY habit_id;", null)

        while (nCursor.moveToNext()) {
            var calendarView: MaterialCalendarView = MaterialCalendarView(this)
            var str_habit = nCursor.getString(nCursor.getColumnIndex("habit")).toString()
            var habitID = nCursor.getString(nCursor.getColumnIndex("habit_id")).toInt()

            // 영역 생성
            var linearLayout: LinearLayout = LinearLayout(this)
            var layoutlp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, WRAP_CONTENT)
            layoutlp.setMargins(10, 10, 10, 10)
            layoutlp.gravity = CENTER
            linearLayout.layoutParams = layoutlp
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.id = habitID
            linLayList.add(linearLayout)

            // habit이름 textView
            var textView: TextView = TextView(this)
            textView.text = str_habit
            textView.textSize = 17f
            textView.gravity = CENTER

            // 달력 생성
            calendarView.state().edit()
                    .setFirstDayOfWeek(Calendar.MONDAY)
                    .setMaximumDate(CalendarDay.from(2000, 0, 1))
                    .setMaximumDate(CalendarDay.from(2100, 11, 31))
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit()
            var callp = LinearLayout.LayoutParams(485, 485)
            callp.setMargins(20, 30, 0, 0)
            calendarView.layoutParams = callp
            calendarView.topbarVisible = false
            calendarView.addDecorator(SundDayDeco())
            calendarView.addDecorator(SaturdayDeco())
            calendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_NONE
            calendarView.isPagingEnabled = false
            calView.add(calendarView)

            linearLayout.addView(textView)
            linearLayout.addView(calendarView)
            trackerLayout.addView(linearLayout)
            if (calView.size % 2 == 0) trackerLayout.rowCount++     // Gridlayout의 열 증가

            cCursor = sqlitedb.rawQuery("SELECT * FROM habit_check_lists WHERE habit = '${str_habit}';", null)

            while (cCursor.moveToNext()) {
                var date = cCursor.getString(cCursor.getColumnIndex("reporting_date")).toInt()
                var checkLevel = cCursor.getString(cCursor.getColumnIndex("check_result")).toInt()

                // db의 reporting_date를 년/달/일로 변경
                var year = date / 10000
                var month = (date % 10000) / 100
                var day = (date % 10000) % 100

                if (str_habit == "mood") {  // 달력에 mood 찍기
                    calendarView.addDecorator(CheckMoodDeco(this, CalendarDay.from(year, month - 1, day), checkLevel))
                } else {  // 달력에 check_result에 따른 색깔 찍기
                    calendarView.addDecorator(CheckDeco(this, CalendarDay.from(year, month - 1, day), checkLevel))
                }
            }
            cCursor.close()
        }

        /* Month 이동 */
        // 모든 캘린더뷰 아이디 받아오기
        var calYear = thisYear
        var calMonth = thisMonth
        // 택스트의 달 바꾸기
        writeCalDate(calYear, calMonth)
        // 이전달 이동
        ivPreMonth.setOnClickListener {
            if (calMonth > 1) {
                calMonth--
            }
            else {
                calYear--
                calMonth = 12
            }
            writeCalDate(calYear, calMonth)
            for (i in calView) {
                i.goToPrevious()
            }
        }
        // 다음달 이동
        ivNextMonth.setOnClickListener {
            if (calMonth < 12) {
                calMonth++
                tvYearMonth.text = "$calYear.$calMonth"
            }
            else {
                calYear++
                calMonth = 1
                tvYearMonth.text = "$calYear.$calMonth"
            }
            writeCalDate(calYear, calMonth)
            for (i in calView) {
                i.goToNext()
            }
        }

        nCursor.close()
        myDBHelper.close()
        sqlitedb.close()
    }

    // add,delete 옵션메뉴 설정
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tracker_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.add_menu -> {  // 트래커 더하기
                addShow()
            }
            R.id.del_menu -> {  // 트래커 삭제하기
                delShow()
            }
            android.R.id.home -> {   // 드로어 메뉴 선택시
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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

    // 추가 창 띄우기
    private fun addShow() {
        val newFragment = AddTrackerDialog()
        Log.d("add","추가창 테스트")
        newFragment.show(supportFragmentManager,"dialog")
    }

    // 추가
    override fun onInputedData(habitTitle: String) {
        sqlitedb = myDBHelper.writableDatabase
        sqlitedb.execSQL("INSERT INTO habit_lists VALUES(NULL, '$habitTitle', null);")
        refresh()
    }

    // 삭제 창 띄우기
    private fun delShow() {
        val newFragment = DelTrackerDialog()
        newFragment.show(supportFragmentManager,"dialog")
    }

    // 삭제
    override fun onInputedData(habit: String, num: Int) {
        sqlitedb = myDBHelper.writableDatabase
        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT habit FROM habit_lists WHERE habit = '$habit';", null)

        if (cursor.moveToFirst()) {
            sqlitedb.execSQL("DELETE FROM habit_lists WHERE habit = '$habit';")

            refresh()
        }
        else {
            Toast.makeText(this, "없는 habit입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 택스트의 달 바꾸기
    fun writeCalDate(year: Int, month: Int) {
        if (month.toString().length < 2) {
            tvYearMonth.text = "$year.0$month"
        }
        else {
            tvYearMonth.text = "$year.$month"
        }
    }

    fun refresh() {
        var intent: Intent = intent
        finish()
        startActivity(intent)
    }

}