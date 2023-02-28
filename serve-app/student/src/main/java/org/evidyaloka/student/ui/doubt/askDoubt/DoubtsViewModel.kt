package org.evidyaloka.student.ui.doubt.askDoubt

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.student.ui.BaseViewModel
import java.io.InputStream
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 12-04-2021
 *
 */
@HiltViewModel
class DoubtsViewModel  @Inject constructor(
    private val mainRepository: StudentRepository
) : BaseViewModel()  {

    private var _progressPercentage = MutableLiveData<Int>()

    fun submitDoubt(data:HashMap<String, Any>) = requestWithRetry{
        mainRepository.submitDoubt(data)
    }

    fun editDoubt(data:HashMap<String, Any>) = requestWithRetry{
        mainRepository.editDoubt(data)
    }

    fun uploadFile( docType: Int, format:Int, filename:String, inputStream: InputStream) = requestWithRetry{
        mainRepository.uploadFile(docType,format,filename,inputStream) { progress: Int -> setProgress(progress)}
    }

    private fun setProgress(progress: Int) {
        _progressPercentage.postValue(progress)
    }

    fun uploadProgress(): LiveData<Int> = _progressPercentage
}