package org.evidyaloka.core.student.datasource.doubt

import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.model.Doubt
import org.evidyaloka.core.student.repository.StudentRepository

class DoubtDataSource(
    private val id: Int?,
    private val offeringId: Int?,
    private val topicId: Int?,
    private val subtopicId: Int?,
    private val repo: StudentRepository,
    private val scope: CoroutineScope,
    private val doubtDetails: (Int) -> Unit
) : PageKeyedDataSource<Int, Doubt>() {

    private var searchJob: Job? = null
    private var count = 0
    private fun getDoubts(callback: LoadInitialCallback<Int, Doubt>) {
        searchJob?.cancel()
        searchJob = scope.launch {

            repo.getDoubts(0, id, offeringId, topicId, subtopicId)?.let {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let { d->
                            var doubtsCount =  d.totalCount?.let {
                                if(it == 0){
                                    d.doubts?.size
                                }else{
                                    it
                                }
                            } ?: d.doubts?.size
                            count = doubtsCount
                            doubtDetails(
                                doubtsCount
                            )
                        }

                        it.data?.doubts?.let { list ->
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
        page: Int,
        previousOrNextPageNo: Int,
        callback: LoadCallback<Int, Doubt>
    ) {
        searchJob?.cancel()
        searchJob = scope.launch {
            repo.getDoubts(page, id, offeringId, topicId, subtopicId)?.let {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {

                            var doubtsCount =  it.totalCount?.let {
                                it
                            } ?: it.doubts?.size

                            count = doubtsCount
                            it.doubts?.let { list ->

                                callback.onResult(
                                    list,
                                    previousOrNextPageNo
                                )
                            }
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
        callback: LoadInitialCallback<Int, Doubt>
    ) {
        getDoubts(callback)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Doubt>) {
        val nextPageNo = params.key + 1
        if (count > 0 && count >= StudentConst.PAGINATION_COUNT)
            getMoreUsers(params.key, nextPageNo, callback)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Doubt>) {
        val previousPageNo = if (params.key > 1) params.key - 1 else 0
        getMoreUsers(params.key, previousPageNo, callback)
    }

}