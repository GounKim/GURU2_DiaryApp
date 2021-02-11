package com.example.guru2_diaryapp.category

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.guru2_diaryapp.MyDBHelper

class CategoryTabFragmentAdapter(myDBHelper: MyDBHelper,fragment: CategoryActivity,
                                 category_list:ArrayList<Pair<Int, String>>)
    : FragmentStateAdapter(fragment) {

    var category_list = category_list
    var size = this.category_list.size
    var myDBHelper = myDBHelper

    override fun getItemCount(): Int = size

    override fun createFragment(position: Int): Fragment {
        val fragment = CategoryTabFragment()
            fragment.dataSet = fragment.loadPosts(myDBHelper,category_list[position])
            fragment.arguments = Bundle().apply {
            putInt(ARG_CATEGORY, position + 1)
        }
        return fragment
    }

    fun setData(position: Int){

    }
}
private const val ARG_CATEGORY = "Category"



