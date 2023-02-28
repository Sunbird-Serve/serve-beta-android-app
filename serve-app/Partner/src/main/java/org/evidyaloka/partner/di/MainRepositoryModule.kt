package org.evidyaloka.partner.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.evidyaloka.core.partner.network.PartnerApiService
import org.evidyaloka.core.partner.repository.MainRepository
import org.evidyaloka.core.student.network.StudentApiService
import org.evidyaloka.core.student.repository.StudentRepository
import javax.inject.Singleton

/**
 * @author Madhankumar
 * created on 26-12-2020
 *
 */
@Module
@InstallIn(SingletonComponent::class)
object MainRepositoryModule {

    @Singleton
    @Provides
    fun providesMainRepository(
        partnerApiService: PartnerApiService,
        preferences: SharedPreferences
    ): MainRepository {
        return MainRepository(partnerApiService, preferences)
    }
}