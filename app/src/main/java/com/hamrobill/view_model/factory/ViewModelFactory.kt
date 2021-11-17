package com.hamrobill.view_model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hamrobill.data.repository.AuthRepository
import com.hamrobill.data.repository.BillingRepository
import com.hamrobill.utils.NetworkConnectivity
import com.hamrobill.utils.SharedPreferenceStorage
import com.hamrobill.view_model.LoginActivityViewModel
import com.hamrobill.view_model.SharedViewModel
import com.hamrobill.view_model.SplashActivityViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelFactory @Inject constructor(
    private val networkConnectivity: NetworkConnectivity,
    private val authRepository: AuthRepository,
    private val billingRepository: BillingRepository,
    private val sharedPreferenceStorage: SharedPreferenceStorage
) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass == LoginActivityViewModel::class.java && modelClass.isAssignableFrom(
                LoginActivityViewModel::class.java
            ) -> LoginActivityViewModel(
                sharedPreferenceStorage,
                networkConnectivity,
                authRepository
            ) as T
            modelClass == SharedViewModel::class.java && modelClass.isAssignableFrom(
                SharedViewModel::class.java
            ) -> SharedViewModel(
                sharedPreferenceStorage,
                networkConnectivity,
                billingRepository
            ) as T
            modelClass == SplashActivityViewModel::class.java && modelClass.isAssignableFrom(
                SplashActivityViewModel::class.java
            ) -> SplashActivityViewModel(
                sharedPreferenceStorage,
                networkConnectivity,
                billingRepository
            ) as T
            else -> throw IllegalArgumentException("Unable to construct ${modelClass.simpleName}")
        }
    }
}