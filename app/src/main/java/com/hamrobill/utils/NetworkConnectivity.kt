package com.hamrobill.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import javax.inject.Inject
import javax.inject.Named

class NetworkConnectivity @Inject constructor(@Named("Application") context: Context) :
        LiveData<Boolean>() {
    private val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(isNetworkAvailable())
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(isNetworkAvailable())
        }
    }

    override fun onActive() {
        super.onActive()
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun isNetworkAvailable(): Boolean {
        var isConnected = false
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                ) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        ) {
            isConnected = true
        }

        return isConnected
    }
}