package com.example.guru2_diaryapp.CalendarView

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.DialogFragment

class SetMoodDialog (nd: Int): DialogFragment() {
    var newDate: Int = nd

    interface OnCompleteListener{
        fun onInputedData(mood: Int, newDate: Int)
    }

    lateinit var mCallback: OnCompleteListener

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

        try {
            mCallback = activity as OnCompleteListener
        }
        catch (e: ClassCastException) {
            Log.d("DialogFragmentExample", "Activity doesn't implement the OnCompleteListener interface");
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        builder.setTitle("오늘의 기분은?")

        var levelArray = arrayOf("Bad", "SoSo", "Good", "Sick", "Surprise")
        builder.setItems(levelArray, DialogInterface.OnClickListener { dialog, which ->
            mCallback.onInputedData(which + 1, newDate)
            Log.d("확인","${which+1},$newDate")
            dismiss()
        })

        return builder.create()
    }
}