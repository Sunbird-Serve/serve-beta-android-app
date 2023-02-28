package org.evidyaloka.partner.ui.stateList

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.core.partner.model.Pincode
import org.evidyaloka.core.partner.model.State
import org.evidyaloka.partner.R
import org.evidyaloka.partner.databinding.LayoutStateListBinding

class PincodeAdapter: PagedListAdapter<Pincode, PincodeAdapter.ViewHolder>(PIN_COMPARATOR)  {

    private var pincodeList: List<Pincode> = listOf()
    private var selectedPincode: MutableList<Pincode> = mutableListOf()
    private var preSelectedPincode: MutableList<Pincode> = mutableListOf()
    private var disableSelected: Boolean = false
    private var selectedCheckboxView: ImageView? = null


    inner class ViewHolder(val binding: LayoutStateListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(pincode:Pincode){
            with(binding){
                var talukName = pincode.taluk?.takeIf { it.isNotEmpty() }?:pincode.district
                if(!talukName.isNullOrEmpty() && !talukName.isNullOrBlank()){
                    talukName = "$talukName - "
                }
                name.setText(talukName.plus(pincode.pincode))
                if(disableSelected && preSelectedPincode.find { it.id == pincode.id} != null) {
                    binding.checkView?.setImageDrawable(item.context.resources.getDrawable(R.drawable.ic_gray_tick))
                }else {
                    if (selectedPincode.find { it.id == pincode.id } == null) {
                        binding.checkView?.setImageDrawable(null)
                    } else {
                        binding.checkView?.setImageDrawable(item.context.resources.getDrawable(R.drawable.ic_secondary_tick))
                    }
                }
                item.setOnClickListener {

                    if(disableSelected && preSelectedPincode.find { it.id == pincode.id} != null) {
                        return@setOnClickListener
                    }
                    if(selectedPincode.find { it.id == pincode.id} != null){
                        binding.checkView?.setImageDrawable(null)
                        selectedPincode.remove(pincode)
                    }else {
                        selectedPincode.add(pincode)
                        binding.checkView?.setImageDrawable(item.context.resources.getDrawable(R.drawable.ic_secondary_tick))
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = LayoutStateListBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }

    }

    fun getSelectedPincode() = selectedPincode

    fun setSelectedPincodes(list:List<Pincode>,disableSelected:Boolean = false){
        preSelectedPincode.addAll(list)
        if(!disableSelected) selectedPincode.addAll(list)
        this.disableSelected = disableSelected
    }

    companion object {
        private val PIN_COMPARATOR = object : DiffUtil.ItemCallback<Pincode>() {
            override fun areItemsTheSame(oldItem: Pincode, newItem: Pincode): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Pincode, newItem: Pincode): Boolean =
                newItem == oldItem
        }
    }
}