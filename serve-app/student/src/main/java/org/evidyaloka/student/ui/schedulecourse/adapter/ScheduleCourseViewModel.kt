package org.evidyaloka.student.ui.schedulecourse.adapter

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.repository.StudentRepository
import javax.inject.Inject

@HiltViewModel
class ScheduleCourseViewModel @Inject constructor(
    private val mainRepository: StudentRepository
) : ViewModel() {

    fun createSchedule(data: Map<String, Any>) = liveData {
        emit(mainRepository.generateOTP(data))
    }

}