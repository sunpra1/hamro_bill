package com.hamrobill.utility.broadcast_receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hamrobill.HamrobillApp
import com.hamrobill.utility.SharedPreferenceStorage
import com.hamrobill.utility.getCalenderDate
import java.util.*
import javax.inject.Inject

class BootCompletedBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var mSharedPreferenceStorage: SharedPreferenceStorage

    @Inject
    lateinit var mAlarmManager: AlarmManager

    companion object {
        private const val LOGOUT_ALARM_REQUEST_CODE = 1
    }

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as HamrobillApp).applicationComponent
            .getActivityComponentFactory()
            .create(context)
            .inject(this)

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
}