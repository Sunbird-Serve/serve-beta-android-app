package org.evidyaloka.student.ui.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.common.helper.loadUrlWithGlideCircle
import org.evidyaloka.core.student.model.School
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.ItemSchoolBinding
import org.evidyaloka.student.ui.myschools.OnItemClickListner

class SchoolAdapter() :
    RecyclerView.Adapter<SchoolAdapter.SchoolHolder>() {
    private val TAG = "SchoolAdapter"

    private var schools: List<School> = listOf()
    private lateinit var binding: ItemSchoolBinding
    private var onItemClickListner: OnItemClickListner? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolHolder {
        binding = ItemSchoolBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return SchoolHolder(binding.root)
    }

    public fun setClickListener(onItemClickListner: OnItemClickListner) {
        this.onItemClickListner = onItemClickListner
    }

    fun setItems(schools: List<School>) {
        this.schools = schools
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return schools.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: SchoolHolder, position: Int) {
        val school = schools[position]
        school?.let {
            holder.bind(it)
        }
    }

    inner class SchoolHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemSchoolBinding.bind(view)
        fun bind(school: School) {
            with(binding) {

                school.name?.let {
                    tvSchoolName.text = it
                }
                school.boardName?.let{
                    tvSchoolBoard.text = it
                }

                var schoolLogourl = ""
                school.logoUrl?.let {
                    schoolLogourl = it
                }

                when (adapterPosition) {
                    0 -> { llRoot.setBackgroundResource(R.drawable.ic_school_bg_1) }
                    1 -> { llRoot.setBackgroundResource(R.drawable.ic_school_bg_2) }
                    2 -> { llRoot.setBackgroundResource(R.drawable.ic_school_bg_3) }
                    3 -> { llRoot.setBackgroundResource(R.drawable.ic_school_bg_4) }
                    else -> { llRoot.setBackgroundResource(R.drawable.ic_school_bg_1) }
                }

                ivSchoolLogo?.loadUrlWithGlideCircle(
                    schoolLogourl, R.drawable.ic_school_logo_placeholder,
                    R.drawable.ic_school_logo_placeholder
                )
            }

            binding.root.setOnClickListener {
                onItemClickListner?.OnItemClick(school)
            }
        }
    }
}