package com.hamrobill.di.module

import androidx.lifecycle.ViewModel
import com.hamrobill.di.anotation.ViewModelKey
import com.hamrobill.view_model.LoginActivityViewModel
import com.hamrobill.view_model.SharedViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoginActivityViewModel::class)
    abstract fun bindLoginActivityViewModel(viewModel: LoginActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SharedViewModel::class)
    abstract fun bindSharedViewModel(viewModel: SharedViewModel): ViewModel
}