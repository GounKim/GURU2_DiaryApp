package com.example.guru2_diaryapp.Tracker

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.guru2_diaryapp.CalendarView.SaturdayDeco
import com.example.guru2_diaryapp.CalendarView.SundDayDeco
import com.example.guru2_diaryapp.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*
import kotlin.collections.ArrayList

class TrackerRecyclerViewAapter(val context: Context, val dataList: ArrayList<TrackerData>)
    : RecyclerView.Adapter<TrackerRecyclerViewAapter.ItemViewHolder>() {

    var mPosition = 0

    fun getPosition(): Int {
        return mPosition
    }

    private fun setPosition(position: Int) {
        mPosition = position
    }

    fun addItem(trData: TrackerData) {
        dataList.add(trData)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if (position > 0) {
            dataList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackerRecyclerViewAapter.ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.tracker_view_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackerRecyclerViewAapter.ItemViewHolder, position: Int) {
        holder.bind(dataList[position], context)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val tvHabbit = itemView.findViewById<TextView>(R.id.tvHabbit)
        private val trackerCal = itemView.findViewById<MaterialCalendarView>(R.id.trackerCal)

        fun bind(trData: TrackerData, context: Context) {
            var intDate = trData.reportingDate
            var strHabit = trData.habit
            var intCheck = trData.checkResult

            tvHabbit.text = strHabit
            trackerCal.state().edit()
                    .setFirstDayOfWeek(Calendar.MONDAY)
                    .setMaximumDate(CalendarDay.from(2000, 0, 1))
                    .setMaximumDate(CalendarDay.from(2100, 11, 31))
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit()
            trackerCal.topbarVisible = false
            trackerCal.addDecorator(SundDayDeco())
            trackerCal.addDecorator(SaturdayDeco())
            trackerCal.selectionMode = MaterialCalendarView.SELECTION_MODE_NONE
            //Toast.makeText(context, "${trData.reportingDate.size}", Toast.LENGTH_SHORT).show()

            for (i in intDate.indices) {
                trackerCal.id = 1000 + i

                var year = intDate[i] / 10000
                var month = (intDate[i] % 10000) / 100
                var day = (intDate[i] % 10000) % 100

                if (strHabit == "mood") { // strHabit == "mood" -> habit이 mood일 경우
                    trackerCal.addDecorator(MoodDeco(context, CalendarDay.from(year, month, day), intCheck[i]))
                }
                else {
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month - 1, day)

                    when (intCheck[i]) {
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
            }
        }
    }
}