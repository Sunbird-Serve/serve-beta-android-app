package org.evidyaloka.student.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.core.student.database.entity.BookmarkEntity
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.FragmentBookmarkBinding
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.rtc.RtcViewFragmentArgs


@AndroidEntryPoint
class BookmarkFragment : BaseFragment() {

    private val viewModel: BookmarkViewModel by viewModels()
    private lateinit var binding: FragmentBookmarkBinding
    private val bookmarkAdapter = BookmarkAdapter()
    private var studentBookmark: List<BookmarkEntity>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.rvDownloads.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = bookmarkAdapter
        }
        getSelectedStudentContent()

        bookmarkAdapter.setClickListener(object : OnItemClickListner {
            override fun OnItemClick(bookmark: BookmarkEntity) {
                navController.navigate(
                    R.id.rtcnavigation,
                    buildRtfcArgs(bookmark).toBundle()
                )
            }
        })
    }

    private fun buildRtfcArgs(content: BookmarkEntity): RtcViewFragmentArgs {
        with(content) {
            return RtcViewFragmentArgs(
                Session(
                    id = sessionId ?: 0,
                    topicId = topicId ?: 0,
                    topicName = topicName ?: "",
                    offeringId = offeringId ?: 0
                ),
                timetableId = timetableId ?: 0,
                subTopic = SubTopic(subtopicId ?: 0, subTopicName ?: "")
            )
        }
    }

    private fun getSelectedStudentContent() {
        viewModel.getSelectedStudentBookmarks().observe(viewLifecycleOwner, Observer {
            if(it.isNullOrEmpty()){
                binding.llEmptyBookmark.visibility = View.VISIBLE
                binding.rvDownloads.visibility = View.GONE
                return@Observer
            }
            binding.rvDownloads.visibility = View.VISIBLE
            studentBookmark = it
            studentBookmark?.let { bookmarkAdapter.setItems(it) }
        })
    }

}