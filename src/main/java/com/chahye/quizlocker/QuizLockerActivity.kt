package com.chahye.quizlocker

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import android.widget.SeekBar
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_quiz_locker.*
import org.json.JSONObject
import java.util.*

class QuizLockerActivity : AppCompatActivity() {

    var quiz: JSONObject? = null

    // 오답 및 정답횟수 각각 저장 SharedPreference
    val wrongAnswerPref by lazy { getSharedPreferences("wrongAnswer", Context.MODE_PRIVATE) }
    val correctAnswerPref by lazy { getSharedPreferences("correctAnswer", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 잠금화면보다 상단에 위치하기 위한 설정 조정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // 잠금화면에서 보여지도록 설정
            setShowWhenLocked(true)
            // 잠금 해제
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED) // Deprecated in API level 27
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }

        // 화면을 켜진 상태로 유지
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_quiz_locker)

        // 설정한 카테고리 가져오기
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        var category = pref.getStringSet("category", null)

        // 선택한 카테고리가 없을 경우 화면 종료
        if (category?.size == 0) {
            finish()
            //category = setOf("일반상식") // default value
        }

        //  퀴즈 데이터를 가져와 선택하여 보여준다
        val json = assets.open("quiz.json").reader().readText()
        var quizArray = JSONObject(json).getJSONArray(category!!.random())
        var random = Random().nextInt(quizArray.length())
        quiz = quizArray.getJSONObject(random)

        quizLabel.text = quiz?.getString("question")
        choice1.text = quiz?.getString("choice1")
        choice2.text = quiz?.getString("choice2")

        // 정답 및 오답횟수를 보여준다
        val id = quiz?.getString("id") ?: ""
        correctCountLabel.text = "정답횟수: ${correctAnswerPref.getInt(id, 0)}"
        wrongCountLabel.text = "오답횟수: ${wrongAnswerPref.getInt(id, 0)}"

        // SeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when {
                    progress > 95 -> {
                        leftImageView.setImageResource(R.drawable.padlock)
                        // 우측 선택한 것으로 간주
                        rightImageView.setImageResource(R.drawable.unlock)
                    }
                    progress < 5 -> {
                        // 좌측 선택한 것으로 간주
                        leftImageView.setImageResource(R.drawable.unlock)
                        rightImageView.setImageResource(R.drawable.padlock)
                    }
                    // 양끝이 아닌 경우 이미지 모두 잠금 아이콘으로 변경
                    else -> {
                        leftImageView.setImageResource(R.drawable.padlock)
                        rightImageView.setImageResource(R.drawable.padlock)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            // 터치 조작을 끝낸 경우
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress ?: 50
                when {
                    progress > 95 -> checkChoice(quiz?.getString("choice2") ?: "")
                    progress < 5 -> checkChoice(quiz?.getString("choice1") ?: "")
                    // 양끝이 아닌 경우 중앙값으로 초기화
                    else -> seekBar?.progress = 50
                }
            }

        })

    }

    // 정답 체크 함수
    fun checkChoice(choice: String) {
        quiz?.let {
            when {
                choice == it.getString("answer") -> {
                    // 정답횟수 증가
                    val id = it.getString("id")
                    var count = correctAnswerPref.getInt(id, 0)
                    count++
                    correctAnswerPref.edit().putInt(id, count).apply()
                    correctCountLabel.text = "정답횟수: ${count}"
                    finish()
                }
                else -> {
                    // 오답횟수 증가
                    val id = it.getString("id")
                    var count = wrongAnswerPref.getInt(id, 0)
                    count++
                    wrongAnswerPref.edit().putInt(id, count).apply()
                    wrongCountLabel.text = "오답횟수: ${count}"

                    // 정답이 아닌 경우 UI 초기화
                    leftImageView.setImageResource(R.drawable.padlock)
                    rightImageView.setImageResource(R.drawable.padlock)
                    seekBar?.progress = 50

                    // 정답이 아닌 경우 진동알림 추가
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= 26) {
                        // 1초 동안 100의 세기(최고 255)로 1회 진동
                        vibrator.vibrate(VibrationEffect.createOneShot(1000, 100))
                    } else {
                        vibrator.vibrate(1000)
                    }
                }
            }
        }

    }
}