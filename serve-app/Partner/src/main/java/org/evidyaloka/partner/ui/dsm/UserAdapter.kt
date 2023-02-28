package org.evidyaloka.partner.ui.dsm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_dsm_user.view.*
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.partner.R

class UserAdapter(
    private val clickListener: (userId: Int) -> Unit,
    private val schoolClickListener: (userId: Int) -> Unit
) : PagedListAdapter<User, UserAdapter.DsmViewHolder>(USER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DsmViewHolder {
        return DsmViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_dsm_user, parent, false)
        )
    }


    override fun onBindViewHolder(holder: DsmViewHolder, position: Int) {
        val dsmUser = getItem(position)
        if (dsmUser != null) {
            holder.bind(dsmUser, clickListener, schoolClickListener)
        }
    }

    class DsmViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(
            user: User,
            clickListener: (userId: Int) -> Unit,
            schoolClickListener: (userId: Int) -> Unit
        ) {

            view.iv_profile_pic?.let {
                it.context?.let { context ->
                    Glide.with(context)
                        .load(user.profileImageUrl)
                        .placeholder(R.drawable.ic_user_placeholder)
                        .error(R.drawable.ic_user_placeholder)
                        .circleCrop()
                        .into(it)
                }
            }

            view.tv_name?.setText(user.fname + " " + user.lname)
            view.tv_email?.setText(user.email)
            view.tv_phone?.setText(user.phone)

            if (user.schoolCount > 0) {
                view.tv_schools_assigned?.visibility = View.VISIBLE

                view.context?.let {
                    if (user.schoolCount == 1) {
                        view.tv_schools_assigned?.setText(it.getString(R.string.one_school_assigned))
                    } else {
                        view.tv_schools_assigned?.setText(
                            String.format(
                                it.getString(R.string.no_of_school_assigned),
                                user.schoolCount
                            )
                        )
                    }
                }

                view.tv_schools_assigned?.setOnClickListener {
                    schoolClickListener(user.id)
                }
            } else {
                view.tv_schools_assigned?.visibility = View.GONE
            }

            view.setOnClickListener {
                clickListener(user.id)
            }

        }
    }

    companion object {
        private val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                newItem == oldItem
        }
    }
}