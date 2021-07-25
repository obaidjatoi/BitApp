package com.android.bitapp.utils

var BASE_URL = "https://api-pub.bitfinex.com/"
var ALL_POSSIBLE_TRADING_PAIRS = "v2/conf/pub:list:pair:exchange"
var GET_PAIRS_DETAIL = "v2/tickers?symbols="
var SOCKET_BASE_ADDRESS = "wss://api-pub.bitfinex.com/ws/2"

const val SUBSCRIBE_EVENT = "subscribe"
const val UNSUBSCRIBE_EVENT = "unsubscribe"
const val TICKER_CHANNEL = "ticker"
const val TRADES_CHANNEL = "trades"
const val BTCUSD_PAIR = "tBTCUSD"
const val SUBSCRIBE_EVENT_SUCCESS = "subscribed"

const val TICKER_KEY = "ticker_key_current"
const val TRADE_KEY = "trade_key_current"
const val REFRESH_KEY = "refresh"






