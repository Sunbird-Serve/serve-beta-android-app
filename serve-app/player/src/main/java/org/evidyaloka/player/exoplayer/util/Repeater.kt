package org.evidyaloka.player.exoplayer.util

import android.os.Handler
import android.os.HandlerThread
import androidx.annotation.Nullable

/**
 * A method repeater to easily perform update functions on a timed basis.
 * **NOTE:** the duration between repeats may not be exact.  If you require an exact
 * amount of elapsed time use the [StopWatch] instead.
 */
class Repeater {
    interface RepeatListener {
        fun onRepeat()
    }

    /**
     * Determines if the Repeater is currently running
     *
     * @return True if the repeater is currently running
     */
    @Volatile
    var isRunning = false
        protected set
    /**
     * Retrieves the amount of time between method invocation.
     *
     * @return The millisecond time between method calls
     */
    /**
     * Sets the amount of time between method invocation.
     *
     * @param milliSeconds The time between method calls [default: {@value #DEFAULT_REPEAT_DELAY}]
     */
    var repeaterDelay = DEFAULT_REPEAT_DELAY
    protected var delayedHandler: Handler? = null
    protected var handlerThread: HandlerThread? = null
    protected var useHandlerThread = false
    protected var listener: RepeatListener? = null
    protected var pollRunnable = PollRunnable()

    /**
     * @param processOnStartingThread True if the repeating process should be handled on the same thread that created the Repeater
     */
    @JvmOverloads
    constructor(processOnStartingThread: Boolean = true) {
        if (processOnStartingThread) {
            delayedHandler = Handler()
            return
        }
        useHandlerThread = true
    }

    /**
     * @param handler The Handler to use for the repeating process
     */
    constructor(handler: Handler?) {
        delayedHandler = handler
    }

    /**
     * Starts the repeater
     */
    fun start() {
        if (!isRunning) {
            isRunning = true
            if (useHandlerThread) {
                handlerThread = HandlerThread(HANDLER_THREAD_NAME)
                handlerThread!!.start()
                delayedHandler = Handler(handlerThread!!.looper)
            }
            pollRunnable.performPoll()
        }
    }

    /**
     * Stops the repeater
     */
    fun stop() {
        if (handlerThread != null) {
            handlerThread!!.quit()
        }
        isRunning = false
    }

    /**
     * Sets the listener to be notified for each repeat
     *
     * @param listener The listener or null
     */
    fun setRepeatListener(@Nullable listener: RepeatListener?) {
        this.listener = listener
    }

    protected inner class PollRunnable : Runnable {
        override fun run() {
            if (listener != null) {
                listener!!.onRepeat()
            }
            if (isRunning) {
                performPoll()
            }
        }

        fun performPoll() {
            delayedHandler!!.postDelayed(pollRunnable, repeaterDelay.toLong())
        }
    }

    companion object {
        protected const val HANDLER_THREAD_NAME = "ExoMedia_Repeater_HandlerThread"
        protected const val DEFAULT_REPEAT_DELAY = 33 // ~30 fps
    }
}