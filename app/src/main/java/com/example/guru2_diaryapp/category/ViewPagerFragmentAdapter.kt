package com.example.guru2_diaryapp.category

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerFragmentAdapter(fa : FragmentActivity, size:Int) : FragmentStateAdapter(fa) {
    private var count = size

    override fun getItemCount(): Int {
        return count
    }

    override fun createFragment(position: Int): Fragment {
        return ViewPagerFragment()
        }
    }
