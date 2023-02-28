package org.evidyaloka.student.ui.doubt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.student.model.Doubt
import org.evidyaloka.core.student.model.Student
import org.evidyaloka.partner.ui.helper.DoubtPreviewDialogFragment
import org.evidyaloka.student.databinding.FragmentDoubtDetailBinding
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.StudentHomeActivity
import org.evidyaloka.student.ui.rtc.DoubtViewModel
import org.evidyaloka.common.util.SubjectViewUtils

@AndroidEntryPoint
class DoubtDetailFragment : BaseFragment() {

    private val TAG = DoubtDetailFragment::class.java.simpleName
    private lateinit var binding: FragmentDoubtDetailBinding
    private var student: Student? = null
    private val viewModel: DoubtViewModel by viewModels()
    private var doubtDetailAdapter = DoubtDetailAdapter()
    private var doubt: Doubt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var args = DoubtDetailFragmentArgs.fromBundle(it)
            doubt = args.doubt
        }
        doubt?.let { doubt ->
            try {
                activity?.let { (it as StudentHomeActivity).setToolbarTitle(doubt.topicName) }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDoubtDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        binding.rvDoubts.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = doubtDetailAdapter
            doubtDetailAdapter.setClickListener(object : OnItemClickListner {
                override fun OnItemClick(doubt: Doubt, type: StudentConst.DoubtViewType?) {
                    showPreviewDialog(doubt)
                }
            })
        }
        viewModel.getSelectedStudent().observe(viewLifecycleOwner, Observer {
            student = it
            getDoubtDetails()
        })
    }

    private fun showPreviewDialog(doubt: Doubt) {

        context?.let {
            DoubtPreviewDialogFragment.Builder(it)
                .setIsDialogCancelable(true)
                .setDoubt(doubt)
                .setViewType(DoubtPreviewDialogFragment.DIALOG_TYPE.FULL_SCREEN)
                .build()
                .show(childFragmentManager, "")
        }
    }

    /**
     * To get list of all the doubts created by student
     */

    fun getDoubtDetails() {
        doubt?.let {
            viewModel.getDoubts(it.id, it.offeringId, it.topicId, it.subTopicId)
                ?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    doubtDetailAdapter.setItem(student)
                    doubtDetailAdapter.submitList(null)
                    doubtDetailAdapter.submitList(it)
                })
        }
    }

    /**
     * To change the color of the app bar & status bar.
     */
    fun updateUIColor(subject: String? = null) {
        // update cardview border and bg
        activity?.let { it ->
            (it as StudentHomeActivity).updateUiStyle(
                SubjectViewUtils.getUIBackground(
                    subject
                )
            )
        }
    }
}