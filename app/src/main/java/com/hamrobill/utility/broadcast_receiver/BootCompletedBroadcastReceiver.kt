package com.hamrobill.utility.broadcast_receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.hamrobill.di.subcomponent.ActivityComponent
import com.hamrobill.utility.DICompactBroadcastReceiver
import com.hamrobill.utility.SharedPreferenceStorage
import com.hamrobill.utility.getCalenderDate
import java.util.*
import javax.inject.Inject

class BootCompletedBroadcastReceiver : DICompactBroadcastReceiver() {
    @Inject
    lateinit var mSharedPreferenceStorage: SharedPreferenceStorage

    @Inject
    lateinit var mAlarmManager: AlarmManager

    companion object {
        private const val LOGOUT_ALARM_REQUEST_CODE = 1
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                val tokenExpiryTime = mSharedPreferenceStorage.tokenExpiresAt
                if (tokenExpiryTime != null) {
                    val pendingIntent: PendingIntent =
                        Intent(context, LogoutServiceBroadcastReceiver::class.java)
                            .let {
                                PendingIntent.getBroadcast(
                                    context,
                                    LOGOUT_ALARM_REQUEST_CODE,
                                    it,
                                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                )
                            }
                    val currentTimeInMillis = Calendar.getInstance().timeInMillis
                    val expiryTimeInMillis = tokenExpiryTime.getCalenderDate().timeInMillis
                    val triggerAt =
                        if (currentTimeInMillis >= expiryTimeInMillis) currentTimeInMillis + 1000 else expiryTimeInMillis
                    mAlarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerAt,
                        pendingIntent
                    )
                }
            }
        }
    }

    override fun configureDependencyInjection(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }
}