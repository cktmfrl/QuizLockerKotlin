package com.chahye.quizlocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenOffReceiver: BroadcastReceiver() {
    val TAG = "ScreenOffReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        when {
            // 화면이 꺼질 때 수신되는 브로드캐스트 메시지인 경우
            intent?.action == Intent.ACTION_SCREEN_OFF -> {
                //Log.d(TAG, "퀴즈잠금: 화면이 꺼졌습니다.")
                //Toast.makeText(context, "퀴즈잠금: 화면이 꺼졌습니다.", Toast.LENGTH_LONG).show()

                // 화면이 꺼지면 다음 화면을 실행
                val intent = Intent(context, QuizLockerActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 기존 액티비티 스택 제거
                context?.startActivity(intent)
            }
        }
    }

}