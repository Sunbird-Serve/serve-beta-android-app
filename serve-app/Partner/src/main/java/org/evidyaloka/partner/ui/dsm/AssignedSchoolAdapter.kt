package org.evidyaloka.partner.ui.dsm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_assigned_school.view.*
import org.evidyaloka.partner.R
import org.evidyaloka.core.partner.network.entity.dsm.SchoolsAssigned

class AssignedSchoolAdapter() : RecyclerView.Adapter<AssignedSchoolAdapter.AssignedSchoolHolder>() {

    var assignedSchools: ArrayList<SchoolsAssigned> = ArrayList()

    constructor(assignedSchools: ArrayList<SchoolsAssigned>): this()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignedSchoolHolder {
        return AssignedSchoolHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_assigned_school, parent, false)
        )
    }

    fun setItem(assignedSchools: ArrayList<SchoolsAssigned>) {
        this.assignedSchools = assignedSchools
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return assignedSchools.size
    }

    override fun onBindViewHolder(holder: AssignedSchoolHolder, position: Int) {
        val school = assignedSchools[position]
        holder.bind(school)
    }

    class AssignedSchoolHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(school: SchoolsAssigned) {

            view.iv_profile_pic?.let {
                it.context?.let {it1->
                    Glide.with(it1)
                        .load(school.logoUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_school_logo_placeholder)
                        .error(R.drawable.ic_school_logo_placeholder)
                        .into(it)

                }
            }

            view.tv_school_name?.setText(school.name)

        }
    }
}