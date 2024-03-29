package com.example.guru2_diaryapp.Tracker

import android.content.Context
import android.graphics.drawable.Drawable
import com.example.guru2_diaryapp.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*

class CheckDeco (context: Context, date: CalendarDay, check: Int) : DayViewDecorator {
    val drawableBad: Drawable = context?.getDrawable(R.drawable.ic_baseline_circle_bad)!!
    val drawableSoso: Drawable = context?.getDrawable(R.drawable.ic_baseline_circle_soso)!!
    val drawableGood: Drawable = context?.getDrawable(R.drawable.ic_baseline_circle_good)!!
    var mycheck = check
    var myDay = date

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day!!.equals(myDay)
    }

    override fun decorate(view: DayViewFacade?) {
        when(mycheck) {
            1 -> view?.setBackgroundDrawable(drawableBad)
            2 -> view?.setBackgroundDrawable(drawableSoso)
            3 -> view?.setBackgroundDrawable(drawableGood)
        }
    }
}