package com.hamrobill

import android.app.Application
import com.hamrobill.di.component.ApplicationComponent
import com.hamrobill.di.component.DaggerApplicationComponent

class HamrobillApp : Application() {
    lateinit var applicationComponent: ApplicationComponent
    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.factory().create(applicationContext)
    }
}