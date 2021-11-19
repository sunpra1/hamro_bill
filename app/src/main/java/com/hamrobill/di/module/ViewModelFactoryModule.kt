package com.hamrobill.di.module

import androidx.lifecycle.ViewModelProvider
import com.hamrobill.di.scope.ActivityScope
import com.hamrobill.view_model.factory.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {
    @ActivityScope
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelProviderFactory): ViewModelProvider.Factory
}