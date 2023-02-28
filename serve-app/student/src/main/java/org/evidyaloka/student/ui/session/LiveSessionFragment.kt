package org.evidyaloka.student.ui.session

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Session
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.LiveSessionFragmentBinding
import org.evidyaloka.student.ui.BaseFragment

@AndroidEntryPoint
class LiveSessionFragment : BaseFragment() {
    private val TAG = "LiveSessionFragment"
    private var session: Session? = null
    private val viewModel: LiveSessionViewModel by viewModels()

    private lateinit var binding: LiveSessionFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            val args =
                LiveSessionFragmentArgs.fromBundle(
                    it
                )
            session = args.session
        }
        binding = LiveSessionFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session?.let {
            binding.topic.text = it.topicName
            binding.sessionTime.text = it.startTime.plus(" - ").plus(it.endTime)
            if (it.liveClassUrl.isNullOrEmpty()) {
                binding.link.text = getString(R.string.live_session_link_missing)
                return@let
            }
            binding.link.text = it.liveClassUrl
            binding.join.setOnClickListener { view ->
                sendAttadance()
                startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_VIEW, Uri.parse(it.liveClassUrl)),
                        getString(R.string.open_with)
                    )
                )
            }
        }
    }

    private fun sendAttadance() {

        try {
            Log.e(TAG, "sendAttadance: " + StudentConst.ClassType.values())
            val data = HashMap<String, Any?>()
            data[StudentConst.SESSION_ID] = session?.id
            data[StudentConst.CLASS_TYPE] =  StudentConst.ClassType.Live.value.toString()
            viewModel.sendAttendance(data).observe(
                viewLifecycleOwner,
                Observer {
                    if (it is Resource.GenericError) {
                        Toast.makeText(
                            context,
                            resources.getString(R.string.attendance_submit_error),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

}