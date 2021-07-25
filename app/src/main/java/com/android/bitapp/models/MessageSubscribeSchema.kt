package com.android.bitapp.models

data class MessageSubscribeSchema(
    var event: String,
    var channel: String,
    var chanId: Double,
    var symbol: String,
    var pair: String,
)
