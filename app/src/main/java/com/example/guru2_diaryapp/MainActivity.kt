package com.example.guru2_diaryapp;

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat
import androidx.core.view.isInvisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.guru2_diaryapp.R
import com.example.guru2_diaryapp.diaryView.DiaryView
import com.google.android.material.navigation.NavigationView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*

class MainActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener {

    // 화면
    lateinit var calendarView: MaterialCalendarView
    lateinit var tvShortDiary: TextView
    lateinit var bottomTextBox: LinearLayout

    // 메뉴
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.calendarView)
        tvShortDiary = findViewById(R.id.shortDiary)
        bottomTextBox = findViewById(R.id.bottomTextLayout)


        // actionbar의 왼쪽에 버튼 추가
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_book_24)

        // 네비게이션 드로어 설정
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.naviView)

        navigationView.setNavigationItemSelectedListener(this)

        // 달력 생성
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMaximumDate(CalendarDay.from(2000, 0, 1))
                .setMaximumDate(CalendarDay.from(2100, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit()

        // 달력 Date 클릭시
        calendarView.setOnDateChangedListener { widget, date, selected ->
            val ani = TranslateAnimation(0f, 0f, bottomTextBox.height.toFloat(), 0f)
            ani.duration = 400
            ani.fillAfter = true
            bottomTextBox.animation = ani
            bottomTextBox.visibility = View.VISIBLE
            tvShortDiary.setText(date.toString())

            calendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_NONE
        }

        tvShortDiary.setOnClickListener {
            val intent = Intent(this, SelectActivity::class.java)
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
                val intent = Intent(this, MainTimelineView::class.java)
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
                val intent = Intent(this, DiaryView::class.java)
                startActivity(intent)
            }
            else -> {
                Toast.makeText(applicationContext, "눌림", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawers()
        return true
    }

    override fun onBackPressed() {
        if (bottomTextBox.visibility == View.VISIBLE) {
            val ani = TranslateAnimation(0f, 0f, 0f, bottomTextBox.height.toFloat())
            ani.duration = 400
            ani.fillAfter = true
            bottomTextBox.animation = ani
            bottomTextBox.visibility = View.GONE
            calendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_SINGLE
        }
        else if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers()
        }
        else {
            super.onBackPressed()
        }
    }
}