package com.hamrobill.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamrobill.R
import com.hamrobill.data.repository.AuthRepository
import com.hamrobill.di.scope.ActivityScope
import com.hamrobill.utility.Event
import com.hamrobill.utility.NetworkConnectivity
import com.hamrobill.utility.RequestStatus
import com.hamrobill.utility.SharedPreferenceStorage
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScope
class LoginActivityViewModel @Inject constructor(
    private val sharedPrefs: SharedPreferenceStorage,
    private val networkConnectivity: NetworkConnectivity,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _errorMessage: MutableLiveData<Any> = MutableLiveData()
    val errorMessage: LiveData<Any> = _errorMessage
    private val _isNetworkAvailable: MutableLiveData<Boolean> = MutableLiveData()
    val isNetworkAvailable: LiveData<Boolean> = _isNetworkAvailable
    private val _isLoginSuccess: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isLoginSuccess: LiveData<Event<Boolean>> = _isLoginSuccess

    private val locationObserver = Observer<Boolean> { _isNetworkAvailable.value = it }

    init {
        networkConnectivity.observeForever(locationObserver)
    }

    override fun onCleared() {
        super.onCleared()
        networkConnectivity.removeObserver(locationObserver)
    }

    fun loginUser(loginBody: HashMap<String, Any>) {
        if (_isNetworkAvailable.value == true) viewModelScope.launch {
            authRepository.login(loginBody)
                .catch {
                    _isLoading.value = false
                    _errorMessage.value = R.string.something_went_wrong
                }
                .collect {
                    when (it) {
                        is RequestStatus.Waiting -> {
                            _isLoading.value = true
                        }
                        is RequestStatus.Success -> {
                            _isLoading.value = false
                            it.data?.let { data ->
                                sharedPrefs.token = data.accessToken
                                sharedPrefs.tokenExpiresAt = data.expiresAt
                                sharedPrefs.loggedUserName = data.userName
                                sharedPrefs.hasSessionExpired = false
                            }
                            _isLoginSuccess.value = Event(true)
                        }
                        is RequestStatus.Error -> {
                            _isLoading.value = false
                            _errorMessage.value = it.message
                        }
                    }
                }
        }
    }
}