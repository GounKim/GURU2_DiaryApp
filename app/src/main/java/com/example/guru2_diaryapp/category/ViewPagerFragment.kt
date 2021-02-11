package com.example.guru2_diaryapp.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru2_diaryapp.DiaryData
import com.example.guru2_diaryapp.R

class ViewPagerFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.category_viewpager_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvName)

        recyclerView.adapter = CategoryRecyclerViewAdapter(data)
        recyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
    }
}