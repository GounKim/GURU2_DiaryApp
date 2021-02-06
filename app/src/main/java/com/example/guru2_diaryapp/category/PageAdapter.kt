package com.example.guru2_diaryapp.category

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.guru2_diaryapp.category.CategoryFragment
import androidx.viewpager2.adapter.FragmentStateAdapter

// ViewPager를 관리해줄 Adapter
class PageAdapter(fa : FragmentActivity) : FragmentStateAdapter(fa){
    private val items = ArrayList<CategoryFragment>()
    override fun getItemCount(): Int {
        return items.size
    }

    override fun createFragment(position: Int): Fragment {
        return items[position]
    }

    fun updateFragments(items : List<CategoryFragment>) {
        this.items.addAll(items)
    }


    // viewpager와 연동시킬 fragement들
    //private var fragments : ArrayList<CategoryFragment> = ArrayList()

    // position에 위치한 프래그먼트 반환환
  // override fun getItem(position: Int): Fragment = fragments[position]

    // page의 개수 반환(fragments 배열의 크기 = page 개수)
    //override fun getCount(): Int = fragments.size

    // fragments 배열에 원하는 fragment 추가
    //fun addItems(fragment : CategoryFragment) {
        //fragments.add(fragment)
    //}
}