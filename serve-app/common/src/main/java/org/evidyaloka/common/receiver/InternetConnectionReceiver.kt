package org.evidyaloka.common.receiver

import android.app.Activity
import android.content.Context
import android.net.*
import android.os.Build
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent


class InternetConnectionReceiver<T>(
    val context: T,
    val isConnected: (isConnected: Boolean) -> Unit
) : LifecycleObserver where  T : LifecycleOwner {

    private val TAG = "InternetConnectionReceiver"
    private val appContext =
        if (context is Fragment) context.requireContext().applicationContext else (context as Activity).applicationContext
    val connectivityManager =
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isConnected(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            isConnected(false)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            isConnected(false)
        }

    }

    init {
        context.lifecycle.addObserver(this)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        Log.e(TAG, "@OnLifecycleEvent(Lifecycle.Event.ON_START)")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        isConnected(activeNetwork?.isConnectedOrConnecting == true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        Log.e(TAG, "@OnLifecycleEvent(Lifecycle.Event.ON_STOP)")
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            context.lifecycle.removeObserver(this)
        }
    }
}