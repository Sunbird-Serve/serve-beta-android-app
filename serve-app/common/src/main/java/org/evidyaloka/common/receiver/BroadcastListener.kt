package org.evidyaloka.common.receiver

interface BroadcastListener {

    fun onOtpReceived(otp: String?)
    fun onOtpTimeout()
}

