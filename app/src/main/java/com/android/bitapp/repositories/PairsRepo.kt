package com.android.bitapp.repositories

import com.android.bitapp.network.networkservices.PairsService
import com.android.bitapp.utils.getAllPairsUrl

object PairsRepo {

    private suspend fun getTradingPairNames(): List<String>? {
        try {
            val responseBody = PairsService.getInstance().getNames()
            responseBody?.let { list ->
                return if (list.isNotEmpty()) {
                    responseBody[0]
                } else {
                    null
                }
            }
        } catch (ee: java.lang.Exception) {
            return null
        }
        return null
    }

    suspend fun getAllTradingPairs(): List<List<String>?>? {
        val list = getTradingPairNames()
        list?.let { li ->
            try {
                val url = getAllPairsUrl(li)
                val responseBody = PairsService.getInstance().getAllPairs(url = url)

                responseBody?.let { list ->
                    return if (list.isNotEmpty()) {
                        return list
                    } else {
                        null
                    }
                }
            } catch (ee: java.lang.Exception) {
                return null
            }
            return null

        }

        return null
    }
}