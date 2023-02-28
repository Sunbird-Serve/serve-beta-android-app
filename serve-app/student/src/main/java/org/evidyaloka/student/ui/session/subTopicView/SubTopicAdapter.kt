package org.evidyaloka.student.ui.session.subTopicView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.student.databinding.LayoutSubtopicListBinding

/**
 * @author Madhankumar
 * created on 29-03-2021
 *
 */
class SubTopicAdapter:RecyclerView.Adapter<SubTopicAdapter.ViewHolder>() {
    private var subtopics: List<SubTopic> = listOf()

    private var onItemClickListener: OnItemClickListner? = null


    fun setOnItemClickListener(listner: OnItemClickListner) {
        onItemClickListener = listner
    }

    inner class ViewHolder(val bindView: LayoutSubtopicListBinding): RecyclerView.ViewHolder(bindView.root) {
        fun bind(subtopic: SubTopic) {
            bindView.tvTitle.text = subtopic.subtopicName
            bindView.root.setOnClickListener {
                //TODO Navigate to RTC View
                onItemClickListener?.OnItemClick(subtopic)
            }
        }
    }



   fun setItem(list: List<SubTopic>){
       subtopics = list
       notifyDataSetChanged()
   }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val subtopicView = LayoutSubtopicListBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return ViewHolder(subtopicView)
    }

    override fun getItemCount(): Int {
        return subtopics.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subtopics[position])
    }
}