package com.example.guru2_diaryapp.category

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CategoryActivity : AppCompatActivity() {
    private lateinit var viewPager2 : ViewPager2
    private lateinit var tabLayout : TabLayout

    lateinit var myDBHelper: MyDBHelper
    lateinit var sqldb:SQLiteDatabase

    var tabList = ArrayList<Pair<Int,String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager2 = findViewById(R.id.category_vp)

        myDBHelper = MyDBHelper(this)
        sqldb = myDBHelper.readableDatabase

        var cursor:Cursor
        cursor = sqldb.rawQuery("SELECT * FROM diary_categorys;",null)


        while(cursor.moveToNext()){
            var position = cursor.getInt(cursor.getColumnIndex("category_id"))
            var tab = cursor.getString(cursor.getColumnIndex("category_name"))
            tabList.add(Pair(position,tab))
        }

        sqldb.close()

        viewPager2.adapter = ViewPagerFragmentAdapter(this)
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = tabList[position].second
        }.attach()

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
    }
}