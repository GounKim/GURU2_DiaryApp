package com.example.guru2_diaryapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBManager(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {

        //항목 리스트 테이블
        db?.execSQL("CREATE TABLE habit_lists ("+
                "habit_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "habit TEXT NOT NULL UNIQUE," +
                "sort_num INTEGER);")

        //habit 체크 상태 테이블
        db?.execSQL("CREATE TABLE habit_check_lists " +
                "(reporting_date INTEGER," +
                "habit TEXT," +
                "check_result INTEGER, " +
                "PRIMARY KEY (reporting_date, habit)," +
                "CONSTRAINT habit_fk FOREIGN KEY (habit) REFERENCES habit_lists (habit) " +
                "ON UPDATE CASCADE ON DELETE NO ACTION);")

        //기분 날씨 테이블
        db?.execSQL("CREATE TABLE mood_weather_lists " +
                "(reporting_date INTEGER PRIMARY KEY, " +
                "weather TEXT, " +
                "mood INTEGER);")

        //카테고리 리스트 테이블
        db?.execSQL("CREATE TABLE diary_categorys" +
                "(category_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "category_name TEXT);")

        //다이어리 글 저장 테이블
        db?.execSQL("CREATE TABLE diary_posts" +
                "(post_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "reporting_date INTEGER NOT NULL," +
                "category_id TEXT," +
                "content TEXT," +
                "CONSTRAINT category_fk FOREIGN KEY (category_id) REFERENCES diary_categorys (category_id) " +
                "ON DELETE SET NULL);")

        //이미지 경로 저장 테이블
        db?.execSQL("CREATE TABLE diary_imgs(img_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "post_id INTEGER,img_dir TEXT NOT NULL," +
                "CONSTRAINT post_id_fk FOREIGN KEY (post_id) REFERENCES diary_posts (post_id)" +
                "ON DELETE CASCADE);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun onCategoryDeleteMode(db: SQLiteDatabase?){
        //카테고리가 삭제되면 모든 하위 항목들이 삭제 설정을 변경

    }
}