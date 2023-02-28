package org.evidyaloka.student.ui.session

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.student.repository.StudentRepository
import javax.inject.Inject

@HiltViewModel
class LiveSessionViewModel @Inject constructor(
    private val mainRepository: StudentRepository
) : ViewModel() {

    fun sendAttendance(data: HashMap<String, Any?>) = liveData {
        emit(mainRepository.sendAttendance(data))
    }
}