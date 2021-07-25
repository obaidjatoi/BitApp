package com.android.bitapp.models

data class SubscribeRequest(
    val event: String,
    val channel: String,
    val symbol: String
)
