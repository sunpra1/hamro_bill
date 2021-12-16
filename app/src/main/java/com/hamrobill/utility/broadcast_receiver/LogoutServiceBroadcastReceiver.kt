package com.hamrobill.utility.broadcast_receiver

import android.content.Context
import android.content.Intent
import com.hamrobill.di.subcomponent.ActivityComponent
import com.hamrobill.utility.DICompactBroadcastReceiver
import com.hamrobill.utility.SharedPreferenceStorage
import com.hamrobill.view.LoginActivity
import javax.inject.Inject

class LogoutServiceBroadcastReceiver : DICompactBroadcastReceiver() {
    @Inject
    lateinit var mSharedPreferenceStorage: SharedPreferenceStorage

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        mSharedPreferenceStorage.token = null
        mSharedPreferenceStorage.tokenExpiresAt = null
        mSharedPreferenceStorage.loggedUserName = null
        mSharedPreferenceStorage.hasSessionExpired = true
        context.startActivity(Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    override fun configureDependencyInjection(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }
}