package com.example.guru2_diaryapp.category

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.guru2_diaryapp.DiaryData
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CategoryActivity : AppCompatActivity() {

    lateinit var myDBHelper: MyDBHelper
    lateinit var sqldb:SQLiteDatabase
    lateinit var categoryTabFragmentAdapter: CategoryTabFragmentAdapter

    lateinit var viewPager2: ViewPager2
    lateinit var tabLayout:TabLayout


    //카테고리 정보 저장 페어
    var tabList = ArrayList<Pair<Int, String>>()
    var timeLineData = ArrayList<DiaryData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager2 = findViewById(R.id.category_vp)
        myDBHelper = MyDBHelper(this)


        //카테고리 프래그먼트 생성
        loadCategory()

        //상단 메뉴 생성
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = tabList[position].second
        }.attach()

        //탭전환
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })

        //카테고리별로 글 로딩
        //for (i in tabList){
        //    timeLineData = loadPosts(i)
        //}


    }

    //카테고리 정보 조회 , 생성
    fun loadCategory(){
        sqldb = myDBHelper.readableDatabase

        var cursor: Cursor
        cursor = sqldb.rawQuery("SELECT * FROM diary_categorys;",null)

        while(cursor.moveToNext()){
            var id = cursor.getInt(cursor.getColumnIndex("category_id"))
            var tab = cursor.getString(cursor.getColumnIndex("category_name"))
            tabList.add(Pair(id,tab))
        }

        cursor.close()
        sqldb.close()

        categoryTabFragmentAdapter = CategoryTabFragmentAdapter(myDBHelper,this,tabList)
        viewPager2.adapter = categoryTabFragmentAdapter
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL


    }
}