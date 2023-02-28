package org.evidyaloka.core.repository

import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.core.partner.repository.MainRepository

class UserDataSource(
    private val searchKey: String,
    private val repo: MainRepository,
    private val scope: CoroutineScope
) : PageKeyedDataSource<Int, User>() {

    private var searchJob: Job? = null
    private fun getUsers(callback: LoadInitialCallback<Int, User>) {
        searchJob?.cancel()
        searchJob = scope.launch {
            repo.getAllUsers(0, searchKey)?.let {
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

    private fun getMoreUsers(
        pageNo: Int,
        previousOrNextPageNo: Int,
        callback: LoadCallback<Int, User>
    ) {
        searchJob?.cancel()
        searchJob = scope.launch {
            repo.getAllUsers(pageNo, searchKey)?.let {
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
        callback: LoadInitialCallback<Int, User>
    ) {
        getUsers(callback)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        val nextPageNo = params.key + 1
        getMoreUsers(params.key, nextPageNo, callback)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        val previousPageNo = if (params.key > 1) params.key - 1 else 0
        getMoreUsers(params.key, previousPageNo, callback)
    }

    companion object {
        //val userCountObserserver = MutableLiveData<Int>()
    }
}