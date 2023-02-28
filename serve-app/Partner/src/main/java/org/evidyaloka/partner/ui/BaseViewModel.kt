package org.evidyaloka.partner.ui

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.evidyaloka.core.helper.Resource
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class BaseViewModel : ViewModel() {

    private var networkErrorObservable: MutableLiveData<Int?> = MutableLiveData(); private set
    lateinit var apiResponse: MutableLiveData<*>
    private lateinit var callFunction: suspend () -> Resource<*>

    fun <T> requestWithRetry(context: CoroutineContext = EmptyCoroutineContext, request: suspend () -> Resource<T>): MutableLiveData<Resource<T>> {
        apiResponse = liveData(context) {
            callFunction =  request
            val response = request.invoke()
            if(response != null && response is Resource.GenericError && (response.code == -1 || response.code == 0 )){
                setErrorObservable(response.code!!)
            }
            emit(response)
        } as MutableLiveData<Resource<T>>
        return apiResponse as MutableLiveData<Resource<T>>
    }

    private fun setErrorObservable(code: Int){
        networkErrorObservable.postValue(code)
    }

    fun retry() {
        viewModelScope.launch {
            if(this@BaseViewModel::callFunction.isInitialized) {
                val response = callFunction.invoke()
                (apiResponse as MutableLiveData<Resource<*>>).postValue(response)
            }
        }
    }

    fun getNetworkErrorObserable(): MutableLiveData<Int?> {
        networkErrorObservable = MutableLiveData()
        return networkErrorObservable
    }
}