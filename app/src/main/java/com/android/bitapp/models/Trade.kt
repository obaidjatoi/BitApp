package com.android.bitapp.models

data class Trade(
    var channel: Double,
    var id: String,
    var milliSecondStamp: String, // millisecond time stamp
    var amount: Double, // Amount bought (positive) or sold (negative)
    var price: String, // Price at which the trade was executed

)
