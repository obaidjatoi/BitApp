package com.android.bitapp.network.networkservices

import com.android.bitapp.models.SubscribeRequest
import com.android.bitapp.models.UnsubscribeTicker
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable

interface SocketService {
    @Receive
    fun openWebSocketEvent(): Flowable<WebSocket.Event>

    @Send
    fun sendSubscribeRequest(subscribeRequest: SubscribeRequest)

    @Send
    fun sendUnSubscribeRequest(unSubscribeTickerRequest: UnsubscribeTicker)

    @Receive
    fun observeTicker(): Flowable<List<Any>>
}