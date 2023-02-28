package org.evidyaloka.student.ui.downloads

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.student.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel  @Inject constructor(
    private val repo: StudentRepository
) : BaseViewModel(){

    fun getAllStudents() = repo.getAllStudents()

    fun getSelectedStudentContent() = repo.getSelectedStudentContent()

    fun getContentByStudentId(studentId: Int) = repo.getStudentWithContent(studentId)

    fun getAllDownloadContentDeatils() = repo.getAllDownloadContentDetails()

    fun deleteByContentId(contentId: Long) = liveData(Dispatchers.IO) {
        emit(repo.deleteByContentId(contentId))
    }

    fun updateContentDownloadStatus(data: HashMap<String, Any?>) = requestWithRetry {
        repo.updateContentDownloadStatus(data)
    }
}