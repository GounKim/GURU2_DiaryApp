package com.example.guru2_diaryapp.category

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru2_diaryapp.DiaryData
import com.example.guru2_diaryapp.MyDBHelper
import com.example.guru2_diaryapp.R
import com.example.guru2_diaryapp.diaryView.DiaryView

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
        //카테고리별 글 생성
        recyclerView = view.findViewById(R.id.rvName)
        categoryRecyclerViewAdapter = CategoryRecyclerViewAdapter(dataSet,view.context,recyclerView){
            id, num ->
            var intent = Intent(view.context, DiaryView::class.java)
            intent.putExtra("postID",id)
            startActivity(intent)
        }

        recyclerView.adapter = categoryRecyclerViewAdapter
        recyclerView.layoutManager = GridLayoutManager(view.context, 3)
        }


    //입력받은 카테고리의 글을 로드
    fun loadPosts (dbHelper: MyDBHelper,category:Pair<Int,String>):ArrayList<DiaryData>{
        var myDiaryData = ArrayList<DiaryData>()
        var cursor: Cursor

        sqldb = dbHelper.writableDatabase

        cursor = sqldb.rawQuery(
                "SELECT * FROM diary_posts WHERE category_id = ${category.first} ORDER BY reporting_date DESC;",
                null)

        var num = 0
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex("post_id"))
            val date =
                    cursor.getInt(cursor.getColumnIndex("reporting_date"))
            var content =
                    cursor.getString(cursor.getColumnIndex("content"))

            val img = cursor.getBlob(cursor.getColumnIndex("img_file"))
            //null 값일 경우 오류가 난다면 예외처리 작업할 것

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

        return myDiaryData
    }
}
