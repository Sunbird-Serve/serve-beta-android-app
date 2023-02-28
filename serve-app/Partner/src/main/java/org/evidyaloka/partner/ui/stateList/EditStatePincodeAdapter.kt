package org.evidyaloka.partner.ui.stateList

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.core.partner.model.Pincode
import org.evidyaloka.core.partner.model.State
import org.evidyaloka.partner.R
import org.evidyaloka.partner.databinding.LayoutEditPincodeBinding
import org.evidyaloka.partner.databinding.LayoutEditStatePincodeBinding
import org.evidyaloka.partner.databinding.LayoutSelectedStatePincodeBinding
import org.evidyaloka.partner.databinding.LayoutStateListBinding

class EditStatePincodeAdapter(private var stateList: List<State>,private val deleteState: (state:State) -> Unit,private val deletePinCode: (pincode:Pincode) -> Unit): RecyclerView.Adapter<EditStatePincodeAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: LayoutEditStatePincodeBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(state:State){
            with(binding){
                stateName?.text = state.name
                ivStateDelete?.setOnClickListener {
                    deleteState(state)
                }
                rcPincode?.apply {
                    adapter = EditPincodeAdapter(state.pincodes,deletePinCode)
                    layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = LayoutEditStatePincodeBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return stateList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stateList[position])
    }


    class EditPincodeAdapter(private val pincodeList: List<Pincode>,private val onDeletePincode: (pincode:Pincode) -> Unit): RecyclerView.Adapter<EditPincodeAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: LayoutEditPincodeBinding): RecyclerView.ViewHolder(binding.root){
            fun bind(pincode:Pincode){
                with(binding){
                    pincodeName?.text = pincode.taluk +" - "+pincode.pincode
                    ivPincodeDelete?.setOnClickListener {
                        onDeletePincode(pincode)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var binding = LayoutEditPincodeBinding.inflate(LayoutInflater.from(parent.context),parent, false)
            return ViewHolder(binding)
        }
        override fun getItemCount(): Int {
            return pincodeList.size
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(pincodeList[position])
        }
    }

}