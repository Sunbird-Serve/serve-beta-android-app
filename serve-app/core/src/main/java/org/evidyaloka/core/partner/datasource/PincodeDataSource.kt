package org.evidyaloka.core.partner.datasource

import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.Pincode
import org.evidyaloka.core.partner.repository.MainRepository

class PincodeDataSource(
    private val stateId: Int,
    private val searchQuery: String?,
    private val repo: MainRepository,
    private val scope: CoroutineScope
) : PageKeyedDataSource<Int, Pincode>() {

    private var searchJob: Job? = null
    private fun getPincode(callback: LoadInitialCallback<Int, Pincode>) {
        searchJob?.cancel()
        searchJob = scope.launch {
            repo.getPincode( stateId,searchQuery, 0)?.let {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let { list ->
                            //userCountObserserver.postValue(list.size)
                            callback.onResult(list, null, 1)
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun getMorePincode(
        pageNo: Int,
        previousOrNextPageNo: Int,
        callback: LoadCallback<Int, Pincode>
    ) {
        searchJob?.cancel()
        searchJob = scope.launch {
            repo.getPincode(stateId, searchQuery, pageNo)?.let {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let { list -> callback.onResult(list, previousOrNextPageNo) }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Pincode>
    ) {
        getPincode(callback)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Pincode>) {
        val nextPageNo = params.key + 1
        getMorePincode(params.key, nextPageNo, callback)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Pincode>) {
        val previousPageNo = if (params.key > 1) params.key - 1 else 0
        getMorePincode(params.key, previousPageNo, callback)
    }

    companion object {
        //val userCountObserserver = MutableLiveData<Int>()
    }
}