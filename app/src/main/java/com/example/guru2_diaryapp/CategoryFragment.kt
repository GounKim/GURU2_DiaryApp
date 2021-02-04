package com.example.guru2_diaryapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoryFragment1.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryFragment : Fragment() {
    lateinit var category_tv : TextView
    var name = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        category_tv = getView()!!.findViewById(R.id.fragment_tv)
        category_tv.text = name

        return category_tv

        // 익스텐션 지원 종료로 이 코드를 위의 코드로 변경함
        //val view = inflater.inflate(R.layout.fragment_category, container, false)
        // view.textView.text = name
        // return view
    }
}