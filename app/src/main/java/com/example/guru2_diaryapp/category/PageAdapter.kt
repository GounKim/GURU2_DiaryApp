package com.example.guru2_diaryapp.category

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

// ViewPager를 관리해줄 Adapter
class PageAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm){
    // viewpager와 연동시킬 fragement들
    private var fragments : ArrayList<CategoryFragment> = ArrayList()

    // position에 위치한 프래그먼트 반환환
    override fun getItem(position: Int): Fragment = fragments[position]

    // page의 개수 반환(fragments 배열의 크기 = page 개수)
    override fun getCount(): Int = fragments.size

    // fragments 배열에 원하는 fragment 추가
    fun addItems(fragment : CategoryFragment) {
        fragments.add(fragment)
    }
}