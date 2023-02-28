package org.evidyaloka.common.explore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.evidyaloka.common.databinding.FragmentDigitalSchoolBinding


open class ExploreDigitalSchoolFragment : Fragment() {

    companion object {
        fun newInstance() = ExploreDigitalSchoolFragment()
    }

    lateinit var binding: FragmentDigitalSchoolBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDigitalSchoolBinding.inflate(layoutInflater,container,false)

        return binding.root
    }

}