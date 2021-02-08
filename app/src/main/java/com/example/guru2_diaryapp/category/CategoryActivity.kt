package com.example.guru2_diaryapp.category

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.guru2_diaryapp.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CategoryActivity : AppCompatActivity() {
    private lateinit var viewPager2 : ViewPager2
    private lateinit var tabLayout : TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager2 = findViewById(R.id.category_vp)

        viewPager2.adapter = ViewPagerFragmentAdapter(this)
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when(position) {
                0 -> tab.text = "일상"
                1 -> tab.text = "여행"
                else -> tab.text = "교환일기"
            }
        }.attach()

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
    }
}