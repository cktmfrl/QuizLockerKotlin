package com.chahye.quizlocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager

class BootCompleteReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when {
            // 부팅이 완료될 때의 메시지인지 확인
            intent?.action == Intent.ACTION_BOOT_COMPLETED -> {
                // Intent.ACTION_BOOT_COMPLETED : 오레오 버전 이후에도 암시적 인텐트로 사용할 수 있는 메시지
                Log.d(TAG, "부팅이 완료됨")
                Toast.makeText(context, "퀴즈 잠금화면: 부팅이 완료됨", Toast.LENGTH_LONG).show()

                context?.let {
                    // 퀴즈잠금화면 설정값이 on 인지 확인
                    val pref = PreferenceManager.getDefaultSharedPreferences(context)
                    val useLockScreen = pref.getBoolean("useLockScreen", false)

                    if (useLockScreen) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            it.startForegroundService(Intent(context, LockScreenService::class.java))
                        } else {
                            it.startService(Intent(context, LockScreenService::class.java))
                        }
                    }
                }
            }
        } // when
    }

    companion object {
        val TAG = BootCompleteReceiver::class.java.name
    }
}