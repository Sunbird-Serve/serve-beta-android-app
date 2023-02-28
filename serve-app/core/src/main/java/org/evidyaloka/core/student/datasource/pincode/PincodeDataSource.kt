package org.evidyaloka.core.student.datasource.pincode

import android.util.Log
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.model.Pincode
import org.evidyaloka.core.student.repository.StudentRepository

class PincodeDataSource(
    private val searchText: String,
    private val repo: StudentRepository,
    private val scope: CoroutineScope
) : PageKeyedDataSource<Int, Pincode>() {

    private val TAG: String? = "PincodeDataSource"
    private var searchJob: Job? = null
    private var count = 0
    private fun getPincodes(callback: LoadInitialCallback<Int, Pincode>) {
        searchJob?.cancel()
        searchJob = scope.launch {

            repo.getPincodes(searchText, 0)?.let {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let { list ->
                            callback.onResult(list, null, 1)
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun getMorePincodes(
        page: Int,
        previousOrNextPageNo: Int,
        callback: LoadCallback<Int, Pincode>
    ) {
        searchJob?.cancel()
        searchJob = scope.launch {
            repo.getPincodes(searchText, page)?.let {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let { list ->

                            callback.onResult(
                                list,
                                previousOrNextPageNo
                            )
                        }

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
    ) { getPincodes(callback) }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Pincode>) {
        val nextPageNo = params.key + 1
        if (count > 0 && count >= StudentConst.PAGINATION_COUNT)
            getMorePincodes(params.key, nextPageNo, callback)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Pincode>) {
        val previousPageNo = if (params.key > 1) params.key - 1 else 0
        getMorePincodes(params.key, previousPageNo, callback)
    }

}