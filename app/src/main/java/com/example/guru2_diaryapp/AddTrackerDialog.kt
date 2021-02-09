package com.example.guru2_diaryapp

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment

class AddTrackerDialog: DialogFragment() {

    lateinit var editTitle: EditText
    lateinit var spinLevel: Spinner
    lateinit var swiUseMood: Switch
    lateinit var btnAdd: Button
    lateinit var btnCancle: Button

    interface OnCompleteListener{
        fun onInputedData(title: String)
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
        var view = inflater.inflate(R.layout.add_tracker, null)
        builder.setView(view);

        editTitle = view.findViewById(R.id.editHabbitTitle)
        spinLevel = view.findViewById(R.id.spinHabbitLevel)
        swiUseMood = view.findViewById(R.id.swiUseMood)
        btnAdd = view.findViewById(R.id.btnAdd)
        btnCancle = view.findViewById(R.id.btnCancle)

        btnAdd.setOnClickListener {
            var title = editTitle.text.toString()
            dismiss()
            mCallback.onInputedData(title)
        }


        return builder.create()
    }
}