package org.evidyaloka.student.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.evidyaloka.core.student.database.dao.CourseContentDAO
import org.evidyaloka.core.student.network.StudentApiService
import org.evidyaloka.core.student.repository.StudentRepository
import javax.inject.Singleton

/**
 * @author Madhankumar
 * created on 05-03-2021
 *
 */
@Module
@InstallIn(SingletonComponent::class)
object StudentRepositoryModule {
    @Singleton
    @Provides
    fun providesMainRepository(apiService: StudentApiService, courseContentDAO: CourseContentDAO, preferences: SharedPreferences): StudentRepository {
        return StudentRepository(apiService,courseContentDAO,preferences)
    }
}