package com.hamrobill.di.subcomponent

import android.content.Context
import com.hamrobill.di.module.ViewModelFactoryModule
import com.hamrobill.di.scope.ActivityScope
import com.hamrobill.view.LoginActivity
import com.hamrobill.view.MainActivity
import com.hamrobill.view.SplashActivity
import com.velocity.lsc.shelfdepartment.di.module.ViewModelsModule
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Named

@ActivityScope
@Subcomponent(modules = [ViewModelFactoryModule::class, ViewModelsModule::class])
interface ActivityComponent {
    fun inject(splashActivity: SplashActivity)
    fun inject(loginActivity: LoginActivity)
    fun inject(mainActivity: MainActivity)

    fun getFragmentSubComponent(): FragmentComponent

    @Subcomponent.Factory
    interface Factory {
        fun create(@Named("Activity") @BindsInstance context: Context): ActivityComponent
    }
}