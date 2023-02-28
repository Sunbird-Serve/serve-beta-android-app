package org.evidyaloka.student.ui.explore.rtc

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.model.rtc.ContentAttributes
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.student.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class RtcViewModel @Inject constructor(
    private val mainRepository: StudentRepository
) : BaseViewModel() {

    private val videoPlayer: MutableLiveData<ContentAttributes> = MutableLiveData()

    fun playVideo(item: ContentAttributes){
        videoPlayer.value = item
    }

    fun getVideoUrl(): MutableLiveData<ContentAttributes> {
        return videoPlayer
    }

    fun getSessionDetails(sessionId:Int?, offeringId:Int, topicId : Int, timetableId : Int?, subtopicId: Int) = requestWithRetry {
        mainRepository.getCoursePlayer(sessionId, offeringId, topicId, timetableId, subtopicId)
    }

    fun sendAttendance(data: HashMap<String, Any?>) = requestWithRetry {
        mainRepository.sendAttendance(data)
    }

    fun updateVideoProgress(data: HashMap<String, Any?>) = requestWithRetry {
        mainRepository.updateViewStatus(data)
    }

    fun updateSessionRating(data: HashMap<String, Any?>) = requestWithRetry{
        mainRepository.updateSessionRating(data)
    }
}