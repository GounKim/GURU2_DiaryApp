package com.example.guru2_diaryapp

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*
import kotlin.collections.ArrayList

class MoodDeco(context: Context, date: CalendarDay, mood: Int) : DayViewDecorator {
    val drawable: Drawable = context?.getDrawable(R.drawable.ic_mood_good)!!
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
