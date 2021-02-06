package com.example.guru2_diaryapp.category

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.guru2_diaryapp.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy


class CategoryActivity : AppCompatActivity() {

    /*private lateinit var mContext : Context
    lateinit var category_text : TextView
    lateinit var category_logo : ImageView
    lateinit var  : ViewPager*/
    lateinit var category_tab : TabLayout
    lateinit var category_vp : ViewPager2
    lateinit var pgAdapter: PageAdapter

    val CategoryTextArray = arrayOf("일상", "여행")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        /*mContext = applicationContext

        category_text = findViewById(R.id.category_text)
        category_logo = findViewById(R.id.category_logo)*/

        category_tab = findViewById(R.id.category_tab)
        category_vp = findViewById(R.id.category_vp)

        pgAdapter = PageAdapter(this)
        category_vp.adapter = pgAdapter

        TabLayoutMediator(category_tab, category_vp,
            TabConfigurationStrategy { tab, position ->
                tab.text = "Tab " + (position + 1)
            }).attach()
    }

    // 함수의 파라미터로 각 카테고리의 이름을 받고 그에 맞는 아이콘을 세팅
    /*private fun createView(categoryName : String) : View {
        category_text.text = categoryName
        when (categoryName) {
            "일상" -> {
                category_logo.setImageResource(R.drawable.ic_baseline_article_24)
                return category_logo
            }
            "여행" -> {
                category_logo.setImageResource(R.drawable.ic_baseline_local_airport_24)
                return category_logo
            }
            "교환일기" -> {
                category_logo.setImageResource(R.drawable.ic_baseline_menu_book_24)
                return category_logo
            }
            else -> {
                return category_logo
            }
        }
    }

    private fun initViewPager() {
        val dailyFragment = CategoryFragment()
        dailyFragment.name = "일상"

        val travleFragment = CategoryFragment()
        travleFragment.name = "여행"

        val exchangeFragment = CategoryFragment()
        exchangeFragment.name = "교환일기"

        val adapter = PageAdapter(supportFragmentManager) // pageAdapter 생성
        adapter.addItems(dailyFragment)
        adapter.addItems(travleFragment)
        adapter.addItems(exchangeFragment)

        category_vp.adapter = adapter // 뷰페이저에 adapter 장착
        category_tab.setupWithViewPager(category_vp) // 탭레이아웃과 뷰페이저 연동

        category_tab.getTabAt(0)?.setCustomView(createView("일상"))
        category_tab.getTabAt(1)?.setCustomView(createView("여행"))
        category_tab.getTabAt(2)?.setCustomView(createView("교환일기"))
    }*/
}