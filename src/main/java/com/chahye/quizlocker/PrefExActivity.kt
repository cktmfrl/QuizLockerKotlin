package com.chahye.quizlocker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chahye.quizlocker.R
import kotlinx.android.synthetic.main.activity_pref_ex.*

class PrefExActivity : AppCompatActivity() {

    val nameFieldKey = "nameField"

    val pushCheckBoxKey = "pushCheckBox"

    // activity 초기화 후 사용하기 때문에 lazy 위임 사용
    val preference by lazy { getSharedPreferences("PrefExActivity", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pref_ex)

        saveButton.setOnClickListener {
            preference.edit().putString(nameFieldKey, nameField.text.toString()).apply()
            preference.edit().putBoolean(pushCheckBoxKey, pushCheckBox.isChecked).apply()
        }

        loadButton.setOnClickListener {
            nameField.setText(preference.getString(nameFieldKey, ""))
            pushCheckBox.isChecked = preference.getBoolean(pushCheckBoxKey, false)
        }
    }
}