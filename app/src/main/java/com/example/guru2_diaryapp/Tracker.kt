package com.example.guru2_diaryapp


import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class Tracker : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)



        dbManager = DBManager(this,"habit_lists",null,1)
        dbManager = DBManager(this,"habit_check_lists",null,1)

        sqlitedb = dbManager.readableDatabase

        var cursor : Cursor

    }




}