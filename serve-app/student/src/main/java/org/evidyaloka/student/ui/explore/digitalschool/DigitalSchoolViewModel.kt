package org.evidyaloka.student.ui.explore.digitalschool

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.student.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class DigitalSchoolViewModel  @Inject constructor(
    private val mainRepository: StudentRepository
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