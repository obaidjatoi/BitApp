package com.android.bitapp.di

import android.app.Activity
import android.content.Context
import com.android.bitapp.BitApplication
import com.android.bitapp.network.networkservices.SocketService
import com.android.bitapp.utils.SOCKET_BASE_ADDRESS
import com.android.bitapp.view.activities.MainActivity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tinder.scarlet.MessageAdapter
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun providesApplication(@ApplicationContext context: Context): BitApplication {
        return context as BitApplication
    }

    @Provides
    fun provideMoshi(adapterFactory: KotlinJsonAdapterFactory): Moshi {
        return Moshi.Builder()
            .add(adapterFactory)
            .build()
    }

    @Provides
    fun provideMoshiAdapterFactory(moshi: Moshi): MoshiMessageAdapter.Factory {
        return MoshiMessageAdapter.Factory(moshi)
    }

    @Provides
    fun provideJsonAdapterFactory(): KotlinJsonAdapterFactory {
        return KotlinJsonAdapterFactory()
    }

    @Provides
    fun providesRxJava2StreamAdapterFactory(): RxJava2StreamAdapterFactory {
        return RxJava2StreamAdapterFactory()
    }


    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideScarlet(
        application: BitApplication,
        client: OkHttpClient,
        moshiMessageAdapter: MoshiMessageAdapter.Factory,
        streamAdapterFactory: RxJava2StreamAdapterFactory
    ): Scarlet {
        return Scarlet.Builder()
            .webSocketFactory(client.newWebSocketFactory(SOCKET_BASE_ADDRESS))
            .addMessageAdapterFactory(moshiMessageAdapter)
            .addStreamAdapterFactory(streamAdapterFactory)
            .lifecycle(AndroidLifecycle.ofApplicationForeground(application))
            .build()
    }

    @Provides
    fun providesSocketService(scarlet: Scarlet): SocketService {
        return scarlet.create()
    }
}