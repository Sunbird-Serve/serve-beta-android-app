package org.evidyaloka.student.ui.bookmark

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.student.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel  @Inject constructor(
    private val repo: StudentRepository
) : BaseViewModel(){

    fun getAllStudents() = repo.getAllStudents()

    fun getSelectedStudentBookmarks() = repo.getStudentBookmarks()

    fun getContentByStudentId(studentId: Int) = repo.getStudentWithContent(studentId)

    fun getAllDownloadContentDeatils() = repo.getAllDownloadContentDetails()

    fun deleteByContentId(contentId: Long) = liveData(Dispatchers.IO) {
        emit(repo.deleteByContentId(contentId))
    }

    fun updateContentDownloadStatus(data: HashMap<String, Any?>) = requestWithRetry {
        repo.updateContentDownloadStatus(data)
    }
}