package com.example.guru2_diaryapp

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.example.guru2_diaryapp.SettingsNotificationManager

@SuppressLint("ResourceType")
class SettingsFragment : PreferenceFragmentCompat() {
    lateinit var prefs: SharedPreferences
    lateinit var theme: ListPreference
    lateinit var date: ListPreference
    lateinit var font: ListPreference
    var prefListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
    private val notificationsManager by lazy {
        SettingsNotificationManager(requireContext())
    }
    private val themeProvider by lazy { ThemeProvider(requireContext()) }
    private val themePreference by lazy {
        findPreference<ListPreference>(getString(R.string.theme_preferences_key))
    }
    private val notificationPreference by lazy {
        //     findPreference<SwitchPreferenceCompat>(getString(R.string.notification_preferences_key))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        setThemePreference()
        setNotificationPreference()
    }
    private fun setThemePreference() {
        themePreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                if (newValue is String) {
                    val theme = themeProvider.getTheme(newValue)
                    AppCompatDelegate.setDefaultNightMode(theme)
                }
                true
            }
        themePreference?.summaryProvider =
            Preference.SummaryProvider<ListPreference> { preference ->
                themeProvider.getThemeDescriptionForPreference(preference.value)
            }
    }

    private fun setNotificationPreference() {
        /* notificationPreference?.onPreferenceChangeListener =
        Preference.OnPreferenceChangeListener { _, newValue ->
            val selectedValue = newValue as Boolean
            if (selectedValue) {
                notificationsManager.showNotificationsTurnedOn(
                    title = "Notifications enabled",
                    text = "You have enabled the notifications"
                )
            } else {
                notificationsManager.showNotificationsTurnedOff(
                    title = "Notifications disabled",
                    text = "You have disabled the notifications"
                )
            }
            true
        }
    notificationPreference?.summaryProvider = Preference.SummaryProvider<SwitchPreferenceCompat> { preference ->
        SettingsNotificationManager(requireContext()).getNotiticationsStatusDescription(preference.isChecked)
    }*/
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)

        prefs = getDefaultSharedPreferences(activity)
        //editor = prefs.edit()

        theme = this!!.findPreference("theme_preferences_key")!!
        date = this!!.findPreference("date_list")!!
        if (!prefs.getString("date_list", "").equals("2020/01/01")) {
            date.summary = prefs.getString("date_list", "2020/01/01");
        }
        if (!prefs.getString("date_list", "").equals("2020.01.01")) {
            date.summary = prefs.getString("date_list", "2020.01.01");
        }
        font = this!!.findPreference("fonts_list")!!
        if (!prefs.getString("fonts_list", "").equals("")) {
            font.summary = prefs.getString("fonts_list", "나눔고딕");
        }
        if (!prefs.getString("fonts_list", "").equals("")) {
            font.summary = prefs.getString("fonts_list", "나눔바른펜");
        }

        prefs.registerOnSharedPreferenceChangeListener(prefListener);
        prefListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences, key: String ->
                if (theme.key.equals("쿠키")) {
                    theme.setSummary(prefs.getString("theme_list", "쿠키"));
                }

                if (theme.key.equals("다크")) {
                    theme.setSummary(prefs.getString("theme_list", "다크"));
                }

            }

    }
}