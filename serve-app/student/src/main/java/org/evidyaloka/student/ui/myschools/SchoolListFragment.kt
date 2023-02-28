package org.evidyaloka.student.ui.myschools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.core.student.model.School
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.FragmentMySchoolsBinding
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.home.HomeFragmentDirections
import org.evidyaloka.student.ui.student.SchoolAdapter

@AndroidEntryPoint
class SchoolListFragment : BaseFragment() {

    private val TAG = SchoolListFragment::class.java.simpleName
    private lateinit var binding: FragmentMySchoolsBinding
    private val viewModel: SchoolViewModel by viewModels()
    private var schoolAdapter = SchoolAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySchoolsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSchools.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = schoolAdapter
            schoolAdapter.setClickListener(object : OnItemClickListner {
                override fun OnItemClick(school: School) {
                    try{
                    navController.navigate(
                        SchoolListFragmentDirections.actionSchoolListFragmentToSchoolDetailsFragment(
                            school
                        )
                    )}catch (e : Exception){
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
            })
        }
        getSchools()
    }

    fun getSchools() {
        var schools = arrayListOf<School>()
        viewModel.getSelectedStudent()?.observe(viewLifecycleOwner, Observer {
            it?.schools?.let {
                schools.add(it)
                schoolAdapter.setItems(schools)

                binding.tvSchoolsCount.text = if (schools.size > 1) {
                    getString(R.string.label_school_count_plural, schools.size)
                } else {
                    getString(R.string.label_school_count_singular, schools.size)
                }
            }
        })
    }
}