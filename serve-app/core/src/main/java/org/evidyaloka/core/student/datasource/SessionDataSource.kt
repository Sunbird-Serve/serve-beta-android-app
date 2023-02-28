package org.evidyaloka.core.student.datasource

import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.student.repository.StudentRepository

class SessionDataSource(
    private val startDate: Long,
    private val endDate: Long,
    private val offeringId: Int?,
    private val classType: Int?,
    private val repo: StudentRepository,
    private val scope: CoroutineScope,
    private val sessionDetails:(Map<String, Any>) -> Unit
) : PageKeyedDataSource<Int, Session>() {

    private var searchJob: Job? = null
    private var count = 0
    private fun getUsers(callback: LoadInitialCallback<Int, Session>) {
        searchJob?.cancel()
        searchJob = scope.launch {
            repo.getSessions(startDate, endDate, offeringId,classType,0)?.let {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {
                            count = it.count

                            sessionDetails(
                                HashMap<String, Any>().apply {
                                    put(StudentConst.count, it.count)
                                    put(StudentConst.timetableStatus, it.timetableStatus)
                                    put(StudentConst.timetableId, it.timeTableId)
                                    put(StudentConst.classCount, it.session?.size)

                                }
                            )
                        }

                        it.data?.session?.let { list ->
                            callback.onResult(list, null, 1)
                        }
                    }
                    is Resource.GenericError -> {
                        sessionDetails(
                            HashMap<String, Any>().apply {
                                it.code?.let { it1 -> put(StudentConst.ERROR, it1) }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun getMoreUsers(
        pageNo: Int,
        previousOrNextPageNo: Int,
        callback: LoadCallback<Int, Session>
    ) {
        searchJob?.cancel()
        searchJob = scope.launch {
            repo.getSessions(startDate, endDate, offeringId,classType, pageNo)?.let {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {
                            count = it.count
                            it.session?.let { list ->

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
        callback: LoadInitialCallback<Int, Session>
    ) {
        getUsers(callback)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Session>) {
        val nextPageNo = params.key + 1
        if(count > 0 && count >= StudentConst.PAGINATION_COUNT)
            getMoreUsers(params.key, nextPageNo, callback)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Session>) {
        val previousPageNo = if (params.key > 1) params.key - 1 else 0
        getMoreUsers(params.key, previousPageNo, callback)
    }

}