package com.example.guru2_diaryapp.Tracker


import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlin.collections.ArrayList

class Tracker : AppCompatActivity(), AddTrackerDialog.OnCompleteListener, DelTrackerDialog.OnCompleteListener {

    lateinit var myDBHelper: MyDBHelper
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var toolbar: Toolbar

    lateinit var trackerRecyclerView: RecyclerView

    lateinit var ivPreMonth: ImageView
    lateinit var ivNextMonth: ImageView
    lateinit var tvYearMonth: TextView

    var thisYear: Int = CalendarDay.today().year
    var thisMonth: Int = CalendarDay.today().month + 1
    var calView = ArrayList<Int>(30)

    var trackerData = ArrayList<TrackerData>(31)
    var dateList = ArrayList<Int>(31)
    var levelList = ArrayList<Int>(31)

    private lateinit var tAdapter : TrackerRecyclerViewAapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)

        //툴바를 액션바로 설정
        toolbar = findViewById(R.id.Maintoolbar)
        setSupportActionBar(toolbar)

        myDBHelper = MyDBHelper(this)
        sqlitedb = myDBHelper.readableDatabase

        ivPreMonth = findViewById(R.id.imgViewPreMonth)
        ivNextMonth = findViewById(R.id.imgViewNextMonth)
        tvYearMonth = findViewById(R.id.tvYearMonth)

        trackerRecyclerView = findViewById(R.id.trackerRecyclerView)

        // Tracker 리사이클러뷰 (GridLayout사용)
        tAdapter = TrackerRecyclerViewAapter(this, trackerData)
        trackerRecyclerView.adapter = tAdapter
        trackerRecyclerView.setHasFixedSize(true)


        val gridLayoutManager = GridLayoutManager(applicationContext, 2)
        trackerRecyclerView.layoutManager = gridLayoutManager

        var cCursor : Cursor    // habit_check_lists 용
        var nCursor : Cursor    // habit_lists 용
        nCursor = sqlitedb.rawQuery("SELECT habit FROM habit_lists", null)

        var id: Int = 1
        if (nCursor.moveToFirst()) {
            while (nCursor.moveToNext()) {
                dateList.clear()
                levelList.clear()
                var str_habit = nCursor.getString(nCursor.getColumnIndex("habit")).toString()
                cCursor = sqlitedb.rawQuery("SELECT * FROM habit_check_lists WHERE habit = '${str_habit}';",null)
                while (cCursor.moveToNext()) {
                    var date = cCursor.getString(cCursor.getColumnIndex("reporting_date")).toInt()
                    var checkLevel = cCursor.getString(cCursor.getColumnIndex("check_result")).toInt()

                    dateList.add(date)
                    levelList.add(checkLevel)

                }
                trackerData.add(TrackerData(str_habit, dateList, levelList))
            }
        }
        else { addShow() }


        /* Month 이동 */
        // 모든 캘린더뷰 아이디 받아오기
        var calYear = thisYear
        var calMonth = thisMonth
        if (calMonth.toString().length < 2) {
            tvYearMonth.text = "$calYear.0$calMonth"
        }
        else {
            tvYearMonth.text = "$calYear.$calMonth"
        }
        ivPreMonth.setOnClickListener {
            if (calMonth > 1) {
                calMonth--
            }
            else {
                calYear--
                calMonth = 12
            }
            writeCalDate(calYear, calMonth)
        }

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
//            for (i in calView) {
//                val calendarView = findViewById<MaterialCalendarView>(i)
//                calendarView.goToNext()
//             }
        }

        nCursor.close()
        myDBHelper.close()
        sqlitedb.close()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tracker_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.add_menu -> {
                addShow()
                tAdapter.upDate()
            }
            R.id.del_menu -> {
                delShow()
                tAdapter.upDate()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun addShow() {
        val newFragment = AddTrackerDialog()
        newFragment.show(supportFragmentManager,"dialog")
        tAdapter.upDate()
    }

    private fun delShow() {
        val newFragment = DelTrackerDialog()
        newFragment.show(supportFragmentManager,"dialog")
        tAdapter.upDate()
    }

    // 추가
    override fun onInputedData(habitTitle: String) {
        sqlitedb = myDBHelper.writableDatabase
        sqlitedb.execSQL("INSERT INTO habit_lists VALUES(NULL, '$habitTitle', NULL)")

        tAdapter.upDate()

        //var intent = Intent(this, Tracker::class.java)
       //startActivity(intent)
    }

    // 삭제
    override fun onInputedData(habit: String, num: Int) {
        sqlitedb = myDBHelper.writableDatabase
        sqlitedb.execSQL("DELETE FROM habit_lists WHERE habit = '$habit'")

        tAdapter.upDate()
        //var intent = Intent(this, Tracker::class.java)
        //startActivity(intent)
    }

    fun writeCalDate(year: Int, month: Int) {
        if (month.toString().length < 2) {
            tvYearMonth.text = "$year.0$month"
        }
        else {
            tvYearMonth.text = "$year.$month"
        }
    }

}