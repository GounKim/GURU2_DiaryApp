package com.example.guru2_diaryapp.Tracker


import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Gravity.CENTER
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.example.guru2_diaryapp.CalendarView.SaturdayDeco
import com.example.guru2_diaryapp.CalendarView.SundDayDeco
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*
import kotlin.collections.ArrayList

class Tracker : AppCompatActivity(),
        AddTrackerDialog.OnCompleteListener,
        DelTrackerDialog.OnCompleteListener {

    lateinit var myDBHelper: MyDBHelper
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var toolbar: Toolbar

    lateinit var trackerCal: MaterialCalendarView
    lateinit var tvHabit: TextView

    lateinit var trackerLayout: GridLayout
    lateinit var ivPreMonth: ImageView
    lateinit var ivNextMonth: ImageView
    lateinit var tvYearMonth: TextView

    var thisYear: Int = CalendarDay.today().year
    var thisMonth: Int = CalendarDay.today().month + 1
    var calView = ArrayList<MaterialCalendarView>(30)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)

        //툴바를 액션바로 설정
        toolbar = findViewById(R.id.Maintoolbar)
        setSupportActionBar(toolbar)

        myDBHelper = MyDBHelper(this)
        sqlitedb = myDBHelper.readableDatabase

//        trackerCal = findViewById(R.id.trackerCal)
//        tvHabit = findViewById(R.id.tvHabbit)

        trackerLayout = findViewById(R.id.trackerLayout)
        ivPreMonth = findViewById(R.id.imgViewPreMonth)
        ivNextMonth = findViewById(R.id.imgViewNextMonth)
        tvYearMonth = findViewById(R.id.tvYearMonth)
/*
        trackerCal.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setMaximumDate(CalendarDay.from(2000, 0, 1))
                .setMaximumDate(CalendarDay.from(2100, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit()
        trackerCal.topbarVisible = false
        //trackerCal.setCurrentDate(Date(System.currentTimeMillis()))
        //trackerCal.setDateSelected(Date(System.currentTimeMillis()),true)
        trackerCal.addDecorator(SundDayDeco())
        trackerCal.addDecorator(SaturdayDeco())
        //trackerCal.addDecorator(MoodDeco(this, CalendarDay.from(2021,3,20)))

        trackerCal.selectionMode = MaterialCalendarView.SELECTION_MODE_NONE
 */

        var cCursor : Cursor    // habit_check_lists 용
        var nCursor : Cursor    // habit_lists 용
        nCursor = sqlitedb.rawQuery("SELECT habit FROM habit_lists", null)

        if (nCursor.moveToFirst()) {
            while (nCursor.moveToNext()) {
                var calendarView: MaterialCalendarView = MaterialCalendarView(this)
                var str_habit = nCursor.getString(nCursor.getColumnIndex("habit")).toString()
                //if (str_habit != "mood") {
                    var linearLayout: LinearLayout = LinearLayout(this)
                    var layoutlp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, WRAP_CONTENT)
                    layoutlp.setMargins(10,10,10,10)
                    layoutlp.gravity = CENTER
                    linearLayout.layoutParams = layoutlp
                    linearLayout.orientation = LinearLayout.VERTICAL
                    var textView: TextView = TextView(this)
                    textView.text = str_habit
                    textView.textSize = 17f
                    textView.gravity = CENTER

                    calendarView.state().edit()
                            .setFirstDayOfWeek(Calendar.MONDAY)
                            .setMaximumDate(CalendarDay.from(2000, 0, 1))
                            .setMaximumDate(CalendarDay.from(2100, 11, 31))
                            .setCalendarDisplayMode(CalendarMode.MONTHS)
                            .commit()
                    var callp = LinearLayout.LayoutParams(485, 485)
                    callp.setMargins(20,30,0,0)
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
                    if (calView.size % 2 == 0) trackerLayout.rowCount++
                //}
                //else { tvHabit.text = str_habit }

                cCursor = sqlitedb.rawQuery("SELECT * FROM habit_check_lists WHERE habit = '${str_habit}';",null)

                while (cCursor.moveToNext()) {
                    var date = cCursor.getString(cCursor.getColumnIndex("reporting_date")).toInt()
                    var checkLevel = cCursor.getString(cCursor.getColumnIndex("check_result")).toInt()

                    var year = date / 10000
                    var month = (date % 10000) / 100
                    var day = (date % 10000) % 100

                    if (str_habit == "mood") {
                        when (checkLevel) {
                            0 -> {
                                calendarView.addDecorator(MoodSick(this, CalendarDay.from(year, month - 1, day), checkLevel))
                            }
                            1 -> {
                                calendarView.addDecorator(MoodBadDeco(this, CalendarDay.from(year, month - 1, day), checkLevel))
                            }
                            2 -> {
                                calendarView.addDecorator(MoodSosoDeco(this, CalendarDay.from(year, month - 1, day), checkLevel))
                            }
                            3 -> {
                                calendarView.addDecorator(MoodGoodDeco(this, CalendarDay.from(year, month - 1, day), checkLevel))
                            }
                        }
                    }
                    else {
                        val calendar = Calendar.getInstance()
                        calendar.set(year, month - 1, day)

                        when (checkLevel) {
                            0 -> {
                                calendarView.selectionColor = Color.parseColor("#ff5555")
                                calendarView.setDateSelected(calendar, true);
                            }
                            1 -> {
                                calendarView.selectionColor = Color.parseColor("#fca70a")
                                calendarView.setDateSelected(calendar, true);
                            }
                            2 -> {
                                calendarView.selectionColor = Color.parseColor("#ace5f0")
                                calendarView.setDateSelected(calendar, true);
                            }
                        }
                    }
                }
                cCursor.close()
            }
        }
        //else { show() }

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
            trackerCal.goToPrevious()
            for (i in calView) {
                i.goToPrevious()
            }
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
            for (i in calView) {
                i.goToNext()
            }
            trackerCal.goToNext()
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
            }
            R.id.del_menu -> {
                delShow()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 추가 창 띄우기
    private fun addShow() {
        val newFragment = AddTrackerDialog()
        newFragment.show(supportFragmentManager,"dialog")
    }

    // 추가
    override fun onInputedData(habitTitle: String) {
        sqlitedb = myDBHelper.writableDatabase
        sqlitedb.execSQL("INSERT INTO habit_lists VALUES(NULL, '$habitTitle', NULL)")

        var linearLayout: LinearLayout = LinearLayout(this)
        var layoutlp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, WRAP_CONTENT)
        layoutlp.setMargins(10,50,10,10)
        layoutlp.gravity = CENTER
        linearLayout.layoutParams = layoutlp
        linearLayout.orientation = LinearLayout.VERTICAL
        var textView: TextView = TextView(this)
        textView.text = habitTitle
        textView.textSize = 17f
        textView.gravity = CENTER

        var calendarView: MaterialCalendarView = MaterialCalendarView(this)
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setMaximumDate(CalendarDay.from(2000, 0, 1))
                .setMaximumDate(CalendarDay.from(2100, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit()
        var callp = LinearLayout.LayoutParams(485, 485)
        callp.setMargins(0,40,0,0)
        calendarView.layoutParams = callp
        calendarView.topbarVisible = false
        calendarView.addDecorator(SundDayDeco())
        calendarView.addDecorator(SaturdayDeco())
        calView.add(calendarView)

        linearLayout.addView(textView)
        linearLayout.addView(calendarView)
        trackerLayout.addView(linearLayout)
    }

    // 삭제 창 띄우기
    private fun delShow() {
        val newFragment = DelTrackerDialog()
        newFragment.show(supportFragmentManager,"dialog")
    }

    // 삭제
    override fun onInputedData(habit: String, num: Int) {
        sqlitedb = myDBHelper.writableDatabase
        sqlitedb.execSQL("DELETE FROM habit_lists WHERE habit = '$habit'")

        var last = calView.size - 1
        trackerLayout.removeView(calView[last])
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