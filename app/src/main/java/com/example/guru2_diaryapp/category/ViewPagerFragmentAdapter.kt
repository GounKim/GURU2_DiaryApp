package com.example.guru2_diaryapp.category

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerFragmentAdapter(fa : FragmentActivity) : FragmentStateAdapter(fa) {
    private val TYPE_DAILY = 0
    private val TYPE_TRAVEL = 1
    private val TYPE_EXCHANGE = 2

    private var listPager : List<Int> = listOf(TYPE_DAILY, TYPE_TRAVEL, TYPE_EXCHANGE)

    override fun getItemCount(): Int {
        return listPager.size
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            TYPE_DAILY -> ViewPagerFragmentDaily()
            TYPE_TRAVEL -> ViewPagerFragmentTravel()
            else -> ViewPagerFragmentExchange()
        }
    }

}