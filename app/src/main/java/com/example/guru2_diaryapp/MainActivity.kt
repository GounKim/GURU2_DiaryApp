package com.example.guru2_diaryapp;

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*

class MainActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener {

    // 화면
    lateinit var calendarView: MaterialCalendarView

    // 메뉴
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView

    // BottomSheetDialog (하단 슬라이드)
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var tvshortDiary: TextView
    lateinit var moodImage: ImageView

    // DB
    lateinit var myDBHelper:MyDBHelper
    lateinit var sqldb:SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myDBHelper = MyDBHelper(this)
        sqldb = myDBHelper.writableDatabase

        sqldb.execSQL("INSERT INTO diary_posts VALUES (null,20200202, 1 , 0 ,'일기 본문');")

        calendarView = findViewById(R.id.calendarView)

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

        tvshortDiary = bottomSheetDialog.findViewById(R.id.shortDiary)!!
        moodImage = bottomSheetDialog.findViewById<ImageView>(R.id.moodImage)!!



        // 달력 생성
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMaximumDate(CalendarDay.from(2000, 0, 1))
                .setMaximumDate(CalendarDay.from(2100, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit()

        // 달력 Date 클릭시
        calendarView.setOnDateChangedListener { widget, date, selected ->

            var year = date.year
            var month = date.month + 1
            var day = date.day
            var newDate = year * 10000 + month * 100 + day

            Toast.makeText(this, "$year , $month, $day, $newDate", Toast.LENGTH_SHORT).show()

            sqldb = myDBHelper.readableDatabase

            var cursor: Cursor
            cursor = sqldb.rawQuery("SELECT content "
                                            + "FROM diary_posts "
                                            + "WHERE reporting_date = '"+ newDate + "';", null)

            if (cursor.moveToFirst()) {
                var diaryText = cursor.getString(0).toString()
                var shortDiary = diaryText.substring(0, diaryText.indexOf("."))
                tvshortDiary.text = shortDiary
            } else {
                tvshortDiary.text = "작성된 일기가 없습니다."
            }

            /*
            mood_weather_lists 테이블을 합쳤습니다. 맞게 수정해둘게요!
            cursor = sqldb.rawQuery("SELECT mood "
                                            + "FROM mood_weather_lists "
                                            + "WHERE reporting_date = '"+ newDate + "';", null)

             */

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

            cursor.close()
            sqldb.close()

            bottomSheetDialog.show()
        }

        tvshortDiary.setOnClickListener() {
            val intent = Intent(this, com.example.guru2_diaryapp.diaryView.DiaryView::class.java)
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
                val intent = Intent(this, SelectActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, com.example.guru2_diaryapp.diaryView.DiaryView::class.java)
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
}