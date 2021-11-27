package com.hamrobill.di.anotation

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitWithLocalEndPoint


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitWithRemoteEndPoint