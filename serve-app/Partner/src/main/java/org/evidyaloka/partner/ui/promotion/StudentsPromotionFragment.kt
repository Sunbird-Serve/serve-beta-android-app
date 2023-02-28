package org.evidyaloka.partner.ui.promotion

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_student_list.*
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.CourseProvider
import org.evidyaloka.core.partner.model.AcademicYear
import org.evidyaloka.partner.R
import org.evidyaloka.partner.databinding.StudentsPromotionFragmentBinding
import org.evidyaloka.partner.ui.BaseFragment
import org.evidyaloka.partner.ui.student.StudentAdapter

@AndroidEntryPoint
class StudentsPromotionFragment : BaseFragment() {

    private val viewModel: StudentsPromotionViewModel by viewModels()
    private lateinit var binding: StudentsPromotionFragmentBinding
    private var adapter = StudentAdapter({ isChecked:Boolean,studentId: Int -> onItemClick(isChecked,studentId) },true)
    private var digitalSchoolId: Int? = null
    private var gradeSelected: Int? = null
    private var courseProvider: CourseProvider? = null
    private var selectedStudents: MutableList<Int> = mutableListOf()
    private var isAllSelected: Boolean = false
    private var filter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            digitalSchoolId = it.getInt(PartnerConst.DIGITAL_SCHOOL_ID)
            courseProvider = it.getParcelable<CourseProvider>(PartnerConst.COURSE_PROVIDER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(!::binding.isInitialized)
            binding = StudentsPromotionFragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        courseProvider?.id?.let { getAcademicYearData(it) }
        loadGrades()
        gradeSelected?.let{
            getEnrolledStudents()
        }
        binding.rvStudents.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = this@StudentsPromotionFragment.adapter
        }
        binding.btSelectAll.setOnCheckedChangeListener { buttonView, isChecked ->
            isAllSelected = isChecked
            adapter.toggleSelectAll(isChecked)
            if(!isAllSelected)
                selectedStudents = mutableListOf()
        }

        binding.etvFilter.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               filter = s.toString().takeIf { it.isNotEmpty()}
                gradeSelected?.let{
                    getEnrolledStudents()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.btNext.setOnClickListener {
            try{
                if(gradeSelected != null && digitalSchoolId != null) {
                    val directions = StudentsPromotionFragmentDirections.actionStudentsPromotionFragmentToPromotionTypeFragment(
                        gradeSelected!!,
                        selectedStudents.toIntArray(),
                        isAllSelected,
                        digitalSchoolId!!,
                        courseProvider?.id!!
                    )
                    navController.navigate(directions)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }


    }

    fun onItemClick(isChecked:Boolean,studentId: Int) {
        if(isChecked){
            selectedStudents.add(studentId)
        }else{
            selectedStudents.remove(studentId)
            isAllSelected = false
        }
    }

    private fun updateGradeSpinner(grades: List<Int>) {
        if (grades.size > 0) {
            try {
                    var adapter = ArrayAdapter(requireContext(), R.layout.spinner_list_item, grades)
                    var gradeList = grades
                binding.etvGrade?.setAdapter(adapter)
                    //adapter?.notifyDataSetChanged()
                binding.etvGrade?.setOnItemClickListener { adapterView, mView, position, l ->
                        gradeSelected = gradeList.get(position)
                        getEnrolledStudents()
                    }

            } catch (e: Exception) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    /*
     * Function to fetch Grades
     */
    private fun loadGrades() {
        try {
            viewModel.getSettings().observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response?.data.let {
                            if (it.gradeTo != 0 && it.gradeFrom != 0) {
                                updateGradeSpinner((it.gradeFrom..it.gradeTo).map { it }.sorted())
                            }
                        }
                    }
                    is Resource.GenericError -> {

                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


    private fun getEnrolledStudents() {
        //TODO Change hardcoded dsId = 1
        try {

            digitalSchoolId?.let {
                viewModel.getStudentsForPormotion(
                    PartnerConst.RoleType.DSM.name,
                    courseProvider?.id,
                    it,
                    gradeSelected,
                    null,
                    filter
                ).observe(viewLifecycleOwner, Observer { list ->
                    try {
                        viewModel.studentCountObserver.observe(viewLifecycleOwner, Observer {
                            tv_students_count?.text = getString(R.string.total_students).plus(" ($it)")
                        })
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        e.printStackTrace()
                    }
                    adapter?.submitList(list)
                })
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }


    }

}