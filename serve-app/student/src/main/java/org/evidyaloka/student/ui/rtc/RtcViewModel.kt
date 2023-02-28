package org.evidyaloka.student.ui.rtc

import android.app.DownloadManager
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.rtc.ContentDetail
import org.evidyaloka.core.model.rtc.ContentAttributes
import org.evidyaloka.core.student.database.entity.BookmarkEntity
import org.evidyaloka.core.student.database.entity.CourseContentEntity
import org.evidyaloka.core.student.model.Student
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.student.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class RtcViewModel @Inject constructor(
    private val mainRepository: StudentRepository
) : BaseViewModel() {

    private val videoPlayer: MutableLiveData<ContentAttributes> = MutableLiveData()

    fun playVideo(item: ContentAttributes) {
        videoPlayer.value = item
    }

    fun getVideoUrl(): MutableLiveData<ContentAttributes> {
        return videoPlayer
    }

    fun getSessionDetails(
        sessionId: Int,
        offeringId: Int,
        topicId: Int,
        timetableId: Int,
        subtopicId: Int
    ) = liveData(
        Dispatchers.IO
    ) {
        val response =
            mainRepository.getCoursePlayer(sessionId, offeringId, topicId, timetableId, subtopicId)
        when (response) {
            is Resource.Success -> {
                response.data?.let {
                    var data: ContentDetail = it
                    data.videos.forEach { content ->
                        mainRepository.downloadContentDetailsByContentId(content.id.toLong())
                            ?.let { entity ->
                                setLocalUrlAndStatus(content.id, data.videos, entity)
                            }
                    }
                    data.worksheets.forEach { content ->
                        mainRepository.downloadContentDetailsByContentId(content.id.toLong())
                            ?.let { entity ->
                                setLocalUrlAndStatus(content.id, data.worksheets, entity)
                            }
                    }
                    emit(Resource.Success(data))
                    return@liveData
                }
                emit(response)
            }
            else -> {
                emit(response)
            }
        }
    }

    private fun setLocalUrlAndStatus(
        contentId: Int,
        data: MutableList<ContentAttributes>,
        entity: CourseContentEntity
    ) {
        if (entity.downloadStatus != DownloadManager.STATUS_FAILED) {
            data.find { it.id == contentId }?.localUrl =
                entity.localUrl
            data.find { it.id == contentId }?.downloadStatus = entity.downloadStatus
        }
    }

    fun sendAttendance(data: HashMap<String, Any?>) = liveData {
        emit(mainRepository.sendAttendance(data))
    }

    fun updateViewStatus(data: HashMap<String, Any?>) = liveData {
        emit(mainRepository.updateViewStatus(data))
    }

    fun getAllDownloadContentDeatils() = mainRepository.getAllDownloadContentDetails()

    fun updateSessionRating(data: HashMap<String, Any?>) = requestWithRetry {
        mainRepository.updateSessionRating(data)
    }

    fun insertContentDetail(detail: CourseContentEntity) = liveData(Dispatchers.IO) {
        emit(mainRepository.insertContentDetails(detail))
    }

    fun getDownloadContentDetailsByContentId(contentId: Long) = liveData(Dispatchers.IO) {
        emit(mainRepository.getDownloadContentDetailsByContentId(contentId))
    }

    fun updateDownloadContentDetails(id: Long, status: Int?, reason: String? = "") =
        liveData(Dispatchers.IO) {
            emit(mainRepository.updateDownloadContentStatus(id, status, reason))
        }

    fun deleteByContentId(contentId: Long) = liveData(Dispatchers.IO) {
        emit(mainRepository.deleteByContentId(contentId))
    }

    fun updateContentDownloadStatus(data: HashMap<String, Any?>) = requestWithRetry {
        mainRepository.updateContentDownloadStatus(data)
    }

    fun setSelectedStudent(student: Student) = liveData {
        mainRepository.setSelectedStudent(student)
        emit(true)
    }

    fun getStudentById(id: Int) = mainRepository.getStudentById(id)

    fun updateOfflineProgress(contentId: Long, progress: Long) = liveData(Dispatchers.IO) {
        emit(mainRepository.updateOfflineProgress(contentId, progress))
    }

    //Bookmark
    fun insertBookmark(detail: BookmarkEntity) = liveData(Dispatchers.IO) {
        emit(mainRepository.insertBookmark(detail))
    }

    fun deleteBookmark(detail: BookmarkEntity) = liveData(Dispatchers.IO) {
        emit(mainRepository.deleteBookmark(detail))
    }

    fun getBookmarkBySubtopicId(subTopicId: Int) = liveData(Dispatchers.IO) {
        emit(mainRepository.getBookmarkBySubtopicId(subTopicId))
    }
}