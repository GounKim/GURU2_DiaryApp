package com.example.guru2_diaryapp.Tracker


import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*

class Tracker : AppCompatActivity(), AddTrackerDialog.OnCompleteListener {

    lateinit var myDBHelper: MyDBHelper
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var trackerTable: TableLayout
    lateinit var btnAddDialog: Button

    lateinit var trackerCal: MaterialCalendarView
    lateinit var tvHabit: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)

        myDBHelper = MyDBHelper(this)
        sqlitedb = myDBHelper.readableDatabase

        trackerCal = findViewById(R.id.trackerCal)
        tvHabit = findViewById(R.id.tvHabbit)

        trackerCal.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setMaximumDate(CalendarDay.from(2000, 0, 1))
                .setMaximumDate(CalendarDay.from(2100, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit()
        //trackerCal.setCurrentDate(Date(System.currentTimeMillis()))
        //trackerCal.setDateSelected(Date(System.currentTimeMillis()),true)
        //trackerCal.addDecorator(SundDayDeco())
        //trackerCal.addDecorator(SaturdayDeco())

        trackerCal.selectionMode = MaterialCalendarView.SELECTION_MODE_NONE


        var cCursor : Cursor    // habit_check_lists 용
        var nCursor : Cursor    // habit_lists 용
        nCursor = sqlitedb.rawQuery("SELECT habit FROM habit_lists", null)

        if (nCursor.moveToFirst()) {
            while (nCursor.moveToNext()) {
                nCursor.moveToNext()    // 첫번째가 mood라서 한칸 옮겼어요.

                // Habit 텍스트로 설정(이후 텍스트는 생성해야되요!)
                var str_habit = nCursor.getString(nCursor.getColumnIndex("habit")).toString()
                tvHabit.text = str_habit
                cCursor = sqlitedb.rawQuery("SELECT * FROM habit_check_lists WHERE habit = '${str_habit}';",null)

                while (cCursor.moveToNext()) {
                    Toast.makeText(this, "들어옴", Toast.LENGTH_SHORT).show()
                    var date = cCursor.getString(cCursor.getColumnIndex("reporting_date")).toInt()
                    var isChecked = cCursor.getString(cCursor.getColumnIndex("check_result")).toInt()

                    var year = date / 10000
                    var month = (date % 10000) / 100
                    var day = (date % 10000) % 100

                    Toast.makeText(this, "$date , $year , $month , $day", Toast.LENGTH_SHORT).show()

                    val calendar = Calendar.getInstance()
                    calendar.set(year, month - 1, day)

                    when (isChecked) {
                        0 -> {
                            trackerCal.selectionColor = Color.parseColor("#ff5555")
                            trackerCal.setDateSelected(calendar, true);
                        }
                        1 -> {
                            trackerCal.selectionColor = Color.parseColor("#fca70a")
                            trackerCal.setDateSelected(calendar, true);
                        }
                        2 -> {
                            trackerCal.selectionColor = Color.parseColor("#ace5f0")
                            trackerCal.setDateSelected(calendar, true);
                        }
                    }
                }
                break
            }
        }
        //else { show() }


        // HABBIT 추가
        //btnAddDialog.setOnClickListener { show() }


    }

    private fun show() {
        val newFragment = AddTrackerDialog()
        newFragment.show(supportFragmentManager,"dialog")
    }

    override fun onInputedData(title: String) {
        var tableRow: TableRow = TableRow(this)
        tableRow.setBackgroundColor(Color.WHITE)

        var tvTitle: TextView = TextView(this)
        tvTitle.text = title
        tvTitle.textSize = 20f
        tvTitle.gravity = Gravity.CENTER
        tableRow.addView(tvTitle)

        for (i in 1 .. 7) {
            var imageView: ImageView = ImageView(this)
            imageView.id = 100 + i
            //imageView.setImageResource(R.drawable.ic_blue_circle)
            imageView.foregroundGravity = Gravity.CENTER
            tableRow.addView(imageView)
        }

        trackerTable.addView(tableRow)

        Toast.makeText(this, "$title", Toast.LENGTH_SHORT).show()
    }


}