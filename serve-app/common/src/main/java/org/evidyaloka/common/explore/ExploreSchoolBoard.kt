package org.evidyaloka.common.explore

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.evidyaloka.common.databinding.FragmentSchoolBoardBinding
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.CourseProvider

open class ExploreSchoolBoard : Fragment() {
    private val TAG = "ExploreSchoolBoard"
    lateinit var binding: FragmentSchoolBoardBinding
    var schoolBoardAdapter = SchoolBoardAdapter()
    var schoolBoardList = arrayListOf<CourseProvider>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        activity?.let {
            binding.rvSchoolBoards.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = schoolBoardAdapter
            }
        }

        binding.etvSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(e: Editable?) {
                filter(e.toString().toLowerCase());
            }

        })
    }

    fun filter(searchText: String) {
        val tempCourseProvider: MutableList<CourseProvider> = ArrayList()
        for (schoolBoard in schoolBoardList) {
            if (schoolBoard.code?.toLowerCase()
                    ?.contains(searchText) == true || schoolBoard.name.toLowerCase().contains(searchText)
            ) { tempCourseProvider.add(schoolBoard) }
        }
        schoolBoardAdapter.setItems(tempCourseProvider)
    }
}