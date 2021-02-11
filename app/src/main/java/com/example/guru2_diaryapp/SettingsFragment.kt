package com.example.guru2_diaryapp


import android.content.SharedPreferences
import android.os.Bundle

import androidx.preference.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences

class SettingsFragment : PreferenceFragmentCompat() {

     lateinit var prefs : SharedPreferences
     lateinit var theme : ListPreference
     lateinit var date : ListPreference
     lateinit var font : ListPreference
      var prefListener : SharedPreferences.OnSharedPreferenceChangeListener?=null


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         addPreferencesFromResource(R.xml.settings)

        prefs = getDefaultSharedPreferences(activity)
        //editor = prefs.edit()

        theme = this!!.findPreference("theme_list")!!
        date = this!!.findPreference("date_list")!!
        if(!prefs.getString("date_list", "").equals("2020/01/01")){
            date.summary = prefs.getString("date_list", "2020/01/01");
        }
        if(!prefs.getString("date_list", "").equals("2020.01.01")){
            date.summary = prefs.getString("date_list", "2020.01.01");
        }
        font = this!!.findPreference("fonts_list")!!
        if(!prefs.getString("fonts_list", "").equals("")){
            font.summary = prefs.getString("fonts_list", "나눔고딕");
        }
        if(!prefs.getString("fonts_list", "").equals("")){
            font.summary = prefs.getString("fonts_list", "나눔바른펜");
        }

        prefs.registerOnSharedPreferenceChangeListener(prefListener);
        prefListener =  SharedPreferences.OnSharedPreferenceChangeListener{ sharedPreferences: SharedPreferences, key: String ->
            if(theme.key.equals("쿠키")){
                theme.setSummary(prefs.getString("theme_list", "쿠키"));

            }
            if(theme.key.equals("다크")){
                theme.setSummary(prefs.getString("theme_list", "다크"));
            }

        }



     }



}



