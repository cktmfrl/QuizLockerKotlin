package com.chahye.quizlocker

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val fragment = MyPreferenceFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTitle(R.string.settings)

        // preferenceContent FrameLayout 영역을 PreferenceFragment 로 교체
        supportFragmentManager.beginTransaction().replace(R.id.preferenceContent, fragment).commit()

        initButton.setOnClickListener {
            initAnswerCount()
        }
    }

    // 정답, 오답 횟수 초기화
    fun initAnswerCount() {
        val correctAnswerPref = getSharedPreferences("correctAnswer", Context.MODE_PRIVATE)
        val wrongAnswerPref = getSharedPreferences("wrongAnswer", Context.MODE_PRIVATE)

        correctAnswerPref.edit().clear().apply()
        wrongAnswerPref.edit().clear().apply()
    }

    class MyPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            // 환경설정 리소스 파일 적용
            setPreferencesFromResource(R.xml.pref, rootKey)

            // 퀴즈 종류 요약정보에 현재 선택된 항목을 보여줌
            val categoryPref = findPreference<MultiSelectListPreference>("category") as MultiSelectListPreference
            categoryPref.setDefaultValue(setOf("수도"))
            categoryPref.summary = categoryPref.values.joinToString(", ")

            // 환경설정 정보값이 변경될 때에도 요약정보를 변경하도록 리스너 등록
            categoryPref.setOnPreferenceChangeListener { preference, newValue ->
                // newValue 파라미터가 HashSet으로 캐스팅이 실패하면 리턴
                val newValueSet = newValue as? HashSet<*> ?: return@setOnPreferenceChangeListener true

                // 선택된 퀴즈 종류로 요약정보 보여줌
                categoryPref.summary = newValue.joinToString(", ")

                true
            }

            // 퀴즈 잠금화면 사용 스위치 객체
            val useLockScreenPref = findPreference<SwitchPreference>("useLockScreen") as SwitchPreference
            useLockScreenPref.setOnPreferenceClickListener {
                when {
                    useLockScreenPref.isChecked -> {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            requireContext().startForegroundService(Intent(requireActivity(), LockScreenService::class.java))
                        } else {
                            requireContext().startService(Intent(requireActivity(), LockScreenService::class.java))
                        }
                    }
                    else -> requireContext().stopService(Intent(requireActivity(), LockScreenService::class.java))
                }
                true
            }

            // 앱이 시작되었을 때 이미 퀴즈잠금화면 사용이 체크되어있으면 서비스 실행
            if (useLockScreenPref.isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requireContext().startForegroundService(Intent(requireActivity(), LockScreenService::class.java))
                } else {
                    requireContext().startService(Intent(requireActivity(), LockScreenService::class.java))
                }
            }

        }// onCreatePreferences
    }// MyPreferenceFragment

}

