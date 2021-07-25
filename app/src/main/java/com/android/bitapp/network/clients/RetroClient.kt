package com.android.bitapp.network.clients

import com.android.bitapp.utils.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroClient {
    val ottRetrofit by lazy { invoke(BASE_URL) }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .build()
    }

    operator fun invoke(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

}