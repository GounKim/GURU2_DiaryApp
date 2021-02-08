package com.example.guru2_diaryapp


import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Tracker : AppCompatActivity() {

    lateinit var myDBHelper: MyDBHelper
    lateinit var sqlitedb: SQLiteDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)



        //dbManager = MyDBHelper(this,"habit_lists",null,1)
       //dbManager = MyDBHelper(this,"habit_check_lists",null,1)
        myDBHelper = MyDBHelper(this)
        sqlitedb = myDBHelper.readableDatabase

        var cursor : Cursor
        cursor = sqlitedb.rawQuery("SELECT habit FROM habit_lists",null)
    }




}