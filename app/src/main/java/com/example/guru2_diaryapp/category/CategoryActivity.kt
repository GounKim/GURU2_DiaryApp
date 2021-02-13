package com.example.guru2_diaryapp.category

import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

    //카테고리 삭제 추가 메뉴
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.category_edit,menu)
        return super.onCreateOptionsMenu(menu)
    }

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

                    if(name != null) {
                        sqldb = myDBHelper.writableDatabase

                        try {
                            sqldb.execSQL("INSERT INTO diary_categorys VALUES (null,'$name');")
                            Toast.makeText(applicationContext, "$name 가 생성되었습니다.",
                                    Toast.LENGTH_SHORT).show()

                        }catch (e:SQLiteConstraintException){
                            Toast.makeText(applicationContext, "카테고리명이 중복됩니다.",
                                    Toast.LENGTH_SHORT).show()
                        } finally {
                            sqldb.close()
                        }

                    }else{
                        Toast.makeText(applicationContext, "카테고리명은 비워둘 수 없습니다.",
                                Toast.LENGTH_SHORT).show()
                    }

                    //새로고침
                    finish()
                    startActivity(Intent(this, this::class.java))
                }

                alert.setNegativeButton("취소",null)
                alert.show()

            }

            //카테고리 삭제
            R.id.action_delete_cate->{

                //카테고리가 하나뿐일 경우 삭제하지 못하게 함
                if (tabList.size <= 1){
                    Toast.makeText(applicationContext, "삭제할 카테고리가 없습니다.",
                            Toast.LENGTH_SHORT).show()
                    return false
                }

                val tabList = Array<String>(tabList.size,{i->tabList[i].second})    //삭제 가능한 카테고리 리스트
                var selected:String? = null                                         //선택된 카테고리

                //선택창
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

            R.id.action_rename_cate ->{
                val tabList = Array<String>(tabList.size,{i->tabList[i].second})
                var selected:String? = null

                //수정할 카테고리 이름을 입력받을 창
                var alertDialog:AlertDialog.Builder = AlertDialog.Builder(this)
                var newName_edt:EditText = EditText(this)
                alertDialog.setTitle("수정할 카테고리명을 입력하세요.")
                alertDialog.setView(newName_edt)

                //선택 후 입력창 팝업
                alertDialog.setPositiveButton("확인"){ dialog, which ->
                    var newName = newName_edt.text.toString()

                    if (newName != null){

                        sqldb = myDBHelper.writableDatabase

                        try {
                            sqldb.execSQL("UPDATE diary_categorys SET category_name ='$newName' " +
                                    "WHERE category_name = '$selected';")
                            Toast.makeText(applicationContext, "$selected 가 $newName 로 변경되었습니다.",
                                    Toast.LENGTH_SHORT).show()

                        }catch (e: SQLiteConstraintException){
                            Toast.makeText(applicationContext, "카테고리명이 중복됩니다.",
                                    Toast.LENGTH_SHORT).show()

                        }finally {
                            sqldb.close()
                        }

                    }else{
                        Toast.makeText(applicationContext, "카테고리명은 비워둘 수 없습니다.",
                                Toast.LENGTH_SHORT).show()
                    }

                    //새로고침
                    finish()
                    startActivity(Intent(this, this::class.java))
                }

                alertDialog.setNegativeButton("취소",null)


                //카테고리 선택 창
                var alert:AlertDialog.Builder = AlertDialog.Builder(this)
                alert.setTitle("카테고리 수정")

                alert.setItems(tabList, DialogInterface.OnClickListener{
                    dialog, which ->
                    selected = tabList[which]
                    alertDialog.show()
                })

                alert.show()

            }
        }
        return super.onOptionsItemSelected(item)
    }
}