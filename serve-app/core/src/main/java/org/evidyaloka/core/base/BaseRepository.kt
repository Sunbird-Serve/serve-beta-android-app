package org.evidyaloka.core.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.evidyaloka.core.helper.ErrorData
import org.evidyaloka.core.helper.ErrorResponse
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.model.Student
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * @author Madhankumar
 * created on 27-12-2020
 *
 */
abstract class BaseRepository {
    private val TAG = "BaseRepository"
    var progressBarObservable: MutableLiveData<Boolean> = MutableLiveData(); private set

    /**
     * This function is responsible to make api call and handle error and exceptions.
     * Note:
     * api calls will be invoked in IO dispatcher
     * @param mapper this inline function will convert response entity to domain model
     * @param apiCall api suspend call
     * @return Resource<Any> return type will be based on mapper return type, ErrorData or Nothing
     */
    suspend inline fun <T,R> safeApiCall(crossinline mapper: (T) -> R,showProgress:Boolean = true, crossinline apiCall: suspend () -> T): Resource<R?> {
        return withContext(Dispatchers.IO) {
            val MAX_RETRY: Int = 3
            val DELAY_RETRY_TIME = 1000 //in ms
            if (showProgress) progressBarObservable.postValue(true)
            var reTry = 1
            var response: Resource<R> = Resource.GenericError(
                0,
                ErrorData(0),
                null
            )
            loop@ do {
                try {
                    val result = apiCall.invoke()
                    progressBarObservable.postValue(false)
                    if (result is Response<*> && !result.isSuccessful) {
                        //TODO error parsing
                        var errorBody: ErrorData? = null
                        result.errorBody()?.let {
                            errorBody = formatErrorResponse(it)
                        }
                        response = Resource.GenericError(result.code(), errorBody, null)
                    } else {
                        response = Resource.Success(mapper(result))
                    }
                    break@loop
                } catch (throwable: Throwable) {
                    FirebaseCrashlytics.getInstance().recordException(throwable)
                    throwable.printStackTrace()
                    when (throwable) {
                        is IOException -> {

                            response = Resource.GenericError(
                                -1,
                                ErrorData(-1),
                                null
                            ) // -1 status is not handled anywhere. Just returned for empty reference
                            break@loop
                        }
                        is HttpException -> {
                            val code = throwable.code()
                            val message = throwable.message()
                            var errorBody: ErrorData? = null
                            throwable.response()?.errorBody()?.let {
                                errorBody = formatErrorResponse(it)
                            }
                            response = Resource.GenericError(code, errorBody, null)
                            break@loop
                        }
                        else -> {
                            response = Resource.GenericError(
                                0,
                                ErrorData(0),
                                null
                            ) // 0 status is not handled anywhere. Just returned for empty reference
                        }
                    }
                }
                reTry++
                delay(reTry.toLong() * DELAY_RETRY_TIME)
            }while(reTry <= MAX_RETRY)
            progressBarObservable.postValue(false)
            return@withContext response
        }
    }

    fun formatErrorResponse(errorBody: ResponseBody): ErrorData?{
         errorBody.string().let {
             return try {
                 Gson().fromJson(it, ErrorResponse::class.java).error
             }catch (e:Exception){
                 FirebaseCrashlytics.getInstance().recordException(e)
                 e.printStackTrace()
                 ErrorData()
             }
        }
    }



}