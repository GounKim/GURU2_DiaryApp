package com.example.guru2_diaryapp.category

import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ListAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.DialogTitle
import androidx.appcompat.widget.Toolbar
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
    lateinit var toolbar: Toolbar


    //카테고리 정보 저장 페어
    var tabList = ArrayList<Pair<Int, String>>()
    var timeLineData = ArrayList<DiaryData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        toolbar = findViewById(R.id.toolbar)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager2 = findViewById(R.id.category_vp)

        myDBHelper = MyDBHelper(this)

        setSupportActionBar(toolbar)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.category_edit,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //카테고리 삭제 추가 메뉴
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){

            //카테고리 추가
            R.id.action_add_cate ->{

                var alert: AlertDialog.Builder = AlertDialog.Builder(this)
                alert.setTitle("카테고리 추가")
                alert.setMessage("추가할 카테고리명을 입력하세요.")
                val category_name:EditText = EditText(this)
                alert.setView(category_name)

                alert.setPositiveButton("확인"){ dialog, which ->
                    var name = category_name.text.toString()
                    sqldb = myDBHelper.writableDatabase
                    sqldb.execSQL("INSERT INTO diary_categorys VALUES (null,'$name');")
                    sqldb.close()
                    Toast.makeText(applicationContext,"$name 카테고리가 생성되었습니다.",
                        Toast.LENGTH_SHORT).show()

                    //새로고침
                    finish()
                    startActivity(Intent(this, this::class.java))
                }

                alert.setNegativeButton("취소",null)
                alert.show()

            }

            //카테고리 삭제
            R.id.action_delete_cate->{

                val tabList = Array<String>(tabList.size,{i->tabList[i].second})
                var selected:String? = null    //삭제할 카테고리

                var alert:AlertDialog.Builder = AlertDialog.Builder(this)

                alert.setTitle("카테고리 삭제")
                alert.setSingleChoiceItems(tabList,0, DialogInterface.OnClickListener{
                    dialog, which ->
                    selected = tabList[which]
                })

                alert.create()

                alert.setPositiveButton("삭제") { dialog, which ->

                    if (selected != null) {
                        sqldb = myDBHelper.writableDatabase
                        sqldb.execSQL("DELETE FROM diary_categorys WHERE category_name = '$selected';")
                        Toast.makeText(applicationContext, "$selected 카테고리가 삭제되었습니다.",
                                Toast.LENGTH_SHORT).show()
                        sqldb.close()
                    } else{
                        Toast.makeText(applicationContext, "선택하지 않아 취소되었습니다.",
                                Toast.LENGTH_SHORT).show()
                    }

                    //새로고침
                    finish()
                    startActivity(Intent(this, this::class.java))
                }

                alert.setNegativeButton("취소",null)
                alert.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}