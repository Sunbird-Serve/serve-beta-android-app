package org.evidyaloka.student.ui.doubt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Course
import org.evidyaloka.core.student.model.Doubt
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.FragmentYourDoubtsBinding
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.StudentHomeActivity
import org.evidyaloka.student.ui.rtc.DoubtViewModel
import org.evidyaloka.common.util.SubjectViewUtils
import org.evidyaloka.student.utils.Util

@AndroidEntryPoint
class DoubtListFragment : BaseFragment() {

    private val TAG = DoubtListFragment::class.java.simpleName
    private lateinit var binding: FragmentYourDoubtsBinding
    private val viewModel: DoubtViewModel by viewModels()
    private var doubtAdapter = DoubtAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentYourDoubtsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        binding.rvDoubts.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = doubtAdapter
            doubtAdapter.setClickListener(object : OnItemClickListner {
                override fun OnItemClick(
                    doubt: Doubt, type: StudentConst.DoubtViewType?) {
                    try{
                    navController.navigate(DoubtListFragmentDirections.actionDoubtListFragmentToDoubtDetailFragment(doubt))
                    }catch (e : Exception){
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }
            })
        }
        getDoubtList()
        getCourseList()
    }

    /**
     * To get list of all the doubts created by student
     */

    fun getDoubtList(offeringId: Int? = null) {
        viewModel.getDoubts(offeringId = offeringId)
            ?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                doubtAdapter.submitList(null)
                doubtAdapter.submitList(it)
                updateUI(offeringId)
            })
    }

    /**
     * To get list of all the courses opted by student
     */
    fun getCourseList() {
        viewModel.getStudentCourses()?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    it.data?.let {
                        val courses: MutableList<Course> = mutableListOf()
                        // Add all a temporary key for selecting all the subjects
                        context?.let {
                            courses.add(
                                0, Course(
                                    0,
                                    name = it.resources.getString(R.string.labeL_all_class),
                                    isSelected = true
                                )
                            )
                        }
                        it?.forEach { course ->
                            courses.add(Course(course.id, course.name, course.isSelected))
                        }
                        initCourseSpinner(courses)
                    }
                }
                is Resource.GenericError -> {
                    it.code?.let{
                        if(it <= 0)
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
        })
    }

    /**
     * function to update course Spinner
     */
    private fun updateCourseSpinner(list: List<Course>) {
        try {
            var l = list.map { it.name }
            binding.etvCourses?.apply {
                context?.let {
                    var adapter = ArrayAdapter(it, R.layout.item_spinner, l)
                    setAdapter(adapter)
                    adapter?.notifyDataSetChanged()
                    setOnItemClickListener { adapterView, mView, position, l ->
                        list[position].name?.let {
                            binding.etvCourses.setText(list[position].name)
                            updateUIColor(it)
                            if (position == 0) {
                                //open all doubts
                                getDoubtList()
                            } else {
                                list[position]?.let {
                                    getDoubtList(it.id)
                                }
                            }
                        }
                        updateCourseSpinner(list)
                    }
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    /**
     * To initliaze the course spinner
     * and select All as default value.
     */
    private fun initCourseSpinner(list: List<Course>) {
        try {
            var l = list.map { it.name }
            binding.etvCourses?.apply {
                context?.let {
                    var adapter = ArrayAdapter(it, R.layout.item_spinner, l)
                    setAdapter(adapter)
                    adapter?.notifyDataSetChanged()
                    if (list.isNotEmpty()) {
                        binding.etvCourses.setSelection(0)
                        binding.etvCourses.setText(list[0].name)
                    }
                    updateCourseSpinner(list)
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    /**
     * To change the color of the app bar & status bar.
     */
    fun updateUIColor(subject: String? = null) {
        // update cardview border and bg
        activity?.let { it ->
            (it as StudentHomeActivity).updateUiStyle(SubjectViewUtils.getUIBackground(subject))
        }
    }

    private fun updateUI(offeringId : Int? = null){
        viewModel.doubtDetailsObserver.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.tvDoubtsCount.text = if (it <= 1) {
                    getString(R.string.no_of_doubt_singular, it)
                } else {
                    getString(R.string.no_of_doubt_plural, it)
                }

                when (it) {
                    0 -> {
                        if (offeringId == null) {
                            binding.llCourses.visibility = GONE
                            binding.llDoubts.visibility = GONE
                            binding.llNoDoubts.rlNoDoubts.visibility = VISIBLE

                        } else {
                            binding.llCourses.visibility = VISIBLE
                            binding.llDoubts.visibility = VISIBLE
                            binding.llNoDoubts.rlNoDoubts.visibility = GONE
                        }
                    }
                    else -> {
                        binding.llCourses.visibility = VISIBLE
                        binding.llDoubts.visibility = VISIBLE
                        binding.llNoDoubts.rlNoDoubts.visibility = GONE
                    }
                }
            }
        })
    }

}