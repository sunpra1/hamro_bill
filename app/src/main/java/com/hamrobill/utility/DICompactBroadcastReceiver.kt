package com.hamrobill.utility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.CallSuper
import com.hamrobill.HamrobillApp
import com.hamrobill.di.subcomponent.ActivityComponent

abstract class DICompactBroadcastReceiver : BroadcastReceiver() {

    lateinit var activityComponent: ActivityComponent

    @CallSuper
    override fun onReceive(context: Context, intent: Intent) {
        activityComponent = (context.applicationContext as HamrobillApp).applicationComponent
            .getActivityComponentFactory()
            .create(context)
        configureDependencyInjection(activityComponent)
    }

    abstract fun configureDependencyInjection(activityComponent: ActivityComponent)
}