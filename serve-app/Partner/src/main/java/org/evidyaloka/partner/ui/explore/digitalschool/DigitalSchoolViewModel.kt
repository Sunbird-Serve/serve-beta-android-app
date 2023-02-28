package org.evidyaloka.partner.ui.explore.digitalschool

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.partner.repository.MainRepository
import org.evidyaloka.partner.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class DigitalSchoolViewModel  @Inject constructor(
    private val mainRepository: MainRepository
) : BaseViewModel() {

    fun getCourseProviders() = liveData {
        emit(mainRepository.getCourseProviders())
    }

    fun getSettings() = liveData {
        emit(mainRepository.getSettings())
    }

    fun getExploreDigitalSchool(grade: Int, CourseProviderId: Int) = liveData{
        emit(mainRepository.getExploreDigitalSchool(grade, CourseProviderId))
    }

    fun getGrades(courseProviderId: Int) = liveData {
        emit(mainRepository.getGrades(courseProviderId))
    }
}