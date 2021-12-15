package com.hamrobill.utility.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hamrobill.HamrobillApp
import com.hamrobill.utility.SharedPreferenceStorage
import com.hamrobill.view.LoginActivity
import javax.inject.Inject

class LogoutServiceBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var mSharedPreferenceStorage: SharedPreferenceStorage

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as HamrobillApp).applicationComponent
            .getActivityComponentFactory()
            .create(context)
            .inject(this)

        mSharedPreferenceStorage.token = null
        mSharedPreferenceStorage.tokenExpiresAt = null
        mSharedPreferenceStorage.loggedUserName = null
        mSharedPreferenceStorage.hasSessionExpired = true
        context.startActivity(Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}