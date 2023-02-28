package org.evidyaloka.student.ui.timetable.subject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Subject
import org.evidyaloka.student.R
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.StudentHomeActivity
import org.evidyaloka.common.util.SubjectViewUtils
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.student.databinding.FragmentSubjectOverviewBinding

@AndroidEntryPoint
class SubjectOverviewFragment : BaseFragment() {
    private var subject: Subject? = null
    private lateinit var binding: FragmentSubjectOverviewBinding
    private val viewModel: SubjectOverviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = SubjectOverviewFragmentArgs.fromBundle(it)
            subject = args.subject
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubjectOverviewBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        subject?.let {
            viewModel.getSubjectDetails(it.offeringId, it.courseId)
                .observe(viewLifecycleOwner, Observer { listResponse ->
                    if (listResponse is Resource.Success) {
                        listResponse.data?.let { list ->
                            binding.subtopicView.apply {
                                setSubject(
                                    it.subjectName,
                                    list
                                ) { topic, subTopic, isLockedContent ->
                                    subject?.offeringId?.let { id ->
                                        getSessionDetails(
                                            id,
                                            topicId = topic.id,
                                            subtopic = subTopic
                                        )
                                    }
                                }
                                setViewBackground(R.drawable.bg_bottom_sheet_curved)
                            }
                            binding.subtopicView.setLessonCount(
                                String.format(
                                    binding.root.resources.getString(R.string.subject_count),
                                    *arrayOf(it.totalTopicsViewed, it.totalTopics)
                                )
                            )
                        }
                    }
                })
        }


    }

    private fun getSessionDetails(offeringId: Int, topicId: Int, subtopic: SubTopic) {

        viewModel.getSessionDetails(offeringId, topicId, subtopic.id)
            .observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {
                            navController.navigate(
                                SubjectOverviewFragmentDirections.actionSubjectTimetableFragmentToRtcViewFragment(
                                    Session(
                                        id = it.sessionId,
                                        topicId = it.topicId,
                                        offeringId = it.offeringId
                                    ),
                                    timetableId = it.timetableId,
                                    subTopic = subtopic
                                )
                            )
                        }

                    }
                    is Resource.GenericError -> {

                    }
                }
            })

    }

    override fun onResume() {
        super.onResume()
        subject?.let {
            activity?.let { act ->
                val color = SubjectViewUtils.getCourseUISettings(it.subjectName).color()
                (act as StudentHomeActivity).updateUiStyle(color)
                (act as StudentHomeActivity).setToolbarTitle(
                    String.format(
                        getString(R.string.title_lessons),
                        it.subjectName
                    )
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.let { it ->
            (it as StudentHomeActivity).updateUiStyle()
        }
    }

}