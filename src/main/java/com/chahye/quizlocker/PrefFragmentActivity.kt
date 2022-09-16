package com.chahye.quizlocker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.chahye.quizlocker.R

class PrefFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pref_fragment)

        supportFragmentManager.beginTransaction().replace(android.R.id.content, MyPrefFragment()).commit()
    }

    class MyPrefFragment: PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.ex_pref, rootKey);
        }
    }

}