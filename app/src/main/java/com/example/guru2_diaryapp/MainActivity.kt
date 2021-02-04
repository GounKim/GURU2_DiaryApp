package com.example.guru2_diaryapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity.CENTER
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_NONE
import com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_SINGLE
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var calendarView: MaterialCalendarView
    lateinit var tvShortDiary: TextView
    lateinit var bottomTextBox: LinearLayout
    lateinit var blindLayout : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.calendarView)
        tvShortDiary = findViewById(R.id.shortDiary)
        bottomTextBox = findViewById(R.id.bottomTextLayout)
        blindLayout = findViewById(R.id.blindLayout)

        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMaximumDate(CalendarDay.from(2000,0,1))
                .setMaximumDate(CalendarDay.from(2100,11,31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit()

        if (bottomTextBox.isInvisible) {
            calendarView.setOnDateChangedListener { widget, date, selected ->
                val ani = TranslateAnimation(0f, 0f, bottomTextBox.height.toFloat(), 0f)
                ani.duration = 400
                ani.fillAfter = true
                bottomTextBox.animation = ani
                bottomTextBox.visibility = View.VISIBLE
                tvShortDiary.setText(date.toString())

                calendarView.selectionMode =  SELECTION_MODE_NONE

            }
        }
        else {

        }
    }

    override fun onBackPressed() {
        if (bottomTextBox.visibility == android.view.View.VISIBLE) {
            val ani = TranslateAnimation(0f, 0f, 0f, bottomTextBox.height.toFloat())
            ani.duration = 400
            ani.fillAfter = true
            bottomTextBox.animation = ani
            bottomTextBox.visibility = android.view.View.INVISIBLE
            calendarView.selectionMode =  SELECTION_MODE_SINGLE
        }
        else {
            super.onBackPressed()
        }
    }
}

