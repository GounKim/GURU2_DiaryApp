package com.example.guru2_diaryapp.Tracker

import android.content.Context
import android.graphics.drawable.Drawable
import com.example.guru2_diaryapp.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*

class MoodBadDeco(context: Context, date: CalendarDay, mood: Int) : DayViewDecorator {
    val drawable: Drawable = context?.getDrawable(R.drawable.ic_mood_bad)!!
    var myDay = date

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day!!.equals(myDay)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setBackgroundDrawable(drawable)
        //view?.setDaysDisabled(true)
    }

    fun setDate(date: Date) {
        this.myDay = CalendarDay.from(date)
    }
}