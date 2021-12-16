package com.hamrobill.utility

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hamrobill.HamrobillApp
import com.hamrobill.di.subcomponent.ActivityComponent

abstract class DICompactActivity : AppCompatActivity() {
    lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        activityComponent = (applicationContext as HamrobillApp).applicationComponent
            .getActivityComponentFactory()
            .create(this)
        configureDependencyInjection(activityComponent)
        super.onCreate(savedInstanceState)
    }

    abstract fun configureDependencyInjection(activityComponent: ActivityComponent)
}