package com.example.guru2_diaryapp.CalendarView

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import com.example.guru2_diaryapp.MainActivity
import com.example.guru2_diaryapp.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.coroutineContext

class OnDayDeco() : DayViewDecorator {

    var date: CalendarDay = CalendarDay.today()

    fun OnDayDeco() {
       date = CalendarDay.today()
    }

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day!!.equals(date)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(StyleSpan(Typeface.BOLD))
        view?.addSpan(RelativeSizeSpan(1.5f))
        view?.addSpan(ForegroundColorSpan(Color.parseColor("#733E1F")))
        //view?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rectangle)!!)
    }

    fun setDate(date: Date) {
        this.date = CalendarDay.from(date)
    }
}
