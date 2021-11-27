package com.hamrobill.utils

import android.content.Context
import javax.inject.Inject
import javax.inject.Named

class SharedPreferenceStorage @Inject constructor(@Named("Application") context: Context) {
    companion object {
        private const val PREF_NAME = "HAMRO_BILL_SHARED_PREF"
        private const val TOKEN_VALUE: String = "TOKEN_VALUE"
        private const val TOKEN_EXPIRES_AT: String = "TOKEN_EXPIRES_AT"
        private const val REMOTE_BASE_URL: String = "REMOTE_BASE_URL"
        private const val LOCAL_BASE_URL: String = "LOCAL_BASE_URL"
    }

    private val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var token: String? = null
        get() = sharedPreferences.getString(TOKEN_VALUE, null)?.let { "Bearer $it" }
        set(value) = sharedPreferences.edit().putString(TOKEN_VALUE, value).apply()
            .also { field = value }

    var tokenExpiresAt: String? = null
        get() = sharedPreferences.getString(TOKEN_EXPIRES_AT, null)
        set(value) = sharedPreferences.edit().putString(TOKEN_EXPIRES_AT, value).apply()
            .also { field = value }

    var localBaseUrl: String? = null
        get() = sharedPreferences.getString(LOCAL_BASE_URL, null)
        set(value) = sharedPreferences.edit().putString(LOCAL_BASE_URL, value).apply()
            .also { field = value }

    var remoteBaseUrl: String? = null
        get() = sharedPreferences.getString(REMOTE_BASE_URL, null)
        set(value) = sharedPreferences.edit().putString(REMOTE_BASE_URL, value).apply()
            .also { field = value }

}