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

        cursor.close()
        sqldb.close()

        viewPager2.adapter = ViewPagerFragmentAdapter(this, tabList.size)
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

    //카테고리 페어를 입력받는다.
    fun loadPosts (category:Pair<Int,String>):ArrayList<DiaryData>{
        var myDiaryData = ArrayList<DiaryData>()
        var cursor:Cursor

        //입력받은 카테고리의 글을 20개 로딩
        sqldb = myDBHelper.writableDatabase
        sqldb.execSQL("CREATE TEMPORARY TABLE TMP AS SELECT img_id, post_id, img_dir FROM (SELECT img_id, post_id, img_dir, ROW_NUMBER() OVER (PARTITION BY post_id ORDER BY img_id DESC) as RowIdx FROM diary_imgs) AS img_id WHERE RowIdx = 1;",null)
        cursor = sqldb.rawQuery("SELECT diary_posts.post_id, diary_posts.reporting_date, diary_posts.content, TMP.img_dir FROM diary_posts LEFT OUTER JOIN TMP ON diary_posts.post_id = TMP.post_id ORDER BY reporting_date DESC;",null)

        var num = 0
        while (cursor.moveToNext() && num < 20) {
            val id = cursor.getInt(cursor.getColumnIndex("post_id"))
            val date =
                    cursor.getInt(cursor.getColumnIndex("reporting_date"))
            val content =
                    cursor.getString(cursor.getColumnIndex("content")).substring(0,4)

            val img = cursor.getString(cursor.getColumnIndex("diary_dir"))
            val imgArray = arrayListOf(img)
            myDiaryData.add(DiaryData( id, date, 0,
                    "${category.second}", content, imgArray))
            num++
        }

        cursor.close()
        sqldb.close()
        return myDiaryData
    }
}