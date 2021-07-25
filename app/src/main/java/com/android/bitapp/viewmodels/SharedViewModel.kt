package com.android.bitapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.bitapp.models.*
import com.android.bitapp.network.networkservices.SocketService
import com.android.bitapp.repositories.PairsRepo
import com.android.bitapp.utils.*
import com.google.gson.Gson
import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    var scarletClient: SocketService,
    application: Application
) :
    AndroidViewModel(application) {
    private var TAG = "SharedViewModel"
    var screenState: MutableLiveData<ScreenState> = MutableLiveData()
    var data: List<AllPairItem>? = ArrayList()
    private var isSocketConnected = false
    var currentSubscribedData = HashMap<String, MessageSubscribeSchema>()
    var observerIsUp = false
    var socketConnectedIndication = MutableLiveData(false)
    var currentSubscribePairItem = MutableLiveData<AllPairItem>()
    var currentSubscribeTickerData = MutableLiveData<TickerChannel>()
    var currentSubscribeTradeData = MutableLiveData<Trade>()
    val tradesList = ArrayList<Trade>()

    fun getTradingPairs() {

        if (!hasInternetConnection(getApplication())) {
            screenState.value = ScreenState.NETWORK_ERROR
            return
        }

        screenState.value = ScreenState.DATA_LOADING

        viewModelScope.launch {
            val response = PairsRepo.getAllTradingPairs()
            if (response.isNullOrEmpty()) {
                notifyError()
            } else {

                data = response.map { resp ->
                    resp!!.toAllPairItem()
                }
                screenState.value = ScreenState.DATA_LOADED
            }
        }
    }

    fun subscribeFlow(item: AllPairItem) {
        currentSubscribePairItem.postValue(item)
        if (isSocketConnected) {
            subscribeTickerChannel(item)
        } else {
            connectSocket(item)
        }
    }

    private fun observeTickerAndTrade() {
        if (!observerIsUp) {
            var observeTicker = scarletClient.observeTicker()
                .subscribe { list ->
                    if (!list.isNullOrEmpty()) {
                        if (list.size > 1 && (list[1] !is String) && (list[1] as List<*>).size == 10) {
                            val xyz = list.toTickerData()
                            currentSubscribeTickerData.postValue(xyz)
                        } else if (list.size > 2 && (list[2] as List<*>).size == 4) {
                            val xyz = list.toTradeData()
                            currentSubscribeTradeData.postValue(xyz)
                        }
                    }
                }
        }
    }


    private fun subscribeTickerChannel(item: AllPairItem) {
        unsubscribePreviousTickerChannel()
        scarletClient.sendSubscribeRequest(
            SubscribeRequest(
                SUBSCRIBE_EVENT,
                TICKER_CHANNEL,
                item.symbol
            )
        )

        scarletClient.sendSubscribeRequest(
            SubscribeRequest(
                SUBSCRIBE_EVENT,
                TRADES_CHANNEL,
                item.symbol
            )
        )

        observeTickerAndTrade()
    }

    private fun unsubscribePreviousTickerChannel() {
        currentSubscribedData[TICKER_KEY]?.let { previous ->
            val unSubscribeTickerRequest =
                UnsubscribeTicker(UNSUBSCRIBE_EVENT, previous.chanId)
            scarletClient.sendUnSubscribeRequest(unSubscribeTickerRequest)
        }

        currentSubscribedData[TRADE_KEY]?.let { previous ->
            val unSubscribeTickerRequest =
                UnsubscribeTicker(UNSUBSCRIBE_EVENT, previous.chanId)
            scarletClient.sendUnSubscribeRequest(unSubscribeTickerRequest)
            tradesList.clear()

            currentSubscribeTradeData.postValue(refreshTradeSignal())
        }
    }

    private fun refreshTradeSignal(): Trade {
        return Trade(0.0, REFRESH_KEY, "", 0.0, "")
    }

    private fun notifyError() {
        screenState.value = ScreenState.ERROR
    }

    fun connectSocket(item: AllPairItem?) {
        var subscribeEvent = scarletClient.openWebSocketEvent().subscribe {
            when (it) {
                is WebSocket.Event.OnConnectionOpened<*> -> {
                    Log.d(
                        TAG,
                        "OnConnectionOpened "
                    )
                    isSocketConnected = true
                    socketConnectedIndication.postValue(true)
                    item?.let { checkedItem ->
                        subscribeTickerChannel(checkedItem)
                    }
                }
                is WebSocket.Event.OnConnectionClosed, is WebSocket.Event.OnConnectionFailed -> {
                    isSocketConnected = false
                    socketConnectedIndication.postValue(false)
                    Log.d(
                        TAG,
                        "OnConnectionClosed || OnConnectionFailed"
                    )
                }
                is WebSocket.Event.OnMessageReceived -> {
                    // messages are received here
                    val message = it.message
                    processMessage((message as Message.Text).value)
                }
                else -> {
                    // on connection closing
                    Log.d(
                        TAG,
                        "OnConnection Closing"
                    )
                }
            }
        }
    }

    private fun processMessage(messageText: String) {
        val gson = Gson()
        try {
            val message = gson.fromJson(messageText, MessageSubscribeSchema::class.java)
            message?.let { mes ->
                if (mes.event.equals(SUBSCRIBE_EVENT_SUCCESS, ignoreCase = true)) {
                    if (mes.channel.equals(TICKER_CHANNEL, ignoreCase = true)) {
                        currentSubscribedData[TICKER_KEY] = mes
                    } else if (mes.channel.equals(TRADES_CHANNEL, ignoreCase = true)) {
                        currentSubscribedData[TRADE_KEY] = mes
                    }
                }
            }
        } catch (ee: Exception) {
            //  exception can be thrown for other type of messages, like unsubscribe etc
            //  we just need subscribe for now that's why just catching this and not doing anything.
            Log.d(
                TAG,
                "processMessage exception for other type of classes"
            )
        }
    }

    fun unsubscribeFromALL() {
        unsubscribePreviousTickerChannel()
        tradesList.clear()
    }
}