package com.example.guru2_diaryapp

import android.content.Context
import androidx.fragment.app.DialogFragment

class AddTrackerFragment: DialogFragment() {

    internal lateinit var listener: NoticeDialogListener

    interface NoticeDialogListener {
        fun onDialogAddBtnClick(dialog: DialogFragment)
        fun onDialogCancleBtnClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            //mCallback = activity as OnCompleteListener
            listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() + "must implement NoticeDialogListener"))
        }
    }
}