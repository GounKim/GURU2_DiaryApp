package com.example.guru2_diaryapp

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.DialogFragment

class CheckTrakerDialog(btn: Button, hb: String, nd: Int): DialogFragment() {
    var button: Button = btn
    var habit: String = hb
    var newDate: Int = nd

    interface OnCompleteListener{
        fun onInputedData(habitLevel: Int, button: Button, habit: String, newDate: Int)
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
        //builder.setTitle("목표를 달성하셨나요?")

        var levelArray = arrayOf("Bad", "SoSo", "Good")
        builder.setItems(levelArray, DialogInterface.OnClickListener { dialog, which ->
            mCallback.onInputedData(which, button, habit, newDate)
            dismiss()
        })

        return builder.create()
    }
}