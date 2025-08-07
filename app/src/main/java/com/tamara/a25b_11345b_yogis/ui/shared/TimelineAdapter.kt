package com.tamara.a25b_11345b_yogis.ui.shared

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.databinding.ItemAdvancedTimelineBinding

class TimelineAdapter(
    private val items: List<ClassPlanElement>,
    var currentOrder: Int = -1,
    private val onItemClick: (Int) -> Unit // New!
) : RecyclerView.Adapter<TimelineAdapter.ElementHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElementHolder {
        val binding = ItemAdvancedTimelineBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ElementHolder(binding)
    }

    override fun onBindViewHolder(holder: ElementHolder, position: Int) {
        val isChecked = position < currentOrder
        val isNext = position == currentOrder

        holder.bind(items[position], isChecked, isNext)

        holder.binding.ivCheckbox.setOnClickListener {
            if (isNext) onItemClick(position)
        }
        holder.binding.cardEvent.setOnClickListener {
            if (isNext) onItemClick(position)
        }
        holder.binding.ivCheckbox.isEnabled = isNext
        holder.binding.cardEvent.isEnabled = isNext
    }

    class ElementHolder(
        val binding: ItemAdvancedTimelineBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(e: ClassPlanElement, isChecked: Boolean, isNext: Boolean) {
            when (e) {
                is ClassPlanElement.PoseElement -> {
                    binding.tvDuration.text = "${e.pose.duration ?: e.pose.repetitions} ${if (e.pose.duration != null) "min" else "reps"}"
                    binding.tvTitle.text    = e.pose.name
                    binding.tvSub.text      = "Level ${e.pose.level}"
                }
                is ClassPlanElement.FlowElement -> {
                    binding.tvDuration.text = "${e.flow.recommendedRounds} rounds"
                    binding.tvTitle.text    = e.flow.flowName
                    binding.tvSub.text      = "Level ${e.flow.level}"
                }
            }
            if (isNext) {
                // e.g., highlight the border, change card color, show animation, etc.
                binding.cardEvent.strokeColor = ContextCompat.getColor(binding.root.context, R.color.primary_light)
            } else {
                binding.cardEvent.strokeColor = ContextCompat.getColor(binding.root.context, R.color.white)
            }

            binding.ivCheckbox.setImageResource(
                if (isChecked) R.drawable.ic_checkbox_checked
                else R.drawable.ic_checkbox_unchecked
            )
        }
    }
}

