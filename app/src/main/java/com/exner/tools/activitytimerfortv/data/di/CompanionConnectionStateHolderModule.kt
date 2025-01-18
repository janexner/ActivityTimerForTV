package com.exner.tools.activitytimerfortv.data.di

import com.exner.tools.activitytimerfortv.state.CompanionConnectionStateHolder
import com.exner.tools.activitytimerfortv.state.CompanionConnectionStateHolderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CompanionConnectionStateHolderModule {

    @Binds
    abstract fun bindCompanionConnectionStateHolder(
        companionConnectionStateHolderImpl: CompanionConnectionStateHolderImpl
    ) : CompanionConnectionStateHolder
}