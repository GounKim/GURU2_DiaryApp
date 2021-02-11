package com.example.guru2_diaryapp.Tracker

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.R
import java.sql.Types.NULL

class DelTrackerDialog : DialogFragment() {

    lateinit var editText: EditText

    interface OnCompleteListener{
        fun onInputedData(habit: String, num: Int = 1)
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

        builder.setTitle("삭제하기")
        builder.setMessage("삭제할 habit을 입력하세요.")

        var habit:EditText = EditText(activity)
        builder.setView(habit)

        builder.setPositiveButton("삭제", DialogInterface.OnClickListener { dialog, which ->
            var str_habit = habit.text.toString()
            dismiss()
            mCallback.onInputedData(str_habit)
        })
        builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->

        })

        return builder.create()
    }
}