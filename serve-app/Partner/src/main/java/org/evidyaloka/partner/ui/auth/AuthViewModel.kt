package org.evidyaloka.partner.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.Generic
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.core.partner.repository.MainRepository
import org.evidyaloka.partner.ui.BaseViewModel
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 29-12-2020
 *
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val mainRepository: MainRepository
): BaseViewModel() {

    /**
     * Function to invoke login api and convert response to live data
     */
    fun doLogin(data: Map<String,Any>) = requestWithRetry{
        mainRepository.doLogin(data)
    }

    /**
     * Function to do registration and convert response to live data
     */
    fun doRegister(data: Map<String, Any>) = requestWithRetry{
        mainRepository.doRegister(data)
    }

    /**
     * Function to change login password and convert response to live data
     */
    fun changePassword(data: Map<String, Any>) = requestWithRetry{
        mainRepository.changePassword(data)
    }

    /**
     * Function to save User to shared preference
     */
    fun saveUser(user: User): MutableLiveData<Resource<Generic>> {
        val liveData = MutableLiveData<Resource<Generic>>()
        try {
            mainRepository.saveUser(user)
            liveData.value = Resource.Success(Generic("1","Successfully saved!"))
        }catch (e:Exception){
            liveData.value = Resource.GenericError(0,null,null)
        }
        return liveData
    }

    /**
     * This function fetches logged in User from SharedPreference
     * @return MutableLiveData<User?>
     */
    fun getUser(): MutableLiveData<User?> {
        val liveData = MutableLiveData<User?>()
        try {
            mainRepository.getUser().let {
                liveData.value = it
            }

        }catch (e:Exception){
            liveData.value =  null
        }
        return liveData
    }

    fun clearUser(){
        mainRepository.clearUser()
    }

}