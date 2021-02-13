package com.example.guru2_diaryapp.Tracker

import android.content.Context
import android.graphics.drawable.Drawable
import com.example.guru2_diaryapp.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*

class CheckMoodDeco (context: Context, date: CalendarDay, mood: Int) : DayViewDecorator {
    val drawableBad: Drawable = context?.getDrawable(R.drawable.ic_mood_bad)!!
    val drawableSoso: Drawable = context?.getDrawable(R.drawable.ic_mood_soso_main)!!
    val drawableGood: Drawable = context?.getDrawable(R.drawable.ic_mood_good)!!
    val drawableSur: Drawable = context?.getDrawable(R.drawable.ic_mood_surprise)!!
    val drawableSick: Drawable = context?.getDrawable(R.drawable.ic_mood_sick)!!
    var myDay = date
    var myMood = mood

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day!!.equals(myDay)
    }

    override fun decorate(view: DayViewFacade?) {
        when(myMood) {
            1 -> view?.setBackgroundDrawable(drawableBad)
            2 -> view?.setBackgroundDrawable(drawableSoso)
            3 -> view?.setBackgroundDrawable(drawableGood)
            4 -> view?.setBackgroundDrawable(drawableSick)
            5 -> view?.setBackgroundDrawable(drawableSur)
        }
    }

    fun setDate(date: Date) {
        this.myDay = CalendarDay.from(date)
    }
}