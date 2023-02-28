package org.evidyaloka.partner.ui.digitalschool

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.evidyaloka.core.partner.model.User


class DSMAdapter(context: Context, resource: Int,val list: MutableList<User>) :
    ArrayAdapter<User>(context, resource, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // I created a dynamic TextView here, it can be  custom layout for each spinner item

        val label = super.getView(position, convertView, parent) as TextView
        label.setTextColor(Color.BLACK)
        label.text = list[position].fname

        // And finally return dynamic (or custom) view for each spinner item

        return label
    }

    override fun getItem(position: Int): User? {
        return super.getItem(position)
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        label.setTextColor(Color.BLACK)
        label.text = list[position].fname

        return label
    }
}