package org.evidyaloka.partner.ui.explore.rtc

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.partner.repository.MainRepository
import org.evidyaloka.core.model.rtc.ContentAttributes
import org.evidyaloka.partner.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class RtcViewModel @Inject constructor(
    private val mainRepository: MainRepository
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

}