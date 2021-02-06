package com.example.guru2_diaryapp


import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class Tracker : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqLiteDatabase: SQLiteDatabase
    lateinit var h1_mon : TextView; lateinit var h2_mon : TextView; lateinit var h3_mon : TextView ; lateinit var h4_mon : TextView; lateinit var h5_mon : TextView; lateinit var h6_mon : TextView
    lateinit var h1_tue : TextView; lateinit var h2_tue : TextView; lateinit var h3_tue : TextView; lateinit var h4_tue : TextView;lateinit var h5_tue : TextView; lateinit var h6_tue : TextView;
    lateinit var h1_wedn : TextView; lateinit var h2_wedn : TextView; lateinit var h3_wedn : TextView; lateinit var h4_wedn : TextView; lateinit var h5_wedn : TextView; lateinit var h6_wedn : TextView
    lateinit var h1_thur : TextView; lateinit var h2_thur : TextView; lateinit var h3_thur : TextView; lateinit var h4_thur : TextView; lateinit var h5_thur : TextView; lateinit var h6_thur : TextView;
    lateinit var h1_fri : TextView; lateinit var h2_fri : TextView; lateinit var h3_fri : TextView; lateinit var h4_fri : TextView; lateinit var h5_fri : TextView; lateinit var h6_fri : TextView;
    lateinit var h1_sat : TextView; lateinit var h2_sat : TextView; lateinit var h3_sat : TextView; lateinit var h4_sat : TextView; lateinit var h5_sat : TextView; lateinit var h6_sat : TextView;
    lateinit var h1_sun : TextView; lateinit var h2_sun : TextView; lateinit var h3_sun:TextView; lateinit var h4_sun : TextView; lateinit var h5_sun : TextView; lateinit var h6_sun : TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)
        //트래커 표도 항목 추가할 때마다 늘어나게 해야하는지
        h1_mon = findViewById(R.id.h1_mon); h2_mon = findViewById(R.id.h2_mon); h3_mon = findViewById(R.id.h3_mon)
        h4_mon = findViewById(R.id.h4_mon); h5_mon = findViewById(R.id.h5_mon); h6_mon = findViewById(R.id.h6_mon);
        h1_tue = findViewById(R.id.h1_tue); h2_tue = findViewById(R.id.h2_tue); h3_tue = findViewById(R.id.h3_tue);
        h4_tue = findViewById(R.id.h4_tue); h5_tue = findViewById(R.id.h5_tue); h6_tue = findViewById(R.id.h6_tue);
        h1_wedn = findViewById(R.id.h1_wedn); h2_wedn = findViewById(R.id.h2_wedn); h3_wedn = findViewById(R.id.h3_wedn)
        h4_wedn = findViewById(R.id.h1_wedn); h5_wedn = findViewById(R.id.h5_wedn); h6_wedn = findViewById(R.id.h6_wedn)
        h1_thur = findViewById(R.id.h1_thur); h2_thur = findViewById(R.id.h2_thur); h3_thur = findViewById(R.id.h3_thur)
        h4_thur = findViewById(R.id.h4_thur); h5_thur = findViewById(R.id.h5_thur); h6_thur = findViewById(R.id.h6_thur)
        h1_fri = findViewById(R.id.h1_fri); h2_fri = findViewById(R.id.h2_fri); h3_fri = findViewById(R.id.h3_fri)
        h4_fri = findViewById(R.id.h4_fri); h5_fri = findViewById(R.id.h5_fri); h6_fri = findViewById(R.id.h6_fri)
        h1_sat = findViewById(R.id.h1_sat); h2_sat = findViewById(R.id.h2_sat); h3_sat = findViewById(R.id.h3_sat)
        h4_sat = findViewById(R.id.h4_sat); h5_sat = findViewById(R.id.h5_sat); h6_sat = findViewById(R.id.h6_sat)
        h1_sun = findViewById(R.id.h1_sun); h2_sun = findViewById(R.id.h2_sun); h3_sun = findViewById(R.id.h3_sun)
        h4_sun = findViewById(R.id.h4_sun); h5_sun = findViewById(R.id.h5_sun); h6_sun = findViewById(R.id.h6_sun)

        dbManager = DBManager(this,"habit_lists",null,1)
        dbManager = DBManager(this,"habit_check_lists",null,1)

        sqLiteDatabase = dbManager.readableDatabase


    }




}