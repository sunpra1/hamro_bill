package com.hamrobill.di.module

import com.hamrobill.BuildConfig
import com.hamrobill.data.api.HamrobillAPIConsumer
import com.hamrobill.utils.SharedPreferenceStorage
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
    fun providesRetrofitBuilder(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.API_BASE_URL)
                .build()
    }

    @Singleton
    @Provides
    fun providesHamrobillApiConsumer(retrofit: Retrofit): HamrobillAPIConsumer =
            retrofit.create(HamrobillAPIConsumer::class.java)

}