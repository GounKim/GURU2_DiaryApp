package com.example.guru2_diaryapp


import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import org.w3c.dom.Text

class Tracker : AppCompatActivity(), AddTrackerDialog.OnCompleteListener {

    lateinit var myDBHelper: MyDBHelper
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var trackerTable: TableLayout
    lateinit var btnAddDialog: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)

        trackerTable = findViewById(R.id.trackerTable)
        btnAddDialog = findViewById(R.id.btnAddDiaglod)

        // 데이터 불러와 테이블 만들기
        //dbManager = MyDBHelper(this,"habit_lists",null,1)
       //dbManager = MyDBHelper(this,"habit_check_lists",null,1)
        myDBHelper = MyDBHelper(this)
        sqlitedb = myDBHelper.readableDatabase

        var cursor : Cursor
        cursor = sqlitedb.rawQuery("SELECT habit FROM habit_lists",null)

        if (cursor.moveToFirst()) { show() }
        else {
            // 데이터불러오기 불러오기
        }


        // HABBIT 추가
        btnAddDialog.setOnClickListener { show() }


    }

    private fun show() {
        val newFragment = AddTrackerDialog()
        newFragment.show(supportFragmentManager,"dialog")
    }

    override fun onInputedData(title: String) {
        var tableRow: TableRow = TableRow(this)
        tableRow.setBackgroundColor(1)

        var tvTitle: TextView = TextView(this)
        tvTitle.text = title
        tvTitle.textSize = 10F
        tableRow.addView(tvTitle)

        var tvMon: TextView = TextView(this)
        tableRow.addView(tvMon)
        var tvTue: TextView = TextView(this)
        tableRow.addView(tvTue)
        var tvWed: TextView = TextView(this)
        tableRow.addView(tvWed)
        var tvThu: TextView = TextView(this)
        tableRow.addView(tvThu)
        var tvFri: TextView = TextView(this)
        tableRow.addView(tvFri)
        var tvSat: TextView = TextView(this)
        tableRow.addView(tvSat)
        var tvSun: TextView = TextView(this)
        tableRow.addView(tvSun)

        trackerTable.addView(tableRow)

        Toast.makeText(this, "$title", Toast.LENGTH_SHORT).show()
    }


}