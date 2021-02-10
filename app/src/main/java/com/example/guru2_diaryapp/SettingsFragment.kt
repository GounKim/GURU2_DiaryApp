package com.example.guru2_diaryapp


import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         addPreferencesFromResource(R.xml.settings)
     }

}