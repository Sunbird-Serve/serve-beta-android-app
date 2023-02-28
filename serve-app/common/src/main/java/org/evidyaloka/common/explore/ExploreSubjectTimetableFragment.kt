package org.evidyaloka.common.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.evidyaloka.common.databinding.FragmentSubjectTimetableBinding


open class ExploreSubjectTimetableFragment : Fragment() {
    lateinit var binding: FragmentSubjectTimetableBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubjectTimetableBinding.inflate(layoutInflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

}