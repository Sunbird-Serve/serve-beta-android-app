package org.evidyaloka.student.ui.explore.join

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.model.Generic
import org.evidyaloka.core.student.model.User
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.student.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val repo: StudentRepository
) : BaseViewModel() {

    private val TAG = "JoinViewModel"

    private val countDownTimer: CountDownTimer
    private var timeLeft = MutableLiveData<Int>(CommonConst.RESEND_TIMEOUT)

    init {
        timeLeft.value = CommonConst.RESEND_TIMEOUT
        countDownTimer = object : CountDownTimer(CommonConst.RESEND_TIMEOUT * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                try {
                    timeLeft.value = timeLeft.value!! - 1
                }catch (e : Exception){
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }

            override fun onFinish() {
                timeLeft.value = 0
            }
        }
    }

    /**
     */
    fun generateOTP(data: Map<String, Any>) = liveData {
        emit(repo.generateOtpGuardianJoin(data))
    }

    /**
     * Function to do registration and convert response to live data
     */
    fun verifyOTP(data: Map<String, Any>) = liveData {
        val response = repo.registerGuardian(data)
        if (response is Resource.Success) {
            response?.data?.let {
                var user = it
                user.roles = listOf(CommonConst.PersonaType.Parent.toString())
                repo.saveUser(user)
            }
        }
        emit(response)
    }

    /**
     *
     */
    fun duration(): MutableLiveData<Int> {
        return timeLeft
    }

    /**
     * Function to start CountDown
     */
    fun startCountdown() {
        countDownTimer?.cancel()
        timeLeft?.value = CommonConst.RESEND_TIMEOUT
        countDownTimer?.start()
    }

    /**
     * To cancel countdown
     */
    fun cancelCountdown() {
        countDownTimer?.cancel()
    }

    fun saveUser(user: User): MutableLiveData<Resource<Generic>> {
        val liveData = MutableLiveData<Resource<Generic>>()
        try {
            repo.saveUser(user)
            liveData.value = Resource.Success(Generic("1", "Successfully saved!"))
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            liveData.value = Resource.GenericError(0, null, null)
        }
        return liveData
    }

    fun getProgressObserable() = repo.progressBarObservable

    override fun onCleared() {
        super.onCleared()
        cancelCountdown()
    }


}