package org.evidyaloka.partner.ui.dsm

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.Settings
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.core.partner.repository.MainRepository
import org.evidyaloka.core.repository.UserDataSource
import org.evidyaloka.core.repository.UserDataSourceFactory
import org.evidyaloka.partner.ui.BaseViewModel
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
        private val mainRepository: MainRepository
) : BaseViewModel() {

    private val TAG = "DsmUserViewModel"
    private var _progressPercentage = MutableLiveData<Int>()
    //var userCountObserver = MutableLiveData<Int>()

    private var listUsers : LiveData<PagedList<User>> = MutableLiveData<PagedList<User>>()
    private var mutableLiveData = MutableLiveData<UserDataSource>()
    lateinit var config: PagedList.Config
    init {
        config = PagedList.Config.Builder()
            .setPageSize(PartnerConst.PAGINATION_COUNT)
            .setEnablePlaceholders(false)
            .build()
    }


    fun addUser(data: Map<String, Any>) = requestWithRetry {
        mainRepository.addUser(data)
    }

    fun getUserSchoolList() = requestWithRetry {
        mainRepository.getUserSchoolList(PartnerConst.RoleType.DSP)
    }


    fun getAllUsers(page: Int, searchKey : String): LiveData<PagedList<User>>{
        val factory : UserDataSourceFactory =
            UserDataSourceFactory(
                searchKey,
                mainRepository,
                viewModelScope
            )
        mutableLiveData = factory.userLiveDataSource()
        //userCountObserver = factory.userCountObserserver
        listUsers = LivePagedListBuilder(factory, config)
            .build()
        return listUsers
    }

    fun getUserDetails(userId: Int) = requestWithRetry {
        mainRepository.getUserDetails(userId)
    }

    fun getUser(userId: Int) = requestWithRetry {
        mainRepository.getUser(userId)
    }

    fun assignUserSchool(dsmId : Int, schoolId : Int) = requestWithRetry{
        mainRepository.assignUserSchool(dsmId, schoolId)
    }

    fun updateDsmUser(dsmId : Int, data: Map<String, Any>) = requestWithRetry {
        mainRepository.updateDsmUser(dsmId, data)
    }

    fun getUser(): MutableLiveData<Resource<User>?> {
        val liveData = MutableLiveData<Resource<User>?>()
        try {
            mainRepository.getUser().let {
                liveData.value = Resource.Success(it)
            }

        } catch (e: Exception) {
            liveData.value = null
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

}