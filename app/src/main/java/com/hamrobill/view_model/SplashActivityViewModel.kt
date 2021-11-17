package com.hamrobill.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hamrobill.data.repository.BillingRepository
import com.hamrobill.di.scope.ActivityScope
import com.hamrobill.utils.NetworkConnectivity
import com.hamrobill.utils.SharedPreferenceStorage
import javax.inject.Inject

@ActivityScope
class SplashActivityViewModel @Inject constructor(
    private val sharedPreferenceStorage: SharedPreferenceStorage,
    networkConnectivity: NetworkConnectivity,
    private val billingRepository: BillingRepository
) : ViewModel() {
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _errorMessage: MutableLiveData<Any> = MutableLiveData()
    val errorMessage: LiveData<Any> = _errorMessage
    private val _isNetworkAvailable: MutableLiveData<Boolean> = MutableLiveData()
    val isNetworkAvailable: LiveData<Boolean> = _isNetworkAvailable

    init {
        networkConnectivity.observeForever { _isNetworkAvailable.value = it }
    }
}