package org.evidyaloka.common.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.ExploreData
import org.evidyaloka.common.R
import org.evidyaloka.common.databinding.FragmentGradeSelectionBinding
import org.evidyaloka.partner.ui.explore.grade.GradeAdapter
import java.util.*


open class ExploreGradeSelection : Fragment() {
    private val TAG = "FragmentOnboarding"
    open lateinit var binding: FragmentGradeSelectionBinding
    open var gradeAdapter = GradeAdapter()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGradeSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            binding.rvGrades.apply {
                layoutManager =
                    GridLayoutManager(it, 2, GridLayoutManager.VERTICAL, false)
                adapter = gradeAdapter
            }
        }

    }
}