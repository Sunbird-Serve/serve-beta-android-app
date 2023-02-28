package org.evidyaloka.student.ui.student

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.student.ui.BaseViewModel
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class StudentRegistrationViewModel  @Inject constructor(
    private val mainRepository: StudentRepository
) : BaseViewModel() {

    private var _progressPercentage = MutableLiveData<Int>()

    fun getCourseProviders() = liveData {
        emit(mainRepository.getCourseProviders())
    }

    fun getSettings() = liveData {
        emit(mainRepository.getSettings())
    }

    fun getLanguages() = liveData {
        emit(mainRepository.getLanguages())
    }

    fun uploadFile( docType: Int, format:Int, filename:String, inputStream: InputStream) = liveData{
        emit(mainRepository.uploadFile(docType,format,filename,inputStream,null) { progress: Int -> setProgress(progress)})
    }

    private fun setProgress(progress: Int) {
        _progressPercentage.postValue(progress)
    }

    fun uploadProgress(): LiveData<Int> = _progressPercentage

    fun enrollStudents(data: Map<String, Any>) = liveData {
        emit(mainRepository.enrollStudents(data))
    }

    fun getGrades(courseProviderId: Int) = liveData {
        emit(mainRepository.getGrades(courseProviderId))
    }

}