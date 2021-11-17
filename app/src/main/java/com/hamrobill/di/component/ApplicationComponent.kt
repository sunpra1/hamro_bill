package com.hamrobill.di.component

import android.content.Context
import com.hamrobill.di.module.NetworkModule
import com.hamrobill.di.subcomponent.ActivityComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton


@Singleton
@Component(modules = [NetworkModule::class])
interface ApplicationComponent {
    fun getActivityComponentFactory(): ActivityComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@Named("Application") @BindsInstance context: Context): ApplicationComponent
    }
}