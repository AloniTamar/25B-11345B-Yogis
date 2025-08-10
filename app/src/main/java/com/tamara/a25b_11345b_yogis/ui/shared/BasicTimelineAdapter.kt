package com.tamara.a25b_11345b_yogis.ui.shared

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.databinding.ItemBasicTimelineBinding

class BasicTimelineAdapter(
    private val items: List<ClassPlanElement>
) : RecyclerView.Adapter<BasicTimelineAdapter.ElementHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElementHolder {
        val binding = ItemBasicTimelineBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ElementHolder(binding)
    }

    override fun onBindViewHolder(holder: ElementHolder, position: Int) {
        holder.bind(items[position])
    }

    class ElementHolder(
        private val binding: ItemBasicTimelineBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(e: ClassPlanElement) {
            when (e) {
                is ClassPlanElement.PoseElement -> {
                    binding.tvDuration.text = "${e.pose.duration} seconds"
                    binding.tvTitle.text    = e.pose.name
                    binding.tvSub.text      = "${e.pose.level}"
                }
                is ClassPlanElement.FlowElement -> {
                    binding.tvDuration.text = "${e.flow.recommendedRounds} rounds"
                    binding.tvTitle.text    = e.flow.flowName
                    binding.tvSub.text      = "${e.flow.level}"
                }
            }
        }
    }
}
