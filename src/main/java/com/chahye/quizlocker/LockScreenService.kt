package com.chahye.quizlocker

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.chahye.quizlocker.R

class LockScreenService : Service() {

    // 런타임에 등록할 브로드캐스트 리시버
    //
    // 화면이 꺼졌을 때 전달되는 브로드캐스트 메시지(Intent.ACTION_SCREEN_OFF)는 암시적 허용이 되지 않음.
    // 이런 경우 매니페스트가 아니라 런타임에 브로드캐스트 리시버를 등록해야 함. 런타임에 브로드캐스트 리시버를 등록(597p)
    var screenOffReceiver: ScreenOffReceiver? = null

    private val ANDROID_CHANNEL_ID = "com.chahye.quizlocker"
    private val NOTIFICATION_ID = 9999

    override fun onCreate() {
        super.onCreate()

        if (screenOffReceiver == null) {
            screenOffReceiver = ScreenOffReceiver()
            val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
            registerReceiver(screenOffReceiver, filter)
        }
    }

    // 서비스를 호출하는 클라이언트가 startService() 함수를 호출할 때 불리는 콜백
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent != null) {
            if (intent.action == null) {
                // 서비스가 최초 실행이 아닌 경우 onCreate가 불리지 않을 수 있음.
                // 이 경우 receiver가 null이면 새로 생성하고 등록함.
                if (screenOffReceiver == null) {
                    screenOffReceiver = ScreenOffReceiver()
                    val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
                    registerReceiver(screenOffReceiver, filter)
                }
            }
        }

        // Oreo 버전부터 백그라운드 제약이 있기 때문에 포그라운드 서비스를 실행해야 함.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 알림 채널 생성
            val chan = NotificationChannel(
                ANDROID_CHANNEL_ID,
                "MyService",
                NotificationManager.IMPORTANCE_NONE
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)

            val notifyIntent = Intent(this, MainActivity::class.java)
            notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            val notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(this, ANDROID_CHANNEL_ID).apply {
                setSmallIcon(R.drawable.quiz_icon)
                setContentTitle(getString(R.string.app_name))
                setContentText("SmartTracker Running")
                setContentIntent(notifyPendingIntent) // 제거하면 시스템 설정화면으로 이동
            }
            val notification = builder.build()

            // 알림과 함께 포그라운드 서비스 시작
            startForeground(NOTIFICATION_ID, notification)
        }

        return Service.START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()

        if (screenOffReceiver != null) {
            unregisterReceiver(screenOffReceiver)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}