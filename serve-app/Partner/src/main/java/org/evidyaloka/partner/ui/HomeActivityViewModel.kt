package org.evidyaloka.partner.ui

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.Generic
import org.evidyaloka.core.partner.model.Settings
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.core.partner.network.entity.ErrorData
import org.evidyaloka.core.partner.repository.MainRepository
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 26-12-2020
 *
 */
@HiltViewModel
class HomeActivityViewModel @Inject constructor(
        private val mainRepository: MainRepository,
        private val errorObservable:  MutableLiveData<ErrorData>
) : BaseViewModel() {


    var mErrorObservable:  MutableLiveData<ErrorData> = errorObservable

    val profileObserver: MutableLiveData<Resource<User?>> by lazy { MutableLiveData<Resource<User?>>().also { getProfile() } }

    fun getProgressObserable() = mainRepository.progressBarObservable


    fun getProfile() = viewModelScope.launch {
        profileObserver.value = mainRepository.getProfileData()
    }

    fun getProfile1() = requestWithRetry {
        mainRepository.getProfileData().also {

        }
    }

    fun getUserSchoolList(userType: PartnerConst.RoleType) = requestWithRetry {
        mainRepository.getUserSchoolList(userType)
    }

    fun getUser(): MutableLiveData<User?> {
        val liveData = MutableLiveData<User?>()
        try {
            mainRepository.getUser().let {
                liveData.value = it
            }

        } catch (e: Exception) {
            liveData.value = null
        }
        return liveData
    }

    fun saveUser(user: User): MutableLiveData<Resource<Generic>?> {
        val liveData = MutableLiveData<Resource<Generic>?>()
        try {
            mainRepository.saveUser(user)
            liveData.value = Resource.Success(Generic("1", "Successfully saved!"))
        } catch (e: Exception) {
            liveData.value = null
        }
        return liveData
    }

    fun clearUser(): MutableLiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        try {
            mainRepository.clearUser()
            liveData.value = true
        } catch (e: Exception) {
            liveData.value = false
        }
        return liveData
    }

    fun ping() = requestWithRetry {
        mainRepository.ping()
    }

    fun logout(data: Map<String, Any>) = requestWithRetry {
        mainRepository.logout(data)
    }

    fun getSettings(): MutableLiveData<Settings?> {
        val liveData = MutableLiveData<Settings?>()
        try {
            mainRepository.getSettings().let {
                liveData.value = it
            }

        } catch (e: Exception) {
            liveData.value = null
        }
        return liveData
    }

    fun saveSettings(settings: Settings): MutableLiveData<Resource<Generic>?> {
        val liveData = MutableLiveData<Resource<Generic>?>()
        try {
            mainRepository.saveSettings(settings)
            liveData.value = Resource.Success(Generic("1", "Successfully saved!"))
        } catch (e: Exception) {
            liveData.value = null
        }
        return liveData
    }

}