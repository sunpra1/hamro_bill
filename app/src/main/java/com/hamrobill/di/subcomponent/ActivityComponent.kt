package com.hamrobill.di.subcomponent

import android.content.Context
import com.hamrobill.di.module.ViewModelFactoryModule
import com.hamrobill.di.scope.ActivityScope
import com.hamrobill.utility.broadcast_receiver.BootCompletedBroadcastReceiver
import com.hamrobill.utility.broadcast_receiver.LogoutServiceBroadcastReceiver
import com.hamrobill.view.LoginActivity
import com.hamrobill.view.MainActivity
import com.hamrobill.view.SplashActivity
import com.hamrobill.di.module.ViewModelsModule
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Named

@ActivityScope
@Subcomponent(modules = [ViewModelFactoryModule::class, ViewModelsModule::class])
interface ActivityComponent {
    fun inject(splashActivity: SplashActivity)
    fun inject(loginActivity: LoginActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(logoutServiceBroadcastReceiver: LogoutServiceBroadcastReceiver)
    fun inject(bootCompletedBroadcastReceiver: BootCompletedBroadcastReceiver)

    fun getFragmentSubComponent(): FragmentComponent

    @Subcomponent.Factory
    interface Factory {
        fun create(@Named("Activity") @BindsInstance context: Context): ActivityComponent
    }
}