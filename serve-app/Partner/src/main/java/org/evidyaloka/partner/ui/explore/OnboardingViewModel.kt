package org.evidyaloka.partner.ui.explore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.repository.MainRepository
import org.evidyaloka.partner.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : BaseViewModel() {

    private val TAG: String? = "OnboardingViewModel"




    fun getCourseProviders() = liveData {
        emit(mainRepository.getCourseProviders())
    }

    fun ping() = liveData {
        val response = mainRepository.ping()
        if(response is Resource.Success){
            response.data?.let { mainRepository.saveSettings(it) }
        }
        emit(response)
    }

    fun getGrades(courseProviderId: Int) = liveData {
        emit(mainRepository.getGrades(courseProviderId))
    }
}