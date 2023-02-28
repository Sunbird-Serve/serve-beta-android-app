package org.evidyaloka.student.ui.explore

import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.helper.ErrorData
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.datasource.pincode.PincodeDataSource
import org.evidyaloka.core.student.datasource.pincode.PincodeDataSourceFactory
import org.evidyaloka.core.student.model.Pincode
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.student.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val mainRepository: StudentRepository
) : BaseViewModel() {

    private val TAG: String? = "OnboardingViewModel"
    private var pincodeSourceLiveData = MutableLiveData<PincodeDataSource>()
    lateinit var config: PagedList.Config
    private var listPincodes: LiveData<PagedList<Pincode>> = MutableLiveData<PagedList<Pincode>>()

    init {
        config = PagedList.Config.Builder()
            .setPageSize(CommonConst.PINCODE_PAGINATION_COUNT)
            .setEnablePlaceholders(false)
            .build()
    }

    fun getLocations(geocoder: Geocoder, latitude: Double, longitude: Double) =
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            var addresses = listOf<Address>()
            try {
                viewModelScope.async {
                    addresses = geocoder.getFromLocation(latitude, longitude, 10)
                }.await()
                emit(Resource.Success(addresses))
            } catch (e: Exception) {
                emit(
                    Resource.GenericError(
                        1,
                        ErrorData(1, "Unable to fetch location from Geocoder"),
                        1
                    )
                )
            }
        }

    fun getPincodesPagination(searchText: String): LiveData<PagedList<Pincode>> {
        val factory = PincodeDataSourceFactory(searchText, mainRepository, viewModelScope)
        pincodeSourceLiveData = factory.pincodeDataSource()
        listPincodes = LivePagedListBuilder(factory, config).build()
        return listPincodes
    }

    fun getPincodes(searchText: String) = liveData {
        emit(mainRepository.getPincodes(searchText, 0))
    }

    fun getCourseProviders() = liveData {
        emit(mainRepository.getCourseProviders())
    }

    fun getGrades(courseProviderId: Int) = liveData {
        emit(mainRepository.getGrades(courseProviderId))
    }

    fun ping() = liveData {
        val response = mainRepository.ping()
        emit(response)
    }

    fun updateLocationDetails(data : HashMap<String, Any>) = liveData {
        val response = mainRepository.updateLocationDetails(data)
        emit(response)
    }
}