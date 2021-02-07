package com.example.guru2_diaryapp

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class TimeLineView : AppCompatActivity() {
    lateinit var dbManager: DBManager
    lateinit var sqlite:SQLiteDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_view)

        var cookie_post = ArrayList<DiaryData>()


    }
}