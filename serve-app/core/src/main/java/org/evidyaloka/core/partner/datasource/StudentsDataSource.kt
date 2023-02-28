package org.evidyaloka.core.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.Student
import org.evidyaloka.core.partner.repository.MainRepository

class StudentsDataSource(private val userType: String, private val courseProviderId: Int?, private val digitalSchoolId: Int, private val grade: Int?, private val offeringId: Int?, private val academicYearId: Int?, private val repo: MainRepository, private val scope: CoroutineScope) : PageKeyedDataSource<Int, Student>() {

    private var searchJob: Job? = null
    private fun getStudents(callback: LoadInitialCallback<Int, Student>) {
        searchJob?.cancel()
        searchJob = scope.launch {
            repo.getEnrolledStudents(userType, courseProviderId, digitalSchoolId, grade, offeringId, academicYearId,0)?.let {
                when (it) {
                    is Resource.Success -> {
                        it.data?.count?.let{
                            studentCountObserver.postValue(it)
                        }
                        it.data?.students?.let { list ->

                            callback.onResult(list, null, 1)
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun getMoreStudents(pageNo: Int, previousOrNextPageNo: Int, callback: LoadCallback<Int, Student>) {
        searchJob?.cancel()
        searchJob = scope.launch {
            repo.getEnrolledStudents(userType, courseProviderId, digitalSchoolId, grade, offeringId, academicYearId,pageNo)?.let {
                when (it) {
                    is Resource.Success -> {
                        it.data?.students?.let { list -> callback.onResult(list, previousOrNextPageNo) }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Student>
    ) {
        getStudents(callback)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Student>) {
        val nextPageNo = params.key + 1
         getMoreStudents(params.key,nextPageNo, callback)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Student>) {
        val previousPageNo = if (params.key > 1) params.key - 1 else 0
        getMoreStudents(params.key,previousPageNo, callback)
    }

    companion object{
        val studentCountObserver = MutableLiveData<Int>()
    }

}