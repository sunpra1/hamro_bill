package com.hamrobill.di.module

import com.hamrobill.BuildConfig
import com.hamrobill.data.api.HamrobillAPIConsumer
import com.hamrobill.data.api.PrintApiConsumer
import com.hamrobill.di.anotation.RetrofitWithLocalEndPoint
import com.hamrobill.di.anotation.RetrofitWithRemoteEndPoint
import com.hamrobill.utility.SharedPreferenceStorage
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {
    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.BASIC
        }
    }

    @Singleton
    @Provides
    fun providesNetworkInterceptor(sharedPreferenceStorage: SharedPreferenceStorage): Interceptor =
        Interceptor { chain ->
            val request = chain.request()
            val newRequest = request.newBuilder()
                .header("authorization", sharedPreferenceStorage.token ?: "")
                .build()
            chain.proceed(newRequest)
        }

    @Singleton
    @Provides
    fun providesOkhttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        networkInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(networkInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    @RetrofitWithRemoteEndPoint
    fun providesRetrofitWithRemoteEndPoint(
        client: OkHttpClient,
        sharedPreferenceStorage: SharedPreferenceStorage
    ): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(sharedPreferenceStorage.remoteBaseUrl!!)
            .build()
    }

    @Singleton
    @Provides
    @RetrofitWithLocalEndPoint
    fun providesRetrofitWithRemoteLocalEndPoint(
        client: OkHttpClient,
        sharedPreferenceStorage: SharedPreferenceStorage
    ): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(sharedPreferenceStorage.localBaseUrl!!)
            .build()
    }

    @Singleton
    @Provides
    fun providesHamrobillApiConsumer(@RetrofitWithRemoteEndPoint retrofit: Retrofit): HamrobillAPIConsumer =
        retrofit.create(HamrobillAPIConsumer::class.java)

    @Singleton
    @Provides
    fun providesPrintApiConsumer(@RetrofitWithLocalEndPoint retrofit: Retrofit): PrintApiConsumer =
        retrofit.create(PrintApiConsumer::class.java)

}