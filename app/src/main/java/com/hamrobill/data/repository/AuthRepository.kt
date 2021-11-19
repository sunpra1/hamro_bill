package com.hamrobill.data.repository

import android.util.Log
import com.hamrobill.R
import com.hamrobill.data.api.HamrobillAPIConsumer
import com.hamrobill.utils.RequestStatus
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val hamrobillAPIConsumer: HamrobillAPIConsumer) {
    companion object {
        private const val TAG = "AuthRepository"
    }

    fun login(loginBody: HashMap<String, Any>) = flow {
        emit(RequestStatus.Waiting)
        val response = hamrobillAPIConsumer.get(loginBody)
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()))
        } else {
            Log.d(
                TAG,
                "login: ${response.errorBody()?.byteStream()?.reader()?.readText()}"
            )
            emit(
                RequestStatus.Error(
                    R.string.unable_to_verify_credential
                )
            )
        }
    }
}