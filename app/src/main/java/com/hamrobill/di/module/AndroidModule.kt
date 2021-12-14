package com.hamrobill.di.module

import android.app.AlarmManager
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class AndroidModule {
    @Singleton
    @Provides
    fun providesAlarmManager(@Named("Application") context: Context): AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}