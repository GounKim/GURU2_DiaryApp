package com.example.guru2_diaryapp.category

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.guru2_diaryapp.DiaryData
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.R
import com.example.guru2_diaryapp.diaryView.DiaryView
import java.text.FieldPosition

class CategoryTabFragment : Fragment() {

    lateinit var sqldb: SQLiteDatabase

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryRecyclerViewAdapter: CategoryRecyclerViewAdapter
    lateinit var dataSet: ArrayList<com.example.guru2_diaryapp.DiaryData>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.category_viewpager_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {

        recyclerView = view.findViewById(R.id.rvName)
        categoryRecyclerViewAdapter = CategoryRecyclerViewAdapter(dataSet,view.context,recyclerView){
            id, num -> Toast.makeText(view.context,"인덱스:${num} data: ${id}", Toast.LENGTH_SHORT).show()
            var intent = Intent(view.context, DiaryView::class.java)
            intent.putExtra("post_id",id)
            startActivity(intent)
        }

        recyclerView.adapter = categoryRecyclerViewAdapter
        recyclerView.layoutManager = GridLayoutManager(view.context, 2)
        }

    /*
    override fun getItem(positon:Int){

        return
    }
    */


    //입력받은 카테고리의 글을 로드
    fun loadPosts (dbHelper: MyDBHelper,category:Pair<Int,String>):ArrayList<DiaryData>{
        var myDiaryData = ArrayList<DiaryData>()
        var cursor: Cursor

        //카테고리의 글을 20개씩 로딩.
        //임시 테이블을 생성해 글별로 대표 이미지를 한장씩 선별한 뒤 JOIN
        sqldb = dbHelper.writableDatabase

        try {
            sqldb.execSQL(
                    "CREATE TEMPORARY TABLE TMP AS SELECT img_id, post_id, img_dir FROM (SELECT img_id, post_id, img_dir, ROW_NUMBER() OVER (PARTITION BY post_id ORDER BY img_id DESC) as RowIdx FROM diary_imgs) AS img_id WHERE RowIdx = 1;",
                    null)

            cursor = sqldb.rawQuery(
                    "SELECT diary_posts.post_id, diary_posts.reporting_date, diary_posts.content, TMP.img_dir FROM diary_posts LEFT OUTER JOIN TMP ON diary_posts.post_id = TMP.post_id ORDER BY reporting_date DESC;",
                    null)

        } catch (e:java.lang.IllegalArgumentException) {

            cursor = sqldb.rawQuery(
                    "SELECT * FROM diary_posts WHERE category_id = ${category.first} ORDER BY reporting_date DESC;",
                    null
            )
        }

        var num = 0
        while (cursor.moveToNext() && num < 20) {
            val id = cursor.getInt(cursor.getColumnIndex("post_id"))
            val date =
                    cursor.getInt(cursor.getColumnIndex("reporting_date"))
            val content =
                    cursor.getString(cursor.getColumnIndex("content"))

            //val img = cursor.getString(cursor.getColumnIndex("diary_dir"))
            //val imgArray = arrayListOf(img)
            myDiaryData.add(
                    DiaryData(
                            id, date, 0,
                            "${category.second}", content, null
                    )
            )
            num++
        }

        cursor.close()
        sqldb.close()
        Log.d("test","${category.second}")
        return myDiaryData
    }
}