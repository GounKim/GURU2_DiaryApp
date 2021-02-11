package com.example.guru2_diaryapp


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.*
import com.google.firebase.auth.FirebaseAuth


 class SettingsFragment : PreferenceFragmentCompat() {

     lateinit var prefs : SharedPreferences
     lateinit var editor : SharedPreferences.Editor
     lateinit var theme : ListPreference
     lateinit var date : ListPreference
     lateinit var font : ListPreference
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         addPreferencesFromResource(R.xml.settings)

        prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        editor = prefs.edit()

        theme = this!!.findPreference("theme_list")!!
        if(!prefs.getString("theme_list", "").equals("쿠키")){
            theme.summary = prefs.getString("theme_list", "쿠키");
        }
        if(!prefs.getString("theme_list", "").equals("블랙")){
            theme.summary = prefs.getString("theme_list", "블랙");
        }

        date = this!!.findPreference("date_list")!!
        if(!prefs.getString("date_list", "").equals("2020/01/01")){
            date.summary = prefs.getString("date_list", "2020/01/01");
        }
        if(!prefs.getString("date_list", "").equals("2020.01.01")){
            date.summary = prefs.getString("date_list", "2020.01.01");
        }
        font = this!!.findPreference("fonts_list")!!
        if(!prefs.getString("fonts_list", "").equals("")){
            font.summary = prefs.getString("fonts_list", "기본");
        }
        if(!prefs.getString("fonts_list", "").equals("")){
            font.summary = prefs.getString("fonts_list", "나눔바른펜");
        }
     }


}



