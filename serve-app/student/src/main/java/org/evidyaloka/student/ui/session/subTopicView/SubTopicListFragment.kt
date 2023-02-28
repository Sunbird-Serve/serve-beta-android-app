package org.evidyaloka.student.ui.session.subTopicView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.common.helper.convert24to12a
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.student.R
import org.evidyaloka.common.util.SubjectViewUtils
import org.evidyaloka.student.databinding.FragmentSubTopicListBinding
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.home.HomeViewModel
import org.evidyaloka.student.utils.Util

@AndroidEntryPoint
class SubTopicListFragment : BaseFragment() {
    private lateinit var binding: FragmentSubTopicListBinding

    private var adapter = SubTopicAdapter()
    private var session: Session? = null
    private var timetableId: Int? = null
    private var isOneTopicNav: Boolean = false

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var args = SubTopicListFragmentArgs.fromBundle(it)
            session = args.session
            timetableId = args.timetableId
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubTopicListBinding.inflate(layoutInflater, container, false)
        binding.rvSubtopic.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvSubtopic.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        session?.let { session ->
            setSubject(session.subjectName)
            setTopic(session.topicName)
            setTeacherName(session.teacherName)
            setSessionTime(session.startTime.convert24to12a())

            var topicLenArray = session.subtopicIdStr?.split(",")
            binding.tvSubtpoicCount.text =
                String.format(getString(R.string.you_have_no_subtopics), topicLenArray.size)

            if (isOneTopicNav) {
                findNavController().popBackStack()
            } else {
                getSessionDetail(session.id)
            }
        }

        adapter?.setOnItemClickListener(object : OnItemClickListner {
            override fun OnItemClick(subtopic: SubTopic) {
                openRTCView(subtopic)
            }
        })
    }

    fun setSubject(title: String) {
        binding.tvSubject.text = title
        val courseUiSettings = SubjectViewUtils.getCourseUISettings(title)
        setBackgroundImage(courseUiSettings.background())
        setIcon(courseUiSettings.icon())
    }

    fun setTopic(name: String) {
        binding.tvTopic.takeIf { name.isNotEmpty() }?.apply {
            text = name
            visibility = View.VISIBLE
        }
    }

    fun setTeacherName(name: String) {
        binding.tvFaculty.takeIf { name.isNotEmpty() }?.apply {
            text = name
            visibility = View.VISIBLE
        }
    }

    fun setSessionTime(time: String) {
        binding.tvTime.text = time
    }

    fun setIcon(id: Int) {
        binding.ivIcon.setImageResource(id)
    }

    fun setBackgroundImage(id: Int) {
//        binding.llTopic.setBackgroundResource(id)
    }

    fun getSessionDetail(id: Int) {
        viewModel.getSessionDetail(id).observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {
                            if (it.size == 1 && !isOneTopicNav) {
                                openRTCView(it[0])
                                isOneTopicNav = true
                            } else {
                                adapter.setItem(it)
                            }
                        }
                    }
                    is Resource.GenericError -> {
                        it.code?.let {
                            if (it <= 0)
                                return@Observer
                        }
                        activity?.let {
                            Util.showPopupDialog(
                                it,
                                it.resources.getString(R.string.label_sorry),
                                it.resources.getString(R.string.could_not_able_to_fetch_data)
                            )?.show()
                        }
                    }
                }
            }
        })
    }

    private fun openRTCView(subtopic: SubTopic) {
        var bundle = Bundle()
        session?.id?.let { bundle.putInt("sessionId", it) }
        session?.topicId?.let { bundle.putInt("topicId", it) }
        timetableId?.let { bundle.putInt("timetableId", it) }
        bundle.putInt("subtopicId", subtopic.id)
        val navigate = SubTopicListFragmentDirections.actionSubTopicListFragmentToRtcViewFragment(
            session!!,
            timetableId!!,
            subtopic
        )
        try {
            findNavController().navigate(navigate)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)

        }
    }

}