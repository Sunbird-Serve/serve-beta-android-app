package org.evidyaloka.core.partner.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope
import org.evidyaloka.core.partner.model.Pincode
import org.evidyaloka.core.partner.repository.MainRepository

class PincodeDataSourceFactory(private val stateId:Int, private val searchQuery:String?, private val repo: MainRepository, private val scope: CoroutineScope) : DataSource.Factory<Int, Pincode>() {

    private val mutableLiveData = MutableLiveData<PincodeDataSource>()

    override fun create(): DataSource<Int, Pincode> {
        val pincodeDataSource = PincodeDataSource(
            stateId,
            searchQuery,
            repo,
            scope
        )
        mutableLiveData.postValue(pincodeDataSource)
        return pincodeDataSource
    }

    fun pincodeLiveDataSource() = mutableLiveData

    //val userCountObserserver = UserDataSource.userCountObserserver

}