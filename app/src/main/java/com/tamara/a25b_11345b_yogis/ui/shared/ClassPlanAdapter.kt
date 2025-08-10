package com.tamara.a25b_11345b_yogis.ui.shared

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.databinding.ItemBasicTimelineBinding

class ClassPlanAdapter(
    private val elements: List<ClassPlanElement>,
    private val highlightPosition: Int? = null
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

        val card = holder.binding.root as? MaterialCardView
        if (highlightPosition == position) {
            card?.setCardBackgroundColor(
                ContextCompat.getColor(holder.binding.root.context, R.color.primary_light)
            )
        } else {
            card?.setCardBackgroundColor(
                ContextCompat.getColor(holder.binding.root.context, R.color.white)
            )
        }
    }

    inner class TimelineViewHolder(
        val binding: ItemBasicTimelineBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(element: ClassPlanElement) {
            when (element) {
                is ClassPlanElement.PoseElement -> {
                    binding.tvTitle.text = element.pose.name
                    binding.tvSub.text = "Level ${element.pose.level}"
                    binding.tvDuration.text =
                        element.pose.duration?.let { "$it seconds" } ?: ""
                }
                is ClassPlanElement.FlowElement -> {
                    binding.tvTitle.text = element.flow.flowName
                    binding.tvSub.text = "Level ${element.flow.level}"
                    binding.tvDuration.text =
                        "${element.flow.recommendedRounds} rounds"
                }
            }
        }
    }
}
