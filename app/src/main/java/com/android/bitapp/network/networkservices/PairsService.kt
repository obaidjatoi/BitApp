package com.android.bitapp.network.networkservices

import com.android.bitapp.network.clients.RetroClient
import com.android.bitapp.utils.ALL_POSSIBLE_TRADING_PAIRS
import retrofit2.http.GET
import retrofit2.http.Url

interface PairsService {
    @GET
    suspend fun getNames(@Url() url: String = ALL_POSSIBLE_TRADING_PAIRS): List<List<String>?>?

    @GET
    suspend fun getAllPairs(
        @Url() url: String
    ): List<List<String>?>?

    companion object {
        fun getInstance(): PairsService {
            return RetroClient.ottRetrofit.create(PairsService::class.java)
        }
    }
}