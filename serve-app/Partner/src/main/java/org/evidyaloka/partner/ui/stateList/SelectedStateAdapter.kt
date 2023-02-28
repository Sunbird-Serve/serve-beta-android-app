package org.evidyaloka.partner.ui.stateList

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.core.partner.model.State
import org.evidyaloka.partner.R
import org.evidyaloka.partner.databinding.LayoutSelectedStatePincodeBinding
import org.evidyaloka.partner.databinding.LayoutStateListBinding

class SelectedStateAdapter(private val onClickItem: (state:State) -> Unit): RecyclerView.Adapter<SelectedStateAdapter.ViewHolder>() {

    private var stateList: List<State> = listOf()

    inner class ViewHolder(val binding: LayoutSelectedStatePincodeBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(state:State){
            with(binding){

                statePincodeSelected.setText(state.name+" - "+state.pincodes.size+ " Pincodes selected")

                statePincodeSelected.setOnClickListener {
                    onClickItem(state)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = LayoutSelectedStatePincodeBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return stateList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stateList[position])
    }

    fun setItem(list: List<State>) {
        stateList = list
        notifyDataSetChanged()
    }



}