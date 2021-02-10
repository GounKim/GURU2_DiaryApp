package com.example.guru2_diaryapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MyDBHelper(
        context: Context) : SQLiteOpenHelper(context,"cookieDB",null,1) {
    lateinit var sql:String

    override fun onCreate(db: SQLiteDatabase?) {
        //habit 항목 (기분 항목 포함) 리스트 저장 테이블
        sql = "CREATE TABLE habit_lists (habit_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "habit TEXT NOT NULL UNIQUE, sort_num INTEGER);"
        db?.execSQL(sql)
        Log.d("DB","실행"+sql)

        //기분 항목 기본으로 생성, id 우선순위 모두 0
        sql = "INSERT INTO habit_lists VALUES(0,'mood', 0 );"
        db?.execSQL(sql)
        Log.d("DB","실행"+sql)

        //habit,기분 체크 정보 저장 테이블
        sql = "CREATE TABLE habit_check_lists(reporting_date INTEGER, habit TEXT NOT NULL, check_result INTEGER, "
        sql += "PRIMARY KEY (reporting_date, habit), "
        sql += "CONSTRAINT habit_fk FOREIGN KEY (habit) REFERENCES habit_lists (habit) ON UPDATE CASCADE ON DELETE NO ACTION);"
        db?.execSQL(sql)
        Log.d("DB","실행"+sql)

        //카테고리 리스트 테이블
        sql = "CREATE TABLE diary_categorys (category_id INTEGER PRIMARY KEY AUTOINCREMENT, category_name TEXT);"
        db?.execSQL(sql)
        Log.d("DB","실행"+sql)

        //기본 카테고리 자동 생성
        sql = "INSERT INTO diary_categorys VALUES (0,'basic cookie');"
        db?.execSQL(sql)
        Log.d("DB","실행"+sql)

        //다이어리 글 저장 테이블
        sql = "CREATE TABLE diary_posts (post_id INTEGER PRIMARY KEY AUTOINCREMENT, "
        sql += "reporting_date INTEGER NOT NULL, weather INTEGER, "
        sql += "category_id INTEGER DEFAULT 0 NOT NULL, content TEXT, "
        sql += "CONSTRAINT category_fk FOREIGN KEY (category_id) "
        sql += "REFERENCES diary_categorys (category_id) ON DELETE SET DEFAULT);"
        db?.execSQL(sql)
        Log.d("DB","실행"+sql)

        //이미지 경로 저장 테이블
        db?.execSQL("CREATE TABLE diary_imgs(img_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "post_id INTEGER,img_file BLOB NOT NULL," +
                "CONSTRAINT post_id_fk FOREIGN KEY (post_id) REFERENCES diary_posts (post_id)" +
                "ON DELETE CASCADE);")
    }

    fun resetTable(db:SQLiteDatabase,table:String): Boolean {
        db?.execSQL("DELETE FROM $table;")
        return true
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE diary_imgs;")
        db?.execSQL("DROP TABLE diary_posts;")
        db?.execSQL("DROP TABLE diary_categorys;")
        db?.execSQL("DROP TABLE habit_check_lists;")
        db?.execSQL("DROP TABLE habit_lists;")
        this.onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase?) {
        db?.execSQL("PRAGMA foreign_key = 1;")
        super.onOpen(db)
    }
}