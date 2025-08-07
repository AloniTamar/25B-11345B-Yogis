package com.tamara.a25b_11345b_yogis.ui.shared

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.databinding.ItemAdvancedTimelineBinding
import com.tamara.a25b_11345b_yogis.databinding.ItemFooterTimelineBinding

private const val TYPE_ELEMENT = 0
private const val TYPE_FOOTER  = 1

class TimelineAdapter(
    private val items: List<ClassPlanElement>,
    private val currentOrder: Int = -1,
    private val onFinish: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount() = items.size + 1
    override fun getItemViewType(position: Int) =
        if (position < items.size) TYPE_ELEMENT else TYPE_FOOTER

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == TYPE_ELEMENT) {
            val binding = ItemAdvancedTimelineBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ElementHolder(binding)
        } else {
            val binding = ItemFooterTimelineBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            FooterHolder(binding)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ElementHolder && position < items.size) {
            holder.bind(items[position], position == currentOrder)
        } else if (holder is FooterHolder) {
            holder.binding.btnFinish.setOnClickListener {
                onFinish()
            }
        }
    }

    class ElementHolder(
        private val binding: ItemAdvancedTimelineBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(e: ClassPlanElement, isCurrent: Boolean) {
            // title / subtitle / duration (unchanged)…
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

            // checkbox: checked if it’s the “current” item
            binding.ivCheckbox.setImageResource(
                if (isCurrent) R.drawable.ic_checkbox_checked
                else R.drawable.ic_checkbox_unchecked
            )
        }
    }

    class FooterHolder(
        val binding: ItemFooterTimelineBinding
    ) : RecyclerView.ViewHolder(binding.root)
}
