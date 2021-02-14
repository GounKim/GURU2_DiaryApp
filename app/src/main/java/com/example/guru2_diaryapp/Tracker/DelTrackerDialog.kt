package com.example.guru2_diaryapp.Tracker

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.guru2_diaryapp.R
import java.lang.RuntimeException

class DelTrackerDialog : DialogFragment() {

    lateinit var editTitle: EditText
    lateinit var btnAdd: Button
    lateinit var btnCancle: Button

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
        var inflater = requireActivity().layoutInflater
        var view = inflater.inflate(R.layout.del_tracker, null)

        builder.setTitle("삭제할 habit을 입력하세요.")
        builder.setView(view)

        editTitle = view.findViewById(R.id.editHabbitTitle)
        btnAdd = view.findViewById(R.id.btnAdd)
        btnCancle = view.findViewById(R.id.btnCancle)

        btnAdd.setOnClickListener {
            try {
                var str_habit = editTitle.text.toString()

                if (str_habit == "mood") {
                    dismiss()
                    Toast.makeText(context, "mood는 삭제가 불가능합니다.",Toast.LENGTH_SHORT).show()
                }
                else {
                    dismiss()
                    mCallback.onInputedData(str_habit)
                }
            }
            catch (e: RuntimeException) {
                dismiss()
            }
        }

        btnCancle.setOnClickListener {
            dismiss()
        }

        return builder.create()
    }
}