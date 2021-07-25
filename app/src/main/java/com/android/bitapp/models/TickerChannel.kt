package com.android.bitapp.models
/*
*
* this class will contain the data we get from the socket after successful subscription to any of the pair
* for viewing the meta data consult this doc -> https://docs.bitfinex.com/reference#ws-public-ticker
*
* */

data class TickerChannel(
    var channel: Double,
    var bid: String, // Price of last highest bid
    var bidSize: String,
    var ask: String, // Price of last lowest ask
    var askSize: String,
    var dailyChange: String, // Amount that the last price has changed since yesterday
    var dailyChangeRelative: String, // Relative price change since yesterday (*100 for percentage change)
    var lastPrice: String,
    var volume: String,
    var high: String, // Daily high
    var low: String //Daily low
)