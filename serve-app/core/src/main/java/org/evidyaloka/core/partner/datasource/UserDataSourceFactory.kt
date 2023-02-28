package org.evidyaloka.core.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.core.partner.repository.MainRepository

class UserDataSourceFactory(private val searchKey:String, private val repo: MainRepository, private val scope: CoroutineScope) : DataSource.Factory<Int, User>() {

    private val mutableLiveData = MutableLiveData<UserDataSource>()

    override fun create(): DataSource<Int, User> {
        val userDataSource = UserDataSource(
            searchKey,
            repo,
            scope
        )
        mutableLiveData.postValue(userDataSource)
        return userDataSource
    }

    fun userLiveDataSource() = mutableLiveData

    //val userCountObserserver = UserDataSource.userCountObserserver

}