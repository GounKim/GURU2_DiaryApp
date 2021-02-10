package com.example.guru2_diaryapp


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import com.google.firebase.auth.FirebaseAuth


 class SettingsFragment : PreferenceFragmentCompat() {

    lateinit var prefs : SharedPreferences
    lateinit var theme : ListPreference

    lateinit var intent: Intent
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         addPreferencesFromResource(R.xml.settings)

        prefs = PreferenceManager.getDefaultSharedPreferences(activity)


     }

}