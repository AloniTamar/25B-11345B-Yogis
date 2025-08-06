package com.tamara.a25b_11345b_yogis.ui.shared

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.databinding.ItemBasicTimelineBinding

class ClassPlanAdapter(
    private val elements: List<ClassPlanElement>
) : RecyclerView.Adapter<ClassPlanAdapter.TimelineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ItemBasicTimelineBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TimelineViewHolder(binding)
    }

    override fun getItemCount(): Int = elements.size

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(elements[position])
    }

    inner class TimelineViewHolder(
        private val binding: ItemBasicTimelineBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(element: ClassPlanElement) {
            when (element) {
                is ClassPlanElement.PoseElement -> {
                    binding.tvTitle.text = element.pose.name
                    binding.tvSub.text = element.pose.level.let { "Level $it" }
                    binding.tvDuration.text = element.pose.duration?.let { "$it min" } ?: ""
                }
                is ClassPlanElement.FlowElement -> {
                    binding.tvTitle.text = element.flow.flowName
                    binding.tvSub.text = element.flow.level.let { "Level $it" }
                    binding.tvDuration.text = "${element.flow.recommendedRounds} rounds"
                }
            }
        }
    }
}
