package com.example.guru2_diaryapp.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.TextView
import com.example.guru2_diaryapp.R

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
    lateinit var fragment_tv: TextView
    var name = ""
    lateinit var view : ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)
        fragment_tv = view.findViewById(R.id.fragment_tv)
        fragment_tv.text = name

        return fragment_tv

        // 익스텐션 지원 종료로 이 코드를 위의 코드로 변경함
        //val view = inflater.inflate(R.layout.fragment_category, container, false)
        // view.textView.text = name
        // return view
    }
}