package com.zidansyahidagrifasa0072.assesment3.module

import com.zidansyahidagrifasa0072.assesment3.data.repository.AuthRepository
import com.zidansyahidagrifasa0072.assesment3.data.repository.AuthRepositoryImpl
import com.zidansyahidagrifasa0072.assesment3.data.repository.ReviewRepository
import com.zidansyahidagrifasa0072.assesment3.data.repository.ReviewRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindReviewRepository(
        reviewRepositoryImpl: ReviewRepositoryImpl
    ): ReviewRepository
}