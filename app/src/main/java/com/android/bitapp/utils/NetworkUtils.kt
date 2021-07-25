package com.android.bitapp.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build

fun hasInternetConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    if(Build.VERSION.SDK_INT in 19..22) {
        val netInfo : NetworkInfo? = connectivityManager.activeNetworkInfo
        netInfo?.let {
            return it.isConnected
        } ?: run {
            return false
        }
    } else if(Build.VERSION.SDK_INT >= 23){
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    return false
}