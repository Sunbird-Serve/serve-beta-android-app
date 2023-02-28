package org.evidyaloka.partner.ui.profile

import androidx.lifecycle.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.Generic
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.core.partner.repository.MainRepository
import org.evidyaloka.partner.ui.BaseViewModel
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
        private val mainRepository: MainRepository
) : BaseViewModel() {
    private var _progressPercentage = MutableLiveData<Int>()

    fun getProfile() = requestWithRetry {
        mainRepository.getProfileData()
    }

    fun updateProfile(data: Map<String, Any>) = requestWithRetry {
        mainRepository.updateProfileData(data)
    }

    fun saveUser(user: User): MutableLiveData<Resource<Generic>> {
        val liveData = MutableLiveData<Resource<Generic>>()
        try {
            mainRepository.saveUser(user)
            liveData.value = Resource.Success(Generic("1","Successfully saved!"))
        }catch (e:Exception){
            FirebaseCrashlytics.getInstance().recordException(e)
            liveData.value = Resource.GenericError(0,null,null)
        }
        return liveData
    }

    fun getUser(): MutableLiveData<User?> {
        val liveData = MutableLiveData<User?>()
        try {
            mainRepository.getUser().let {
                liveData.value = it
            }

        }catch (e:Exception){
            FirebaseCrashlytics.getInstance().recordException(e)
            liveData.value =  null
        }
        return liveData
    }

    fun updateUser(user: User): MutableLiveData<String> {
        val liveData = MutableLiveData<String>()
        try {
            mainRepository.updateUser(user).let {
                liveData.value = "Success"
            }

        }catch (e:Exception){
            FirebaseCrashlytics.getInstance().recordException(e)
            liveData.value = "Failed"
        }
        return liveData
    }

    fun uploadFile( docType: Int, format:Int, filename:String, inputStream: InputStream) = requestWithRetry{
        mainRepository.uploadFile(docType,format,filename,inputStream) { progress: Int -> setProgress(progress)}
    }

    private fun setProgress(progress: Int) {
        _progressPercentage.postValue(progress)
    }
    fun uploadProgress(): LiveData<Int> = _progressPercentage

}