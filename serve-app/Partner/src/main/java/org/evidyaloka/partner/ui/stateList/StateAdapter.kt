package org.evidyaloka.partner.ui.stateList

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.core.partner.model.State
import org.evidyaloka.partner.R
import org.evidyaloka.partner.databinding.LayoutStateListBinding

class StateAdapter: RecyclerView.Adapter<StateAdapter.ViewHolder>() {

    private var stateList: List<State> = listOf()
    private var selectedState: State? = null
    private var selectedCheckboxView: ImageView? = null
    private var stateSelectListner: IOnStateSelectListner? = null

    interface IOnStateSelectListner {
        fun onStateSelected(state: State)
    }

    inner class ViewHolder(val binding: LayoutStateListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(state:State){
            with(binding){

                name.setText(state.name)
                if(selectedState?.id == state.id){
                    binding.checkView?.setImageDrawable(item.context.resources.getDrawable(R.drawable.ic_secondary_tick))
                    selectedCheckboxView = binding.checkView
                }else{
                    binding.checkView?.setImageDrawable(null)
                }
                binding.checkView?.setImageDrawable(if(selectedState?.id != state.id) null else item.context.resources.getDrawable(R.drawable.ic_secondary_tick))

                item.setOnClickListener {
                    if(selectedState?.id != state.id){
                        selectedCheckboxView?.setImageDrawable(null)
                    }
                    selectedState = state
                    selectedCheckboxView = binding.checkView
                    selectedCheckboxView?.setImageDrawable(item.context.resources.getDrawable(R.drawable.ic_secondary_tick))
                    stateSelectListner?.onStateSelected(state)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = LayoutStateListBinding.inflate(LayoutInflater.from(parent.context),parent, false)
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

    fun getSelectedState() = selectedState

    fun setSateSelectListner(listner:IOnStateSelectListner){
        stateSelectListner = listner
    }

}